package com.example.photoapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.photoapp.features.odbiorca.data.Odbiorca
import com.example.photoapp.features.odbiorca.data.OdbiorcaRepository
import com.example.photoapp.features.selector.presentation.selector.odbiorca.details.OdbiorcaDetailsViewModel
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
import kotlinx.coroutines.test.runTest
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
import java.util.concurrent.CountDownLatch
import kotlin.intArrayOf

private object TestODVMData {
    val odbiorca = Odbiorca(id = "1", uzytkownikId = "", nazwa = "Test Name", /* fill fields */)
}

@OptIn(ExperimentalCoroutinesApi::class)
@Config(sdk = [33])
@RunWith(RobolectricTestRunner::class)
class OdbiorcaDetailsViewModelTest {

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

    private val odbiorcaRepository: OdbiorcaRepository = mockk(relaxed = true)
    private lateinit var viewModel: OdbiorcaDetailsViewModel

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = OdbiorcaDetailsViewModel(
            odbiorcaRepository=odbiorcaRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.setMain(Dispatchers.Default)
    }

    @Test
    fun `loadSprzedawca should load and update both actual and edited sprzedawca`() = runTest {
        // Given
        coEvery { odbiorcaRepository.getById(TestODVMData.odbiorca.id) } returns TestODVMData.odbiorca

        // When
        viewModel.loadProducts(TestODVMData.odbiorca)
        advanceUntilIdle()

        // Then
        viewModel.actualOdbiorca.test {
            val value = awaitItem()
            Assert.assertEquals(TestODVMData.odbiorca, value)
        }

        viewModel.editedOdbiorca.test {
            val value = awaitItem()
            Assert.assertEquals(TestODVMData.odbiorca, value)
        }
    }

    @Test
    fun `editingSuccess should call updateAllEditedToDB and reload sprzedawca`() = runTest {
        // Given
        coEvery { odbiorcaRepository.upsertOdbiorcaSmart(any()) } returns "99"
        coEvery { odbiorcaRepository.getById(any()) } returns TestODVMData.odbiorca

        viewModel.setOdbiorca(TestODVMData.odbiorca)
        viewModel.updateEditedOdbiorcaTemp(TestODVMData.odbiorca) {}

        // When
        viewModel.editingSuccess()
        advanceUntilIdle()

        // Then
        Assert.assertEquals(TestODVMData.odbiorca, viewModel.actualOdbiorca.value)
    }

    @Test
    fun `editingFailed should reload actualSprzedawca`() = runTest {
        // Given
        viewModel.setOdbiorca(TestODVMData.odbiorca)
        coEvery { odbiorcaRepository.getById(TestODVMData.odbiorca.id) } returns TestODVMData.odbiorca

        // When
        viewModel.editingFailed()
        advanceUntilIdle()

        // Then
        Assert.assertEquals(TestODVMData.odbiorca, viewModel.actualOdbiorca.value)
    }

    @Test
    fun `updateEditedSprzedawcaTemp should update edited sprzedawca and call callback`() = runTest {
        val latch = CountDownLatch(1)
        // Given
        var callbackCalled = false

        // When
        viewModel.updateEditedOdbiorcaTemp(TestODVMData.odbiorca) {
            callbackCalled = true
            latch.countDown()
        }
        latch.await()

        // Then
        Assert.assertEquals(TestODVMData.odbiorca, viewModel.editedOdbiorca.value)
        Assert.assertTrue(callbackCalled)
    }

    @Test
    fun `updateAllEditedToDB should upsert and call callback with updated sprzedawca`() = runTest {
        val latch = CountDownLatch(1)
        // Given
        val updatedId = "88"
        val edited = TestODVMData.odbiorca.copy(id = "1")
        viewModel.updateEditedOdbiorcaTemp(edited) {}

        coEvery { odbiorcaRepository.upsertOdbiorcaSmart(any()) } returns updatedId

        var callbackResult: Odbiorca? = null

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
        coEvery { odbiorcaRepository.getById(TestODVMData.odbiorca.id) } returns TestODVMData.odbiorca

        var result: Odbiorca? = null

        // When
        viewModel.getOdbiorca(TestODVMData.odbiorca) {
            result = it
            latch.countDown()
        }
        latch.await()

        // Then
        Assert.assertEquals(TestODVMData.odbiorca, result)
    }

    @Test
    fun `setSprzedawca should update actual sprzedawca`() = runTest {
        // When
        viewModel.setOdbiorca(TestODVMData.odbiorca)

        // Then
        Assert.assertEquals(TestODVMData.odbiorca, viewModel.actualOdbiorca.value)
    }
}