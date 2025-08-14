package com.example.photoapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.photoapp.features.captureFlow.presentation.acceptFaktura.AcceptFakturaController
import com.example.photoapp.features.faktura.data.faktura.Faktura
import com.example.photoapp.features.faktura.data.faktura.FakturaRepository
import com.example.photoapp.features.faktura.presentation.details.ProduktFakturaZProduktem
import com.example.photoapp.features.odbiorca.data.Odbiorca
import com.example.photoapp.features.odbiorca.data.OdbiorcaRepository
import com.example.photoapp.features.produkt.data.Produkt
import com.example.photoapp.features.produkt.data.ProduktFaktura
import com.example.photoapp.features.sprzedawca.data.Sprzedawca
import com.example.photoapp.features.sprzedawca.data.SprzedawcaRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
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
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalCoroutinesApi::class)
@Config(sdk = [33])
@RunWith(RobolectricTestRunner::class)
class AcceptFakturaControllerTest {

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
    private lateinit var controller: AcceptFakturaController
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        controller = AcceptFakturaController(
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
    fun `allProdukty returns emptyList`() = runTest {
        // given
        every { fakturaRepository.getAllProdukty() } returns emptyList()

        // when
        val result = controller.allProducts()

        // then
        Assert.assertTrue(result.isEmpty())
    }

    @Test
    fun `allProdukty returns list of Produkt`() = runTest {
        // given
        val produkt1 = Produkt.default().copy(id = "1")
        val produkt2 = Produkt.default().copy(id = "2")
        every { fakturaRepository.getAllProdukty() } returns listOf(produkt1, produkt2)

        // when
        val result = controller.allProducts()

        // then
        Assert.assertEquals(listOf(produkt1, produkt2), result)
    }

    @Test
    fun `checkForExistingProducts calls callback with existing ID when Produkt already exists`() = runTest {
        // given
        val produkt1 = Produkt.default().copy(id = "1", nazwaProduktu = "1", cenaNetto = "1")
        val produkt2 = Produkt.default().copy(id = "2", nazwaProduktu = "2", cenaNetto = "2")
        every { fakturaRepository.getAllProdukty() } returns listOf(produkt1, produkt2)

        val produktNew = Produkt.default().copy(nazwaProduktu = "2", cenaNetto = "2")

        // when
        var result = CompletableDeferred<String>()

        controller.checkForExistingProducts(produktNew) { value ->
            result.complete(value)
        }

        // then
        val assertResult = result.await()
        Assert.assertEquals("2", assertResult)
    }

    @Test
    fun `checkForExistingProducts inserts Produkt and callback with new ID when Produkt does not exist`() = runTest {
        // given
        val produkt1 = Produkt.default().copy(id = "1", nazwaProduktu = "1", cenaNetto = "1")
        val produkt2 = Produkt.default().copy(id = "2", nazwaProduktu = "2", cenaNetto = "2")
        every { fakturaRepository.getAllProdukty() } returns listOf(produkt1, produkt2)

        val produktNew = Produkt.default().copy(nazwaProduktu = "3", cenaNetto = "2")

        every { fakturaRepository.insertProdukt(any()) } returns "3"

        // when
        var result = CompletableDeferred<String>()

        controller.checkForExistingProducts(produktNew) { value ->
            result.complete(value)
        }

        // then
        val assertResult = result.await()
        Assert.assertEquals("3", assertResult)
    }

    @Test
    fun `checkForExistingProducts inserts Produkt and callback with new ID when Produkt list is Empty`() = runTest {
        // given
        every { fakturaRepository.getAllProdukty() } returns emptyList()

        val produktNew = Produkt.default().copy(nazwaProduktu = "3", cenaNetto = "2")

        every { fakturaRepository.insertProdukt(any()) } returns "1"

        // when
        var result = CompletableDeferred<String>()

        controller.checkForExistingProducts(produktNew) { value ->
            result.complete(value)
        }

        // then
        val assertResult = result.await()
        Assert.assertEquals("1", assertResult)
    }

    @Test
    fun `addToDatabase should calculate sums and call insertFaktura with correct data`() = runTest {
        // given
        val latch = CountDownLatch(1)

        val faktura = Faktura(id = "f1", sprzedawcaId = "", odbiorcaId = "", razemNetto = "", razemVAT = "", razemBrutto = "", doZaplaty = "")
        val sprzedawca = Sprzedawca(id = "s1")
        val odbiorca = Odbiorca(id = "o1")

        val produkty = listOf(
            ProduktFakturaZProduktem(
                produkt = Produkt(id = "p1", nazwaProduktu = "Test", cenaNetto = "10.00"),
                produktFaktura = ProduktFaktura(id = "pf1", wartoscNetto = "10,00", wartoscBrutto = "12,30")
            ),
            ProduktFakturaZProduktem(
                produkt = Produkt(id = "p2", nazwaProduktu = "Test2", cenaNetto = "20.00"),
                produktFaktura = ProduktFaktura(id = "pf2", wartoscNetto = "20.00", wartoscBrutto = "24.60")
            )
        )

        val produkt1 = Produkt.default().copy(id = "1", nazwaProduktu = "1", cenaNetto = "1")
        val produkt2 = Produkt.default().copy(id = "2", nazwaProduktu = "2", cenaNetto = "2")
        every { fakturaRepository.getAllProdukty() } returns listOf(produkt1, produkt2)

        coEvery { sprzedawcaRepository.upsertSprzedawcaSmart(sprzedawca) } returns "sprzedawcaId123"
        coEvery { odbiorcaRepository.upsertOdbiorcaSmart(odbiorca) } returns "odbiorcaId123"

        val fakturaDeferred = CompletableDeferred<Faktura>()
        coEvery { fakturaRepository.insertFaktura(any()) } coAnswers {
            val fakturaRes = firstArg<Faktura>()
            fakturaDeferred.complete(fakturaRes)
            "fakturaId123"
        }
        coEvery { fakturaRepository.insertProduktFaktura(any()) } returns "123"

        // when
        controller.addToDatabase(faktura, sprzedawca, odbiorca, produkty)
        dispatcher.scheduler.advanceUntilIdle() // czeka na zakończenie addToDatabasse

        latch.await(3, TimeUnit.SECONDS)

        // then
        val captured = fakturaDeferred.await()
        Assert.assertEquals("sprzedawcaId123", captured.sprzedawcaId)
        Assert.assertEquals("odbiorcaId123", captured.odbiorcaId)
        Assert.assertEquals("30,00", captured.razemNetto)
        Assert.assertEquals("6,90", captured.razemVAT)
        Assert.assertEquals("36,90", captured.razemBrutto)
        Assert.assertEquals("36,90", captured.doZaplaty)

    }
}