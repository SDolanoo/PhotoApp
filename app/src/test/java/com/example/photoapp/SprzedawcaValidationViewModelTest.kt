package com.example.photoapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.photoapp.features.odbiorca.validation.OdbiorcaValidationViewModel
import com.example.photoapp.features.sprzedawca.validation.SprzedawcaValidationViewModel
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
class SprzedawcaValidationViewModelTest {

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

    private lateinit var viewModel: SprzedawcaValidationViewModel

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = SprzedawcaValidationViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.setMain(Dispatchers.Default)
    }

    @Test
    fun `validate function returns valid when value is correct`() = runTest{
        val latch = CountDownLatch(1)
        // Given
        val sprzedawcaNameGood = "Good Name"

        // When
        viewModel.validate(sellerName = sprzedawcaNameGood) {
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
        val sprzedawcaNameBad = ""

        // When
        viewModel.validate(sellerName = sprzedawcaNameBad) {
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

}