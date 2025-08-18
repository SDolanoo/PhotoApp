package com.example.photoapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.photoapp.features.faktura.data.faktura.Faktura
import com.example.photoapp.features.faktura.data.faktura.FakturaRepository
import com.example.photoapp.features.faktura.presentation.details.FakturaDetailsViewModel
import com.example.photoapp.features.faktura.presentation.details.ProduktFakturaZProduktem
import com.example.photoapp.features.odbiorca.data.Odbiorca
import com.example.photoapp.features.odbiorca.data.OdbiorcaRepository
import com.example.photoapp.features.produkt.data.Produkt
import com.example.photoapp.features.produkt.data.ProduktFaktura
import com.example.photoapp.features.sprzedawca.data.Sprzedawca
import com.example.photoapp.features.sprzedawca.data.SprzedawcaRepository
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
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
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
import java.util.concurrent.CountDownLatch

private object TestFDSVMData {
    var faktura = Faktura.default().copy(id = "1", odbiorcaId = "2", sprzedawcaId = "3")

    var odbiorca = Odbiorca.empty().copy(id = "2")

    var sprzedawca = Sprzedawca.empty().copy(id = "3")

    var produktFaktura = ProduktFaktura.default().copy(id = "4", produktId = "5", fakturaId = "1")

    var produkt = Produkt.default().copy(id = "5")

    var pfzp = ProduktFakturaZProduktem(produkt = produkt , produktFaktura = produktFaktura)
    var pfzp2 = ProduktFakturaZProduktem(produkt = produkt.copy(id = "6") , produktFaktura = produktFaktura.copy(id = "7"))
    var pfzpList = listOf(pfzp, pfzp2)
}

@OptIn(ExperimentalCoroutinesApi::class)
@Config(sdk = [33])
@RunWith(RobolectricTestRunner::class)
class FakturaDetailsScreenViewModelTest {

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
    private val sprzedawcaRepository: SprzedawcaRepository = mockk(relaxed = true)
    private val odbiorcaRepository: OdbiorcaRepository = mockk(relaxed = true)
    private lateinit var viewModel: FakturaDetailsViewModel

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = FakturaDetailsViewModel(
            repository=fakturaRepository,
            sprzedawcaRepository=sprzedawcaRepository,
            odbiorcaRepository=odbiorcaRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.setMain(Dispatchers.Default)
    }

    @Test
    fun `loadProducts - Objects are loaded properly`() = runTest {
        // Given
        val faktura = TestFDSVMData.faktura
        every { fakturaRepository.getProduktyFakturaForFaktura(faktura) } returns listOf(TestFDSVMData.produktFaktura)
        coEvery { sprzedawcaRepository.getById(faktura.sprzedawcaId) } returns TestFDSVMData.sprzedawca
        coEvery { odbiorcaRepository.getById(faktura.odbiorcaId) } returns TestFDSVMData.odbiorca
        coEvery { fakturaRepository.getProduktForProduktFaktura(TestFDSVMData.produktFaktura) } returns TestFDSVMData.produkt

        // When
        viewModel.loadProducts(faktura)

        // Then
        viewModel.actualFaktura.test {
            val value = awaitItem()
            Assert.assertEquals("1", value.id)
            Assert.assertEquals("3", value.sprzedawcaId)
            cancelAndIgnoreRemainingEvents()
        }

        viewModel.actualProdukty.test {
            val value = awaitItem()
            Assert.assertEquals("5", value.first().produkt.id)
            Assert.assertEquals("4", value.first().produktFaktura.id)
            cancelAndIgnoreRemainingEvents()
        }

        viewModel.actualSprzedawca.test {
            val value = awaitItem()
            Assert.assertEquals("3", value.id)
            cancelAndIgnoreRemainingEvents()
        }

        viewModel.actualOdbiorca.test {
            val value = awaitItem()
            Assert.assertEquals("2", value.id)
            cancelAndIgnoreRemainingEvents()
        }

    }

    @Test
    fun `getFakturaByID returns Faktura`() = runTest {
        // Given
        val faktura = TestFDSVMData.faktura
        every { fakturaRepository.getFakturaByID(faktura.id) } returns faktura

        // When
        var result = CompletableDeferred<Faktura>()
        viewModel.getFakturaByID(faktura.id) { faktura ->
            result.complete(faktura)
        }
        advanceUntilIdle()

        // Then
        val assertResult = result.await()
        Assert.assertEquals(faktura, assertResult)
    }

    @Test
    fun `updateEditedProductTemp updates editedProduct`() = runTest {
        // Given
        val produktFakturaZProduktem = TestFDSVMData.pfzp
        val produkToUpdate = TestFDSVMData.produkt.copy(nazwaProduktu = "nowa nazwa")
        // Set ViewModel state
        viewModel.setTestData(editedProdukty = listOf(produktFakturaZProduktem))
        val toUpdate = TestFDSVMData.pfzp.copy(produkt = produkToUpdate)

        // When
        viewModel.updateEditedProductTemp(0, toUpdate) {

        }
        advanceUntilIdle()

        // Then
        viewModel.editedProdukty.test {
            val value = awaitItem()
            Assert.assertEquals("nowa nazwa", value[0].produkt.nazwaProduktu)
        }
    }

    @Test
    fun `deleteEditedProduct deletes Product`() = runTest {
        // Given
        val pfzpList = TestFDSVMData.pfzpList
        val pfzp = TestFDSVMData.pfzp
        viewModel.setTestData(editedProdukty = pfzpList)

        // When
        viewModel.deleteEditedProduct(pfzp) {

        }
        advanceUntilIdle()

        // Then
        viewModel.editedProdukty.test {
            val value = awaitItem()
            Assert.assertEquals(listOf(TestFDSVMData.pfzp2), value)
            Assert.assertEquals(1, value.size)
        }
    }

    @Test
    fun `updateAllEditedToDB inserts new produktFaktura and updates faktura`() = runTest {
        // Given
        val f = TestFDSVMData.faktura
        val sprzedawca = TestFDSVMData.sprzedawca
        val odbiorca = TestFDSVMData.odbiorca
        val produktFaktura = TestFDSVMData.produktFaktura.copy(id = "11")
        val produkt = TestFDSVMData.produkt.copy(id = "11")

        val pfzp = ProduktFakturaZProduktem(produktFaktura, produkt)

        // Set ViewModel state
        viewModel.setTestData(editedFaktura = f, editedProdukty = listOf(pfzp),
            editedSprzedawca = sprzedawca, editedOdbiorca = odbiorca)

        coEvery { fakturaRepository.insertProduktFaktura(any()) } returns "11"
        coEvery { fakturaRepository.getProduktyFakturaForFaktura(any()) } returns listOf(produktFaktura)
        coEvery { sprzedawcaRepository.upsertSprzedawcaSmart(any()) } returns sprzedawca.id
        coEvery { odbiorcaRepository.upsertOdbiorcaSmart(any()) } returns odbiorca.id
        coEvery { fakturaRepository.updateFaktura(any()) } just Runs

        var callbackResult: Faktura? = null

        // When
        viewModel.updateAllEditedToDB { updated ->
            callbackResult = updated
        }
        advanceUntilIdle()

        // Then
        coVerify { fakturaRepository.insertProduktFaktura(any()) }
        coVerify { fakturaRepository.updateFaktura(any()) }
        coVerify { sprzedawcaRepository.upsertSprzedawcaSmart(sprzedawca) }
        coVerify { odbiorcaRepository.upsertOdbiorcaSmart(odbiorca) }

        Assert.assertNotNull(callbackResult)
        Assert.assertEquals(sprzedawca.id, callbackResult!!.sprzedawcaId)
        Assert.assertEquals(odbiorca.id, callbackResult.odbiorcaId)
    }

    @Test
    fun `addOneProductToEdited adds new product if not exists`() = runTest {
        val latch = CountDownLatch(1)
        // Given
        Assert.assertEquals(0, viewModel.editedProdukty.value.size)

        // When
        viewModel.addOneProductToEdited {
            latch.countDown()
        }

        latch.await()

        // Then
        viewModel.editedProdukty.test {
            val value = awaitItem()
            Assert.assertEquals(1, value.size)
            Assert.assertEquals("1", value.first().produktFaktura.ilosc) // domyślna ilość
        }
    }

    @Test
    fun `addOneProductToEdited increments ilosc if same new product exists`() = runTest {
        val latch = CountDownLatch(1)

        // Given
        val existing = ProduktFakturaZProduktem(
            produktFaktura = TestFDSVMData.produktFaktura.copy(ilosc = "2"),
            produkt = Produkt.default()
        )

        // Then
        viewModel.setTestData(editedProdukty = listOf(existing))

        viewModel.addOneProductToEdited {
            latch.countDown()
        }

        latch.await()

        // Then
        viewModel.editedProdukty.test {
            val value = awaitItem()
            Assert.assertEquals(2, value.size)
            Assert.assertEquals("3", value[1].produktFaktura.ilosc) // było 2, +1
        }
    }

    @Test
    fun `addOneProductToEdited adds new product`() = runTest {
        // Given
        val existing = ProduktFakturaZProduktem(
            produktFaktura = TestFDSVMData.produktFaktura.copy(ilosc = "2"),
            produkt = TestFDSVMData.produkt.copy(nazwaProduktu = "ABC")
        )

        viewModel.setTestData(editedProdukty = listOf(existing))

        // Override next product to mieć taką samą nazwę
        viewModel.addOneProductToEdited()
        advanceUntilIdle()

        // Then
        viewModel.editedProdukty.test {
            val value = awaitItem()
            Assert.assertEquals(2, value.size)
            Assert.assertEquals("1", value[1].produktFaktura.ilosc) // było 2, +1
        }
    }

    @Test
    fun `setFaktura updates actualFaktura value`() = runTest {
        // Given
        val faktura = TestFDSVMData.faktura.copy(id = "f123", numerFaktury = "FV-001")

        // When
        viewModel.setFaktura(faktura)
        advanceUntilIdle()

        // Then
        viewModel.actualFaktura.test {
            val value = awaitItem()
            Assert.assertEquals("f123", value.id)
            Assert.assertEquals("FV-001", value.numerFaktury)
        }
    }


    @Test
    fun `editEditedSprzedawca updates editedSprzedawca`() = runTest {
        // Given
        val newSprzedawca = TestFDSVMData.sprzedawca.copy(id = "s1", nazwa = "Nowy Sprzedawca")

        // When
        viewModel.editEditedSprzedawca(newSprzedawca)
        advanceUntilIdle()

        // Then
        viewModel.editedSprzedawca.test {
            val value = awaitItem()
            Assert.assertEquals("s1", value.id)
            Assert.assertEquals("Nowy Sprzedawca", value.nazwa)
        }
    }

    @Test
    fun `replaceEditedSprzedawca updates editedSprzedawca and faktura ID`() = runTest {
        // Given
        val initialFaktura = TestFDSVMData.faktura.copy(id = "f1", sprzedawcaId = "old-id")
        val newSprzedawca = TestFDSVMData.sprzedawca.copy(id = "new-id", nazwa = "Sprzedawca X")
        viewModel.setTestData(editedFaktura = initialFaktura)

        var callbackCalled = false

        // When
        viewModel.replaceEditedSprzedawca(newSprzedawca) {
            callbackCalled = true
        }
        advanceUntilIdle()

        // Then
        viewModel.editedSprzedawca.test {
            val value = awaitItem()
            Assert.assertEquals("new-id", value.id)
            Assert.assertEquals("Sprzedawca X", value.nazwa)
        }

        viewModel.editedFaktura.test {
            val value = awaitItem()
            Assert.assertEquals("new-id", value.sprzedawcaId)
        }

        Assert.assertTrue(callbackCalled)
    }

    @Test
    fun `editEditedOdbiorca updates editedOdbiorca`() = runTest {
        // Given
        val odbiorca = TestFDSVMData.odbiorca.copy(id = "o1", nazwa = "Firma X")

        // When
        viewModel.editEditedOdbiorca(odbiorca)
        advanceUntilIdle()

        // Then
        viewModel.editedOdbiorca.test {
            val value = awaitItem()
            Assert.assertEquals("o1", value.id)
            Assert.assertEquals("Firma X", value.nazwa)
        }

    }

    @Test
    fun `replaceEditedOdbiorca updates editedOdbiorca and faktura odbiorcaId`() = runTest {
        // Given
        val initialFaktura = TestFDSVMData.faktura.copy(id = "f1", odbiorcaId = "old-id")
        val newOdbiorca = TestFDSVMData.odbiorca.copy(id = "new-id", nazwa = "Firma Nowa")
        viewModel.setTestData(editedFaktura = initialFaktura)

        var callbackCalled = false

        // When
        viewModel.replaceEditedOdbiorca(newOdbiorca) {
            callbackCalled = true
        }
        advanceUntilIdle()

        // Then
        viewModel.editedOdbiorca.test {
            val value = awaitItem()
            Assert.assertEquals("new-id", value.id)
            Assert.assertEquals("Firma Nowa", value.nazwa)
        }

        viewModel.editedFaktura.test {
            val value = awaitItem()
            Assert.assertEquals("new-id", value.odbiorcaId)
        }
        Assert.assertTrue(callbackCalled)
    }

    @Test
    fun `getListOfSprzedawca returns sorted and distinct list`() = runTest {
        // Given
        val s1 = Sprzedawca(id = "2", nazwa = "Firma A")
        val s2 = Sprzedawca(id = "1", nazwa = "Firma B")
        val duplicateS3 = Sprzedawca(id = "3", nazwa = "firma a") // duplikat nazwy, inny ID

        coEvery { sprzedawcaRepository.getAll() } returns listOf(s1, s2, duplicateS3)

        // When
        val result = viewModel.getListOfSprzedacwa()

        // Then
        Assert.assertEquals(2, result.size)
        Assert.assertEquals("1", result[0].id) // Firma B
        Assert.assertEquals("2", result[1].id) // Firma A (z id = 2, nie 3) ten co ma mniejsze id
    }

    @Test
    fun `getListOfOdbiorca returns sorted and distinct list`() = runTest {
        // Given
        val o1 = Odbiorca(id = "2", nazwa = "Klient A")
        val o2 = Odbiorca(id = "1", nazwa = "Klient B")
        val duplicateO3 = Odbiorca(id = "3", nazwa = "klient a") // duplikat po nazwie

        coEvery { odbiorcaRepository.getAllOdbiorcy() } returns listOf(o1, o2, duplicateO3)

        // When
        val result = viewModel.getListOfOdbiorca()

        // Then
        Assert.assertEquals(2, result.size)
        Assert.assertEquals("1", result[0].id) // Klient B
        Assert.assertEquals("2", result[1].id) // Klient A (z id = 2) ten co ma mniejsze id
    }

    @Test
    fun `replaceEditedProdukt updates item at index and calls callback`() = runTest {
        // Given
        val initialProdukt = Produkt.default().copy(id = "p1", nazwaProduktu = "Stary produkt")
        val initialPfzp = ProduktFakturaZProduktem(
            produktFaktura = ProduktFaktura.default().copy(id = "pf1", produktId = "p1"),
            produkt = initialProdukt
        )
        viewModel.setTestData(editedProdukty = listOf(initialPfzp))

        val newProdukt = Produkt.default().copy(id = "p2", nazwaProduktu = "Nowy produkt")
        var callbackCalled = false

        // When
        viewModel.replaceEditedProdukt(0, newProdukt) {
            callbackCalled = true
        }
        advanceUntilIdle()

        // Then
        viewModel.editedProdukty.test {
            val value = awaitItem()
            Assert.assertEquals(1, value.size)
            Assert.assertEquals("p2", value[0].produkt.id)
            Assert.assertEquals("Nowy produkt", value[0].produkt.nazwaProduktu)
            Assert.assertEquals("p2", value[0].produktFaktura.produktId)
        }
        Assert.assertTrue(callbackCalled)
    }
}
