package com.example.photoapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.photoapp.features.faktura.data.faktura.FakturaRepository
import com.example.photoapp.features.produkt.data.Produkt
import com.example.photoapp.features.selector.presentation.selector.produkt.details.ProduktDetailsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
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

private object TestPDVMData {
    val produkt = Produkt(id = "1", uzytkownikId = "", nazwaProduktu = "Test Produkt", cenaNetto = "100.0", stawkaVat = "23.0")
}

@OptIn(ExperimentalCoroutinesApi::class)
@Config(sdk = [33])
@RunWith(RobolectricTestRunner::class)
class ProduktDetailsViewModelTest {

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

    private val produktRepository: FakturaRepository = mockk(relaxed = true)
    private lateinit var viewModel: ProduktDetailsViewModel

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = ProduktDetailsViewModel(
            produktRepository=produktRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.setMain(Dispatchers.Default)
    }

    @Test
    fun `loadProdukt should load and update both actual and edited produkt`() = runTest {
        // Given
        coEvery { produktRepository.getProduktById(TestPDVMData.produkt.id) } returns TestPDVMData.produkt

        // When
        viewModel.loadProdukt(TestPDVMData.produkt)
        advanceUntilIdle()

        // Then
        viewModel.actualProdukt.test {
            val value = awaitItem()
            Assert.assertEquals(TestPDVMData.produkt, value)
        }

        viewModel.editedProdukt.test {
            val value = awaitItem()
            Assert.assertEquals(TestPDVMData.produkt, value)
        }
    }

    @Test
    fun `editingSuccess should call updateAllEditedToDB and reload produkt`() = runTest {
        // Given
        coEvery { produktRepository.getProduktById(any()) } returns TestPDVMData.produkt
        coEvery { produktRepository.updateProdukt(any()) } just Runs

        viewModel.setProdukt(TestPDVMData.produkt)
        viewModel.updateEditedProduktTemp(TestPDVMData.produkt) {}

        // When
        viewModel.editingSuccess()
        advanceUntilIdle()

        // Then
        Assert.assertEquals(TestPDVMData.produkt, viewModel.actualProdukt.value)
    }

    @Test
    fun `editingFailed should reload actualProdukt`() = runTest {
        // Given
        coEvery { produktRepository.getProduktById(TestPDVMData.produkt.id) } returns TestPDVMData.produkt
        viewModel.setProdukt(TestPDVMData.produkt)

        // When
        viewModel.editingFailed()
        advanceUntilIdle()

        // Then
        Assert.assertEquals(TestPDVMData.produkt, viewModel.actualProdukt.value)
    }

    @Test
    fun `updateEditedProduktTemp should update edited produkt and call callback`() = runTest {
        val latch = CountDownLatch(1)
        // Given
        var callbackCalled = false

        // When
        viewModel.updateEditedProduktTemp(TestPDVMData.produkt) {
            callbackCalled = true
            latch.countDown()
        }
        latch.await()

        // Then
        Assert.assertEquals(TestPDVMData.produkt, viewModel.editedProdukt.value)
        Assert.assertTrue(callbackCalled)
    }

    @Test
    fun `updateAllEditedToDB should updateProduktAndRelativeData when cenaNetto or stawkaVat change`() = runTest {
        val latch = CountDownLatch(1)
        // Given
        val updatedProdukt = TestPDVMData.produkt.copy(cenaNetto = "200.0")
        viewModel.setProdukt(TestPDVMData.produkt)
        viewModel.updateEditedProduktTemp(updatedProdukt) {}

        coEvery { produktRepository.updateProduktAndRelativeData(updatedProdukt) } just Runs

        var callbackProdukt: Produkt? = null

        // When
        viewModel.updateAllEditedToDB {
            callbackProdukt = it
            latch.countDown()
        }
        latch.await()

        // Then
        coVerify { produktRepository.updateProduktAndRelativeData(updatedProdukt) }
        Assert.assertEquals(updatedProdukt, callbackProdukt)
    }

    @Test
    fun `updateAllEditedToDB should call updateProdukt when no change in cenaNetto or stawkaVat`() = runTest {
        val latch = CountDownLatch(1)
        // Given
        viewModel.setProdukt(TestPDVMData.produkt)
        viewModel.updateEditedProduktTemp(TestPDVMData.produkt) {}

        coEvery { produktRepository.updateProdukt(TestPDVMData.produkt) } just Runs

        var callbackProdukt: Produkt? = null

        // When
        viewModel.updateAllEditedToDB {
            callbackProdukt = it
            latch.countDown()
        }
        latch.await()

        // Then
        verify { produktRepository.updateProdukt(TestPDVMData.produkt) }
        Assert.assertEquals(TestPDVMData.produkt, callbackProdukt)
    }

    @Test
    fun `getProdukt should fetch by ID and invoke callback`() = runTest {
        val latch = CountDownLatch(1)
        // Given
        coEvery { produktRepository.getProduktById(TestPDVMData.produkt.id) } returns TestPDVMData.produkt

        var result: Produkt? = null

        // When
        viewModel.getProdukt(TestPDVMData.produkt) {
            result = it
            latch.countDown()
        }
        latch.await()

        // Then
        Assert.assertEquals(TestPDVMData.produkt, result)
    }

    @Test
    fun `setProdukt should update actual produkt`() = runTest {
        // When
        viewModel.setProdukt(TestPDVMData.produkt)

        // Then
        Assert.assertEquals(TestPDVMData.produkt, viewModel.actualProdukt.value)
    }
}