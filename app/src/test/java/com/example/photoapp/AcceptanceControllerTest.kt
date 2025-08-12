package com.example.photoapp

import android.graphics.Bitmap
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.robolectric.annotation.Config
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.photoapp.core.AI.chatWithGemini
import com.example.photoapp.features.captureFlow.presentation.acceptPhoto.AcceptanceController
import com.example.photoapp.features.faktura.data.faktura.FakturaRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.test.runTest
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner


@OptIn(ExperimentalCoroutinesApi::class)
@Config(sdk = [33]) // wersja Androida dla Robolectric
@RunWith(RobolectricTestRunner::class)
class AcceptanceControllerTest {

    companion object {
        @JvmStatic
        @BeforeClass
        fun globalFirebaseMock() {
            mockkStatic(FirebaseAuth::class)
            // mockuje FirebaseAuth dla current usera, żeby modele mogły się wczytać
            val fakeUser = mockk<FirebaseUser>()
            every { fakeUser.uid } returns "test-user-id"
            every { FirebaseAuth.getInstance().currentUser } returns fakeUser
        }

        @JvmStatic
        @AfterClass
        fun removeFirebaseMock() {
            unmockkStatic(FirebaseAuth::class)
        }
    }

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private val fakturaRepository: FakturaRepository = mockk(relaxed = true)
    private lateinit var controller: AcceptanceController
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        controller = AcceptanceController(fakturaRepository)

        mockkStatic(::chatWithGemini) // mockuje funkcje chatWithGemini, dla innych funkcji
    }

    @After
    fun tearDown() {
        unmockkStatic(::chatWithGemini)
        // Przywracamy główny dispatcher
        Dispatchers.setMain(Dispatchers.Default)
    }

    @Test
    fun `formObjectsFrom3Prompt should update state flows`() = runTest {
        // given
        val fakeJson = listOf(
            """{"numerFaktury":"FV-123","dataWystawienia":"2024-01-01","dataSprzedazy":"2024-01-01","miejsceWystawienia":"Warszawa","waluta":"PLN","formaPlatnosci":"przelew"}""",
            """{"sprzedawca":{"nazwa":"Firma X","nip":"123","adres":"","kodPocztowy":"","miejscowosc":"","kraj":"","opis":"","email":"","telefon":""},"odbiorca":{"nazwa":"Firma Y","nip":"321","adres":"","kodPocztowy":"","miejscowosc":"","kraj":"","opis":"","email":"","telefon":""}}""",
            """{"produkty":[{"nazwaProduktu":"Towar 1","jednostkaMiary":"szt","cenaNetto":"100.0","stawkaVat":"23%","ilosc":"1","rabat":"0","wartoscNetto":"100.0","wartoscBrutto":"123.0"}]}"""
        )

        // when
        controller.formObjectsFrom3Prompt(fakeJson) {}

        // then
        controller.faktura.test {
            val faktura = awaitItem()
            Assert.assertEquals("FV-123", faktura.numerFaktury)
            Assert.assertEquals("PLN", faktura.waluta)
            cancelAndIgnoreRemainingEvents()
        }

        controller.sprzedawca.test {
            val sprzedawca = awaitItem()
            Assert.assertEquals("Firma X", sprzedawca.nazwa)
            cancelAndIgnoreRemainingEvents()
        }

        controller.odbiorca.test {
            val odbiorca = awaitItem()
            Assert.assertEquals("Firma Y", odbiorca.nazwa)
            cancelAndIgnoreRemainingEvents()
        }

        controller.produkty.test {
            val produkty = awaitItem()
            Assert.assertEquals(1, produkty.size)
            Assert.assertEquals("Towar 1", produkty.first().produkt.nazwaProduktu)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `processPhoto with valid Bitmap should update flows`() = runTest {
        // given
        val bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        val fakeJson = listOf(
            """{"numerFaktury":"FV-001","dataWystawienia":"2024-01-01","dataSprzedazy":"2024-01-01","miejsceWystawienia":"Warszawa","waluta":"PLN","formaPlatnosci":"przelew"}""",
            """{"sprzedawca":{"nazwa":"Firma X","nip":"","adres":"","kodPocztowy":"","miejscowosc":"","kraj":"","opis":"","email":"","telefon":""},"odbiorca":{"nazwa":"Firma Y","nip":"","adres":"","kodPocztowy":"","miejscowosc":"","kraj":"","opis":"","email":"","telefon":""}}""",
            """{"produkty":[{"nazwaProduktu":"Towar 1","jednostkaMiary":"szt","cenaNetto":"100.0","stawkaVat":"23%","ilosc":"1","rabat":"0","wartoscNetto":"100.0","wartoscBrutto":"123.0"}]}"""
        )

        every {
            chatWithGemini(any(), any(), any(), any())
        } answers {
            val callback = arg<(Int, List<String>) -> Unit>(3)
            callback(1, fakeJson)
        }

        // when
        controller.processPhoto("key", bitmap) { success, _ -> Assert.assertTrue(success) }
        advanceUntilIdle()

        // then
        controller.faktura.test {
            val faktura = awaitItem()
            Assert.assertEquals("FV-001", faktura.numerFaktury)
            cancelAndIgnoreRemainingEvents()
        }
        controller.sprzedawca.test {
            val sprzedawca = awaitItem()
            Assert.assertEquals("Firma X", sprzedawca.nazwa)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `processPhoto with null Bitmap should return error`() = runTest {
        // when
        var callbackInvoked = false
        controller.processPhoto("key", null) { success, message ->
            callbackInvoked = true
            Assert.assertFalse(success)
            Assert.assertTrue(message.first().contains("No valid image"))
        }

        // then
        Assert.assertTrue(callbackInvoked)
    }

    @Test
    fun `getPrompt with valid Bitmap should store geminiPromptResult`() = runTest {
        // given
        val bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        val expected = listOf("one", "two", "three")

        every {
            chatWithGemini(any(), any(), any(), any())
        } answers {
            val callback = arg<(Int, List<String>) -> Unit>(3)
            callback(1, expected)
        }

        var received: List<String>? = null

        // when
        controller.getPrompt("key", bitmap) { _, result ->
            received = result
        }
        advanceUntilIdle()

        // then
        Assert.assertEquals(expected, received)
    }

    @Test
    fun `getPrompt with null Bitmap should return error`() = runTest {
        var received: List<String>? = null

        // when
        controller.getPrompt("key", null) { _, result ->
            received = result
        }

        // then
        Assert.assertTrue(received!!.first().contains("No valid image"))
    }

}