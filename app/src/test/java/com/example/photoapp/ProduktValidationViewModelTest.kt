package com.example.photoapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.photoapp.features.produkt.validation.ProduktValidationViewModel
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

@OptIn(ExperimentalCoroutinesApi::class)
@Config(sdk = [33])
@RunWith(RobolectricTestRunner::class)
class ProduktValidationViewModelTest {

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

    private lateinit var viewModel: ProduktValidationViewModel

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = ProduktValidationViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.setMain(Dispatchers.Default)
    }

    @Test
    fun `validate function returns valid when value is correct`() = runTest{
        val latch = CountDownLatch(1)
        // Given
        val produktNameGood = "Good Name"
        val produktPriceGood = "Good Price"

        // When
        viewModel.validate(productName = produktNameGood, productPrice = produktPriceGood) {
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
    fun `validate function gives errors for buyerName`() = runTest{
        val latch = CountDownLatch(1)
        // Given
        val produktNameBad = ""
        val produktPriceBad = ""

        // When
        viewModel.validate(productName = produktNameBad, productPrice = produktPriceBad) {
            latch.countDown()
        }
        latch.await()

        // Then
        viewModel.validationResult.test {
            val value = awaitItem()
            Assert.assertFalse(value.isValid)
            Assert.assertEquals("Nazwa produktu nie może być pusta", value.fieldErrors["PRODUCT_NAME"])
            Assert.assertEquals("CENA produktu nie może być pusta", value.fieldErrors["PRODUCT_PRICE"])

        }
    }
}