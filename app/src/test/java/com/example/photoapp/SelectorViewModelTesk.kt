package com.example.photoapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.photoapp.TestSVMData.odbiorcy
import com.example.photoapp.TestSVMData.produkty
import com.example.photoapp.TestSVMData.sprzedawcy
import com.example.photoapp.features.faktura.data.faktura.FakturaRepository
import com.example.photoapp.features.odbiorca.data.Odbiorca
import com.example.photoapp.features.odbiorca.data.OdbiorcaRepository
import com.example.photoapp.features.produkt.data.Produkt
import com.example.photoapp.features.selector.presentation.selector.SelectorViewModel
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
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.intArrayOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert


private object TestSVMData {
     val produkty = listOf(
        Produkt(id = "1", uzytkownikId = "", nazwaProduktu = "Produkt A", cenaNetto = "10.0", stawkaVat = "23.0"),
        Produkt(id = "2", uzytkownikId = "", nazwaProduktu = "Produkt B", cenaNetto = "20.0", stawkaVat = "8.0")
    )

     val odbiorcy = listOf(
        Odbiorca(id = "1", uzytkownikId = "", nazwa = "Odbiorca A"),
        Odbiorca(id = "2", uzytkownikId = "", nazwa = "Odbiorca B")
    )

     val sprzedawcy = listOf(
        Sprzedawca(id = "1", uzytkownikId = "", nazwa = "Sprzedawca A"),
        Sprzedawca(id = "2", uzytkownikId = "", nazwa = "Sprzedawca B")
    )
}

@OptIn(ExperimentalCoroutinesApi::class)
@Config(sdk = [33])
@RunWith(RobolectricTestRunner::class)
class SelectorViewModelTesk {

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

    private val fakturaRepository: FakturaRepository = mockk(relaxed = true)
    private val odbiorcaRepository: OdbiorcaRepository = mockk(relaxed = true)
    private val sprzedawcaRepository: SprzedawcaRepository = mockk(relaxed = true)
    private lateinit var viewModel: SelectorViewModel

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = SelectorViewModel(
            fakturaRepository=fakturaRepository,
            odbiorcaRepository=odbiorcaRepository,
            sprzedawcaRepository=sprzedawcaRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.setMain(Dispatchers.Default)
    }

    @Test
    fun `getProdukty should update allProdukty state`() = runTest {
        // Given
        coEvery { fakturaRepository.getAllProdukty() } returns produkty

        // When
        viewModel.getProdukty()
        advanceTimeBy(2000)
        advanceUntilIdle()
        // Then
        viewModel.allProdukty.test {
            val value = awaitItem()
            Assert.assertEquals(produkty, value)
        }
    }

    @Test
    fun `getOdbiorcy should update allOdbiorcy state`() = runTest {
        // Given
        coEvery { odbiorcaRepository.getAllOdbiorcy() } returns odbiorcy

        // When
        viewModel.getOdbiorcy()
        advanceTimeBy(2000)
        advanceUntilIdle()
        // Then
        viewModel.allOdbiorcy.test {
            val value = awaitItem()
            Assert.assertEquals(odbiorcy, value)
        }
    }

    @Test
    fun `getSprzedawcy should update allSprzedawcy state`() = runTest {
        // Given
        coEvery { sprzedawcaRepository.getAll() } returns sprzedawcy

        // When
        viewModel.getSprzedawcy()
        advanceTimeBy(2000)
        advanceUntilIdle()
        // Then
        viewModel.allSprzedawcy.test {
            val value = awaitItem()
            Assert.assertEquals(sprzedawcy, value)
        }
    }

    @Test
    fun `updateLists should update all 3 states`() = runTest {
        // Given
        coEvery { fakturaRepository.getAllProdukty() } returns produkty
        coEvery { odbiorcaRepository.getAllOdbiorcy() } returns odbiorcy
        coEvery { sprzedawcaRepository.getAll() } returns sprzedawcy

        // When
        viewModel.updateLists()
        advanceTimeBy(2000)
        advanceUntilIdle()
        // Then
        viewModel.allProdukty.test {
            val value = awaitItem()
            Assert.assertEquals(produkty, value)
        }
        viewModel.allOdbiorcy.test {
            val value = awaitItem()
            Assert.assertEquals(odbiorcy, value)
        }
        viewModel.allSprzedawcy.test {
            val value = awaitItem()
            Assert.assertEquals(sprzedawcy, value)
        }
    }
}