package com.example.photoapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.photoapp.core.utils.convertStringToDate
import com.example.photoapp.features.faktura.data.faktura.Faktura
import com.example.photoapp.features.faktura.data.faktura.FakturaRepository
import com.example.photoapp.features.faktura.data.faktura.FakturaService
import com.example.photoapp.features.produkt.data.Produkt
import com.example.photoapp.features.produkt.data.ProduktFaktura
import com.example.photoapp.features.produkt.data.ProduktFakturaService
import com.example.photoapp.features.produkt.data.ProduktService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
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

private object TestFRData {

    var faktura1 = Faktura.default().copy(id = "1")
    var faktura2 = Faktura.default().copy(id = "2")
    var fakturyList = listOf(faktura1, faktura2)

    var produkt1 = Produkt.default().copy(id = "1", nazwaProduktu = "Towar A")

    var produktFaktura1 = ProduktFaktura.default().copy(id = "1")

    var faktury = listOf(
        Faktura.default().copy(
            id = "1",
            dataSprzedazy = convertStringToDate("2024-01-15"),
            razemBrutto = "1500.00"
        ),
        Faktura.default().copy(
            id = "2",
            dataSprzedazy = convertStringToDate("2024-03-10"),
            razemBrutto = "3500.00"
        ),
        Faktura.default().copy(
            id = "3",
            dataSprzedazy = convertStringToDate("2024-05-01"),
            razemBrutto = "900.00"
        ),
        Faktura.default().copy(
            id = "4",
            dataSprzedazy = convertStringToDate("2024-06-20"),
            razemBrutto = "2200.00"
        ),
        Faktura.default().copy(
            id = "5",
            dataSprzedazy = convertStringToDate("2024-08-05"),
            razemBrutto = "4800.00"
        )
    )


}

@OptIn(ExperimentalCoroutinesApi::class)
@Config(sdk = [33])
@RunWith(RobolectricTestRunner::class)
class FakturaRepositoryTest {

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

    private val fakturaService: FakturaService = mockk(relaxed = true)
    private val produktFakturaService: ProduktFakturaService = mockk(relaxed = true)
    private val produktService: ProduktService = mockk(relaxed = true)
    private lateinit var repository: FakturaRepository

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        repository = FakturaRepository(
            fakturaService=fakturaService,
            produktFakturaService=produktFakturaService,
            produktService=produktService
        )
    }

    @After
    fun tearDown() {
        Dispatchers.setMain(Dispatchers.Default)
    }

    @Test
    fun `getAllLiveFaktury returns emptyList`() = runTest {
        // Given
        every { fakturaService.getAllLive() } returns flowOf(emptyList())

        // When
        val list = repository.getAllLiveFaktury().first()

        // Then
        Assert.assertTrue(list.isEmpty())
    }

    @Test
    fun `getAllLiveFaktury returns list of Faktura`() = runTest {
        // Given
        val faktura1 = Faktura.default().copy(id = "1")
        val faktura2 = Faktura.default().copy(id = "2")
        every { fakturaService.getAllLive() } returns flowOf(listOf(faktura1, faktura2))

        // When
        val list = repository.getAllLiveFaktury().first()

        // Then
        Assert.assertEquals(listOf(faktura1, faktura2), list)
    }

    @Test
    fun `getAllLiveProduktFaktura returns emptyList`() = runTest {
        // Given
        every { produktFakturaService.getAllLive() } returns flowOf(emptyList())

        // When
        val list = repository.getAllLiveProduktFaktura().first()

        // Then
        Assert.assertTrue(list.isEmpty())
    }


    @Test
    fun `getAllLiveProduktFaktura returns list of Faktura`() = runTest {
        // Given
        val pf1 = ProduktFaktura.default().copy(id = "1")
        val pf2 = ProduktFaktura.default().copy(id = "2")
        every { produktFakturaService.getAllLive() } returns flowOf(listOf(pf1, pf2))

        // When
        val list = repository.getAllLiveProduktFaktura().first()

        // Then
        Assert.assertEquals(listOf(pf1, pf2), list)
    }

    @Test
    fun `getAllLiveProdukt returns emptyList`() = runTest {
        // Given
        every { produktService.getAllLive() } returns flowOf(emptyList())

        // When
        val list = repository.getAllLiveProdukt().first()

        // Then
        Assert.assertTrue(list.isEmpty())
    }

    @Test
    fun `getAllLiveProdukt returns list of Faktura`() = runTest {
        // Given
        val p1 = Produkt.default().copy(id = "1")
        val p2 = Produkt.default().copy(id = "2")
        every { produktService.getAllLive() } returns flowOf(listOf(p1, p2))

        // When
        val list = repository.getAllLiveProdukt().first()

        // Then
        Assert.assertEquals(listOf(p1, p2), list)
    }

    @Test
    fun `getAllFaktury returns emptyList`() = runTest {
        // Given
        coEvery { fakturaService.getAllFaktury() } returns emptyList()

        // When
        val list = repository.getAllFaktury()

        // Then
        Assert.assertTrue(list.isEmpty())
    }

    @Test
    fun `getAllFaktury returns list of Faktura`() = runTest {
        // Given
        coEvery { fakturaService.getAllFaktury() } returns TestFRData.fakturyList

        // When
        val list = repository.getAllFaktury()

        // Then
        Assert.assertEquals(TestFRData.fakturyList, list)
    }

    @Test
    fun `getFakturaByID returns faktura when ID exists`() = runTest {
        // Given
        val faktura = TestFRData.faktura1
        coEvery { fakturaService.getById("1") } returns faktura

        // When
        val result = repository.getFakturaByID("1")

        // Then
        Assert.assertEquals(faktura, result)
    }

    @Test
    fun `getFakturaByID returns null when faktura not found`() = runTest {
        // Given
        coEvery { fakturaService.getById("404") } returns null

        // When
        val result = repository.getFakturaByID("404")

        // Then
        Assert.assertNull(result)
    }

    @Test
    fun `getProduktyFakturaForFaktura returns empty list when no products for faktura`() = runTest {
        // Given
        val faktura = Faktura.default().copy(id = "1")
        coEvery { produktFakturaService.getAllProduktFakturaForFakturaId("1") } returns emptyList()

        // When
        val result = repository.getProduktyFakturaForFaktura(faktura)

        // Then
        Assert.assertTrue(result.isEmpty())
    }

    @Test
    fun `getProduktyFakturaForFaktura returns only products for given faktura`() = runTest {
        // Given
        val faktura = Faktura.default().copy(id = "1")
        val produkt1 = ProduktFaktura.default().copy(id = "p1", fakturaId = "1")
        val produkt2 = ProduktFaktura.default().copy(id = "p2", fakturaId = "1")
        val unrelatedProdukt = ProduktFaktura.default().copy(id = "p3", fakturaId = "2")

        coEvery { produktFakturaService.getAllProduktFakturaForFakturaId("1") } returns listOf(produkt1, produkt2)

        // When
        val result = repository.getProduktyFakturaForFaktura(faktura)

        // Then
        Assert.assertEquals(2, result.size)
        Assert.assertTrue(result.containsAll(listOf(produkt1, produkt2)))
        Assert.assertFalse(result.contains(unrelatedProdukt))
    }

    @Test
    fun `getProduktForProduktFaktura returns correct Produkt`() = runTest {
        // Given
        val produktFaktura = ProduktFaktura.default().copy(produktId = "prod-123")
        val expectedProdukt = Produkt.default().copy(id = "prod-123")

        coEvery { produktFakturaService.getProduktForProduktFaktura("prod-123") } returns expectedProdukt

        // When
        val result = repository.getProduktForProduktFaktura(produktFaktura)

        // Then
        Assert.assertEquals(expectedProdukt, result)
    }

    @Test
    fun `insertFaktura returns inserted ID`() = runTest {
        // Given
        val faktura = Faktura.default().copy(id = "f-1")
        coEvery { fakturaService.insert(faktura) } returns "f-1"

        // When
        val result = repository.insertFaktura(faktura)

        // Then
        Assert.assertEquals("f-1", result)
    }

    @Test
    fun `insertProduktFaktura returns inserted ID`() = runTest {
        // Given
        val produktFaktura = ProduktFaktura.default().copy(id = "pf-1")
        coEvery { produktFakturaService.insert(produktFaktura) } returns "pf-1"

        // When
        val result = repository.insertProduktFaktura(produktFaktura)

        // Then
        Assert.assertEquals("pf-1", result)
    }

    @Test
    fun `insertProdukt returns inserted ID`() = runTest {
        // Given
        val produkt = Produkt.default().copy(id = "p-1")
        coEvery { produktService.insert(produkt) } returns "p-1"

        // When
        val result = repository.insertProdukt(produkt)

        // Then
        Assert.assertEquals("p-1", result)
    }

    @Test
    fun `updateFaktura updates Faktura`() = runTest {
        // Given
        val faktura = TestFRData.faktura1

        // When
        repository.updateFaktura(faktura)

        // Then
        coVerify(exactly = 1) { fakturaService.update(faktura) }
    }

    @Test
    fun `updateProduktFaktura updates ProduktFaktura`() = runTest {
        // Given
        val pf = TestFRData.produktFaktura1

        // When
        repository.updateProduktFaktura(pf)

        // Then
        coVerify(exactly = 1) { produktFakturaService.update(pf) }
    }

    @Test
    fun `updateProdukt updates Produkt`() = runTest {
        // Given
        val produkt = TestFRData.produkt1

        // When
        repository.updateProdukt(produkt)

        // Then
        coVerify(exactly = 1) { produktService.update(produkt) }
    }

    @Test
    fun `deleteFaktura deletes Faktura`() = runTest {
        // Given
        val faktura = TestFRData.faktura1

        // When
        repository.deleteFaktura(faktura)

        // Then
        coVerify(exactly = 1) { fakturaService.delete(faktura) }
    }

    @Test
    fun `deleteProduktFakturaFromFaktura deletes ProduktFaktura from Faktura`() = runTest {
        // Given
        val produktFaktura = TestFRData.produktFaktura1

        // When
        repository.deleteProduktFakturaFromFaktura(produktFaktura)

        // Then
        coVerify(exactly = 1) { produktFakturaService.delete(produktFaktura) }
    }

    @Test
    fun `fetchFilteredFaktury returns Faktura List`() = runTest {
        // Given
        val startDate = convertStringToDate("2024-01-01")
        val endDate = convertStringToDate("2024-05-02")
        val minPrice = 800.0
        val maxPrice = 1600.0
        val filterDate = "dataSprzedazy"
        val filterPrice = "brutto"
        coEvery { fakturaService.getFilteredFaktury(
            startDate,
            endDate,
            minPrice,
            maxPrice,
            filterDate,
            filterPrice
        ) } returns listOf(TestFRData.faktury[0], TestFRData.faktury[2])

        // When
        val result = repository.fetchFilteredFaktury(
            startDate,
            endDate,
            minPrice,
            maxPrice,
            filterDate,
            filterPrice
        )

        // Then
        Assert.assertEquals(listOf(TestFRData.faktury[0], TestFRData.faktury[2]), result)
    }

    @Test
    fun `getAllProdukty returns correct list`() = runTest {
        // Given
        val expectedList = listOf(
            Produkt.default().copy(id = "1"),
            Produkt.default().copy(id = "2")
        )
        coEvery { produktService.getAll() } returns expectedList

        // When
        val result = repository.getAllProdukty()

        // Then
        Assert.assertEquals(expectedList, result)
    }

    @Test
    fun `getProduktById returns correct Produkt`() = runTest {
        // Given
        val produkt = Produkt.default().copy(id = "123")
        coEvery { produktService.getOneProduktById("123") } returns produkt

        // When
        val result = repository.getProduktById("123")

        // Then
        Assert.assertEquals(produkt, result)
    }

    @Test
    fun `getListProduktyFakturaZProduktemForListFaktura returns combined list`() = runTest {
        // Given
        val faktura = Faktura.default().copy(id = "f1")
        val produktFaktura = ProduktFaktura.default().copy(id = "pf1", fakturaId = "f1", produktId = "p1")
        val produkt = Produkt.default().copy(id = "p1", nazwaProduktu = "Produkt Test")

        coEvery { produktFakturaService.getAllProduktFakturaForFakturaId("f1") } returns listOf(produktFaktura)
        coEvery { produktFakturaService.getProduktForProduktFaktura("p1") } returns produkt

        // When
        val result = repository.getListProduktyFakturaZProduktemForListFaktura(listOf(faktura))

        // Then
        Assert.assertEquals(1, result.size)
        Assert.assertEquals(produktFaktura, result.first().produktFaktura)
        Assert.assertEquals(produkt, result.first().produkt)
    }

    @Test
    fun `updateProduktAndRelativeData updates Produkt, ProduktFaktura and Faktura correctly`() = runTest {
        // Given
        val produkt = Produkt.default().copy(id = "p1", cenaNetto = "100.0", stawkaVat = "23")
        val pf = ProduktFaktura.default().copy(
            id = "pf1",
            fakturaId = "f1",
            produktId = "p1",
            ilosc = "2",
            wartoscNetto = "50.0",
            wartoscBrutto = "61.5"
        )
        val faktura = Faktura.default().copy(
            id = "f1",
            razemNetto = "50.0",
            razemBrutto = "61.5"
        )

        coEvery { produktService.update(produkt) } returns Unit
        coEvery { produktFakturaService.getAllProduktFakturaForProduktId("p1") } returns listOf(pf)
        coEvery { fakturaService.getById("pf1") } returns faktura
        coEvery { produktFakturaService.update(any()) } returns Unit
        coEvery { fakturaService.update(any()) } returns Unit

        // When
        repository.updateProduktAndRelativeData(produkt)

        // Then
        coVerify { produktService.update(produkt) }
        coVerify { produktFakturaService.getAllProduktFakturaForProduktId("p1") }
        coVerify { fakturaService.getById("pf1") }

        val expectedWartoscNetto = "200,00" // 2 * 100
        val expectedWartoscBrutto = "246,00" // +23% VAT

        coVerify {
            produktFakturaService.update(
                withArg {
                    Assert.assertEquals(expectedWartoscNetto, it.wartoscNetto)
                    Assert.assertEquals(expectedWartoscBrutto, it.wartoscBrutto)
                }
            )
        }

        coVerify {
            fakturaService.update(
                withArg {
                    Assert.assertEquals("200,00", it.razemNetto)
                    Assert.assertEquals("46,00", it.razemVAT)
                    Assert.assertEquals("246,00", it.razemBrutto)
                    Assert.assertEquals("246,00", it.doZaplaty)
                }
            )
        }
    }


}

