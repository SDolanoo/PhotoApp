package com.example.photoapp

import android.app.Application
import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.photoapp.features.excelPacker.presentation.ExportRoomViewModel
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
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import org.apache.poi.xssf.usermodel.XSSFWorkbook
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
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert

@OptIn(ExperimentalCoroutinesApi::class)
@Config(sdk = [33])
@RunWith(RobolectricTestRunner::class)
class ExportRoomViewModelTest {

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
    private val context: Context = mockk(relaxed = true)
    private lateinit var viewModel: ExportRoomViewModel
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        every { context.applicationContext } returns mockk<Application>(relaxed = true)
        viewModel = ExportRoomViewModel(
            fakturaRepository,
            odbiorcaRepository,
            sprzedawcaRepository,
            context
        )
    }

    @After
    fun tearDown() {
        Dispatchers.setMain(Dispatchers.Default)
    }

    @Test
    fun `exportFaktura should create correct rows in workbook`() = runTest {
        // given
        val workbook = XSSFWorkbook()

        val faktura = Faktura(
            id = "1",
            sprzedawcaId = "s1",
            odbiorcaId = "o1",
            typFaktury = "VAT",
            numerFaktury = "FV/1/2025",
            razemNetto = "100.00",
            razemVAT = "23.00",
            razemBrutto = "123.00",
            doZaplaty = "123.00",
            waluta = "PLN",
            formaPlatnosci = "przelew"
        )

        val sprzedawca = Sprzedawca(id = "s1", nazwa = "Test Sprzedawca", nip = "1234567890")
        val odbiorca = Odbiorca(id = "o1", nazwa = "Test Odbiorca", nip = "0987654321")

        val produktFaktura = ProduktFaktura(id = "pf1", fakturaId = "1", wartoscNetto = "100.00", wartoscBrutto = "123.00")
        val produkt = Produkt(id = "p1", nazwaProduktu = "Test Produkt", jednostkaMiary = "szt", cenaNetto = "100.00", stawkaVat = "23%")

        coEvery { fakturaRepository.getListProduktyFakturaZProduktemForListFaktura(listOf(faktura)) } returns
                listOf(ProduktFakturaZProduktem(produktFaktura, produkt))

        coEvery { sprzedawcaRepository.getById("s1") } returns sprzedawca
        coEvery { odbiorcaRepository.getById("o1") } returns odbiorca

        // when
        viewModel.exportFaktura(workbook, listOf(faktura))

        // then
        val sheetFaktura = workbook.getSheet("Faktury")
        val sheetProdukty = workbook.getSheet("Produkty")

        // nagłówki faktury
        Assert.assertEquals("ID Faktury", sheetFaktura.getRow(0).getCell(0).stringCellValue)
        Assert.assertEquals("1", sheetFaktura.getRow(1).getCell(0).stringCellValue) // id faktury
        Assert.assertEquals("Test Sprzedawca", sheetFaktura.getRow(1).getCell(2).stringCellValue)
        Assert.assertEquals("Test Odbiorca", sheetFaktura.getRow(1).getCell(4).stringCellValue)

        // nagłówki produktów
        Assert.assertEquals("ID Faktury", sheetProdukty.getRow(0).getCell(0).stringCellValue)
        Assert.assertEquals("1", sheetProdukty.getRow(1).getCell(0).stringCellValue) // id faktury w produktach
        Assert.assertEquals("Test Produkt", sheetProdukty.getRow(1).getCell(1).stringCellValue)

        coVerify {
            fakturaRepository.getListProduktyFakturaZProduktemForListFaktura(listOf(faktura))
            sprzedawcaRepository.getById("s1")
            odbiorcaRepository.getById("o1")
        }
    }
}
