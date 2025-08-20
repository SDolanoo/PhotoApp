package com.example.photoapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.photoapp.features.selector.presentation.selector.sprzedawca.details.SprzedawcaDetailsViewModel
import com.example.photoapp.features.sprzedawca.data.Sprzedawca
import com.example.photoapp.features.sprzedawca.data.SprzedawcaRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.intArrayOf
import kotlinx.coroutines.test.runTest
import java.util.concurrent.CountDownLatch

private object TestSDVMData {
    val sprzedawca = Sprzedawca(id = "1", uzytkownikId = "", nazwa = "Test Name", /* fill fields */)
}

@OptIn(ExperimentalCoroutinesApi::class)
@Config(sdk = [33])
@RunWith(RobolectricTestRunner::class)
class SprzedawcaDetailsViewModelTest {

    companion object { // mockuje firebase usera przed włączeniem testu, poniważ klasy z modelu mają domyślną funkcję pozyskania ID
        @JvmStatic
        @BeforeClass
        fun globalFirebaseMock() {
            mockkStatic(FirebaseAuth::class)

            val fakeUser = mockk<FirebaseUser>()
            every { fakeUser.uid } returns "test-user-id"
            every { FirebaseAuth.getInstance().currentUser } returns fakeUser
        }

        @JvmStatic
        @AfterClass
        fun removeFirebaseMock() {
            unmockkStatic(FirebaseAuth::class) // zawsze unmock
        }
    }

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val sprzedawcaRepository: SprzedawcaRepository = mockk(relaxed = true)
    private lateinit var viewModel: SprzedawcaDetailsViewModel

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = SprzedawcaDetailsViewModel(
            sprzedawcaRepository=sprzedawcaRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.setMain(Dispatchers.Default)
    }

    @Test
    fun `loadSprzedawca should load and update both actual and edited sprzedawca`() = runTest {
        // Given
        coEvery { sprzedawcaRepository.getById(TestSDVMData.sprzedawca.id) } returns TestSDVMData.sprzedawca

        // When
        viewModel.loadSprzedawca(TestSDVMData.sprzedawca)
        advanceUntilIdle()

        // Then
        viewModel.actualSprzedawca.test {
            val value = awaitItem()
            Assert.assertEquals(TestSDVMData.sprzedawca, value)
        }

        viewModel.editedSprzedawca.test {
            val value = awaitItem()
            Assert.assertEquals(TestSDVMData.sprzedawca, value)
        }
    }

    @Test
    fun `editingSuccess should call updateAllEditedToDB and reload sprzedawca`() = runTest {
        // Given
        coEvery { sprzedawcaRepository.upsertSprzedawcaSmart(any()) } returns "99"
        coEvery { sprzedawcaRepository.getById(any()) } returns TestSDVMData.sprzedawca

        viewModel.setSprzedawca(TestSDVMData.sprzedawca)
        viewModel.updateEditedSprzedawcaTemp(TestSDVMData.sprzedawca) {}

        // When
        viewModel.editingSuccess()
        advanceUntilIdle()

        // Then
        Assert.assertEquals(TestSDVMData.sprzedawca, viewModel.actualSprzedawca.value)
    }

    @Test
    fun `editingFailed should reload actualSprzedawca`() = runTest {
        // Given
        viewModel.setSprzedawca(TestSDVMData.sprzedawca)
        coEvery { sprzedawcaRepository.getById(TestSDVMData.sprzedawca.id) } returns TestSDVMData.sprzedawca

        // When
        viewModel.editingFailed()
        advanceUntilIdle()

        // Then
        Assert.assertEquals(TestSDVMData.sprzedawca, viewModel.actualSprzedawca.value)
    }

    @Test
    fun `updateEditedSprzedawcaTemp should update edited sprzedawca and call callback`() = runTest {
        val latch = CountDownLatch(1)
        // Given
        var callbackCalled = false

        // When
        viewModel.updateEditedSprzedawcaTemp(TestSDVMData.sprzedawca) {
            callbackCalled = true
            latch.countDown()
        }
        latch.await()

        // Then
        Assert.assertEquals(TestSDVMData.sprzedawca, viewModel.editedSprzedawca.value)
        Assert.assertTrue(callbackCalled)
    }

    @Test
    fun `updateAllEditedToDB should upsert and call callback with updated sprzedawca`() = runTest {
        val latch = CountDownLatch(1)
        // Given
        val updatedId = "88"
        val edited = TestSDVMData.sprzedawca.copy(id = "1")
        viewModel.updateEditedSprzedawcaTemp(edited) {}

        coEvery { sprzedawcaRepository.upsertSprzedawcaSmart(any()) } returns updatedId

        var callbackResult: Sprzedawca? = null

        // When
        viewModel.updateAllEditedToDB {
            callbackResult = it
            latch.countDown()
        }
        latch.await()

        // Then
        Assert.assertEquals(updatedId, callbackResult?.id)
    }

    @Test
    fun `getSprzedawca should fetch by ID and invoke callback`() = runTest {
        val latch = CountDownLatch(1)
        // Given
        coEvery { sprzedawcaRepository.getById(TestSDVMData.sprzedawca.id) } returns TestSDVMData.sprzedawca

        var result: Sprzedawca? = null

        // When
        viewModel.getSprzedawca(TestSDVMData.sprzedawca) {
            result = it
            latch.countDown()
        }
        latch.await()

        // Then
        Assert.assertEquals(TestSDVMData.sprzedawca, result)
    }

    @Test
    fun `setSprzedawca should update actual sprzedawca`() = runTest {
        // When
        viewModel.setSprzedawca(TestSDVMData.sprzedawca)

        // Then
        Assert.assertEquals(TestSDVMData.sprzedawca, viewModel.actualSprzedawca.value)
    }
}