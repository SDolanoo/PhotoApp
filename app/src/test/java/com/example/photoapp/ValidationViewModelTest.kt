package com.example.photoapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.photoapp.features.faktura.presentation.details.ProduktFakturaZProduktem
import com.example.photoapp.features.faktura.validation.ValidationViewModel
import com.example.photoapp.features.produkt.data.Produkt
import com.example.photoapp.features.produkt.data.ProduktFaktura
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
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

private object TestValidationFaktura {
    var sellerNameBad = ""
    var buyerNameBad = ""
    var productsBad = listOf<ProduktFakturaZProduktem>(
        ProduktFakturaZProduktem(produkt = Produkt.default().copy(id = "1", nazwaProduktu = "111"), produktFaktura = ProduktFaktura.default().copy(id = "1", ilosc = "", wartoscBrutto = "111")),
        ProduktFakturaZProduktem(produkt = Produkt.default().copy(id = "2", nazwaProduktu = ""), produktFaktura = ProduktFaktura.default().copy(id = "2", ilosc = "22", wartoscBrutto = "222")),
        ProduktFakturaZProduktem(produkt = Produkt.default().copy(id = "3", nazwaProduktu = "333"), produktFaktura = ProduktFaktura.default().copy(id = "3", ilosc = "33", wartoscBrutto = ""))
    )

    var sellerNameGood = "hello"
    var buyerNameGood = "world"
    var productsGood = listOf<ProduktFakturaZProduktem>(
        ProduktFakturaZProduktem(produkt = Produkt.default().copy(id = "1", nazwaProduktu = "111"), produktFaktura = ProduktFaktura.default().copy(id = "1", ilosc = "11", wartoscBrutto = "111")),
        ProduktFakturaZProduktem(produkt = Produkt.default().copy(id = "2", nazwaProduktu = "222"), produktFaktura = ProduktFaktura.default().copy(id = "2", ilosc = "22", wartoscBrutto = "222")),
        ProduktFakturaZProduktem(produkt = Produkt.default().copy(id = "3", nazwaProduktu = "333"), produktFaktura = ProduktFaktura.default().copy(id = "3", ilosc = "33", wartoscBrutto = "333"))
    )
}

@OptIn(ExperimentalCoroutinesApi::class)
@Config(sdk = [33])
@RunWith(RobolectricTestRunner::class)
class ValidationViewModelTest { // IT'S FAKTURA VALIDATION VM

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

    private lateinit var viewModel: ValidationViewModel

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = ValidationViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.setMain(Dispatchers.Default)
    }

    @Test
    fun `validate function returns valid as all values are correct`() = runTest{
        val latch = CountDownLatch(1)
        // Given
        val sprzedawca = TestValidationFaktura.sellerNameGood
        val odbiorca = TestValidationFaktura.buyerNameGood
        val produkty = TestValidationFaktura.productsGood

        // When
        viewModel.validate(sellerName = sprzedawca, buyerName = odbiorca, products = produkty) {
            latch.countDown()
        }
        latch.await()

        // Then
        viewModel.validationResult.test {
            val value = awaitItem()
            Assert.assertTrue(value.isValid)
        }
    }

    @Test
    fun `validate function gives errors for sellerName`() = runTest{
        val latch = CountDownLatch(1)
        // Given
        val sprzedawca = TestValidationFaktura.sellerNameBad
        val odbiorca = TestValidationFaktura.buyerNameBad
        val produkty = TestValidationFaktura.productsBad

        // When
        viewModel.validate(sellerName = sprzedawca, buyerName = odbiorca, products = produkty) {
            latch.countDown()
        }
        latch.await()

        // Then
        viewModel.validationResult.test {
            val value = awaitItem()
            Assert.assertFalse(value.isValid)
            Assert.assertEquals("Nazwa sprzedawcy nie może być pusta", value.fieldErrors["SELLER_NAME"])
        }
    }

    @Test
    fun `validate function gives errors for buyerName`() = runTest{
        val latch = CountDownLatch(1)
        // Given
        val sprzedawca = TestValidationFaktura.sellerNameBad
        val odbiorca = TestValidationFaktura.buyerNameBad
        val produkty = TestValidationFaktura.productsBad

        // When
        viewModel.validate(sellerName = sprzedawca, buyerName = odbiorca, products = produkty) {
            latch.countDown()
        }
        latch.await()

        // Then
        viewModel.validationResult.test {
            val value = awaitItem()
            Assert.assertFalse(value.isValid)
            Assert.assertEquals("Nazwa odbiorcy nie może być pusta", value.fieldErrors["BUYER_NAME"])
        }
    }

    @Test
    fun `validate function gives errors for products`() = runTest{
        val latch = CountDownLatch(1)
        // Given
        val sprzedawca = TestValidationFaktura.sellerNameBad
        val odbiorca = TestValidationFaktura.buyerNameBad
        val produkty = TestValidationFaktura.productsBad

        // When
        viewModel.validate(sellerName = sprzedawca, buyerName = odbiorca, products = produkty) {
            latch.countDown()
        }
        latch.await()

        // Then
        viewModel.validationResult.test {
            val value = awaitItem()
            Assert.assertFalse(value.isValid)
            Assert.assertEquals("Produkt 2: Nazwa nie może być pusta", value.fieldErrors["PRODUCT_NAME_1"])
            Assert.assertEquals("Produkt 1: Ilość nie może być pusta", value.fieldErrors["PRODUCT_QUANTITY_0"])
            Assert.assertEquals("Produkt 3: Wartość brutto nie może być pusta", value.fieldErrors["PRODUCT_BRUTTO_2"])
        }
    }
}