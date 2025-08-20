package com.example.photoapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.photoapp.features.sprzedawca.data.SaveMode
import com.example.photoapp.features.sprzedawca.data.Sprzedawca
import com.example.photoapp.features.sprzedawca.data.SprzedawcaRepository
import com.example.photoapp.features.sprzedawca.data.SprzedawcaService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.mockk.coEvery
import io.mockk.coJustRun
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

@OptIn(ExperimentalCoroutinesApi::class)
@Config(sdk = [33])
@RunWith(RobolectricTestRunner::class)
class SprzedawcaRepositoryTest {

    companion object {
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
            unmockkStatic(FirebaseAuth::class)
        }
    }

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val service: SprzedawcaService = mockk(relaxed = true)
    private lateinit var repository: SprzedawcaRepository

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        repository = SprzedawcaRepository(
            service = service,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.setMain(Dispatchers.Default)
    }


    @Test
    fun `getAllLiveSprzedawca should return flow from service`() = runTest {
        // Given
        val sprzedawca1 = Sprzedawca(id = "1", nazwa = "Firma A", nip = "123", adres = "ul. Testowa 1")
        val sprzedawca2 = Sprzedawca(id = "2", nazwa = "Firma B", nip = "456", adres = "ul. Testowa 2")
        val fakeFlow = flowOf(listOf(sprzedawca1, sprzedawca2))

        every { service.getAllLive() } returns fakeFlow

        // When
        val result = repository.getAllLiveSprzedawca().first()

        // Then
        Assert.assertEquals(2, result.size)
        Assert.assertEquals("Firma A", result[0].nazwa)
        Assert.assertEquals("Firma B", result[1].nazwa)
    }


    @Test
    fun `getAllSprzedawcy should return list from service`() = runTest {
        // Given
        val sprzedawca1 = Sprzedawca(id = "1", nazwa = "Firma A", nip = "1234567890", adres = "ul. Testowa 1")
        val sprzedawca2 = Sprzedawca(id = "2", nazwa = "Firma B", nip = "0987654321", adres = "ul. Przykładowa 2")

        coEvery { service.getAll() } returns listOf(sprzedawca1, sprzedawca2)

        // When
        val result = repository.getAll()

        // Then
        Assert.assertEquals(2, result.size)
        Assert.assertEquals("Firma A", result[0].nazwa)
        Assert.assertEquals("Firma B", result[1].nazwa)
    }


    @Test
    fun `getByNip should return sprzedawca when found`() = runTest {
        // Given
        val nip = "1234567890"
        val expected = Sprzedawca(id = "1", nazwa = "Firma A", nip = nip, adres = "ul. Testowa 1")
        coEvery { service.getByNip(nip) } returns expected

        // When
        val result = repository.getByNip(nip)

        // Then
        Assert.assertNotNull(result)
        Assert.assertEquals(expected.id, result?.id)
        Assert.assertEquals(expected.nazwa, result?.nazwa)
        Assert.assertEquals(expected.nip, result?.nip)
    }


    @Test
    fun `getByNip should return null when not found`() = runTest {
        // Given
        val nip = "0000000000"
        coEvery { service.getByNip(nip) } returns null

        // When
        val result = repository.getByNip(nip)

        // Then
        Assert.assertNull(result)
    }


    @Test
    fun `getById should return sprzedawca by ID`() = runTest {
        // Given
        val id = "spr-123"
        val expected = Sprzedawca(
            id = id,
            nazwa = "Firma Testowa",
            nip = "1234567890",
            adres = "ul. Przykładowa 5"
        )

        coEvery { service.getById(id) } returns expected

        // When
        val result = repository.getById(id)

        // Then
        Assert.assertNotNull(result)
        Assert.assertEquals(expected.id, result?.id)
        Assert.assertEquals(expected.nazwa, result?.nazwa)
        Assert.assertEquals(expected.nip, result?.nip)
        Assert.assertEquals(expected.adres, result?.adres)
    }


    @Test
    fun `insert should delegate to service and return new ID`() = runTest {
        // Given
        val sprzedawca = Sprzedawca(id = "", nazwa = "Nowy", nip = "1234567890", adres = "ul. Testowa 1")
        val generatedId = "sprzedawca-123"

        coEvery { service.insert(sprzedawca) } returns generatedId

        // When
        val result = repository.insert(sprzedawca)

        // Then
        Assert.assertEquals(generatedId, result)
    }


    @Test
    fun `update should call service update with correct object`() = runTest {
        // Given
        val sprzedawca = Sprzedawca(id = "sprzedawca-456", nazwa = "Zmieniony", nip = "9876543210", adres = "ul. Przykład 2")

        coJustRun { service.update(sprzedawca) }

        // When
        repository.update(sprzedawca)

        // Then
        coVerify { service.update(sprzedawca) }
    }


    @Test
    fun `delete should call service delete with correct object`() = runTest {
        // Given
        val sprzedawca = Sprzedawca(id = "sprzedawca-789", nazwa = "Do usunięcia", nip = "111222333", adres = "ul. Kasowana 9")

        coJustRun { service.delete(sprzedawca) }

        // When
        repository.delete(sprzedawca)

        // Then
        coVerify { service.delete(sprzedawca) }
    }


    @Test
    fun `addOrGetSprzedawca should return existing sprzedawca if found by NIP`() = runTest {
        // Given
        val nip = "1234567890"
        val existing = Sprzedawca(
            id = "spr-001",
            nazwa = "Istniejąca Firma",
            nip = nip,
            adres = "ul. Zapisana 1"
        )

        coEvery { service.getByNip(nip) } returns existing
        coEvery { service.insert(any()) } returns "should-not-be-called"

        // When
        val result = repository.addOrGetSprzedawca(
            nazwa = "Nie ma znaczenia",
            nip = nip,
            adres = "Nie powinien być użyty"
        )

        // Then
        Assert.assertEquals(existing, result)
        coVerify(exactly = 0) { service.insert(any()) }
    }


    @Test
    fun `addOrGetSprzedawca should insert new sprzedawca if not found`() = runTest {
        // Given
        val nazwa = "Nowy Sprzedawca"
        val nip = "999888777"
        val adres = "ul. Nowa 1"
        val generatedId = "sprzedawca-123"

        coEvery { service.getByNip(nip) } returns null
        coEvery { service.insert(any()) } returns generatedId

        // When
        val result = repository.addOrGetSprzedawca(nazwa, nip, adres)

        coVerify { service.insert(match {
            it.nazwa == nazwa && it.nip == nip && it.adres == adres
        }) }

        // Then
        Assert.assertEquals(generatedId, result.id)
        Assert.assertEquals(nazwa, result.nazwa)
        Assert.assertEquals(nip, result.nip)
        Assert.assertEquals(adres, result.adres)
    }


    @Test
    fun `upsertSprzedawcaSmart should update when match found by NIP`() = runTest {
        // Given
        val existing = Sprzedawca(id = "existing-id", nazwa = "Firma A", nip = "1234567890", adres = "adres")
        val new = Sprzedawca(id = "", nazwa = "Firma A", nip = "1234567890", adres = "adres")

        coEvery { service.getAll() } returns listOf(existing)
        coJustRun { service.update(any()) }

        // When
        val resultId = repository.upsertSprzedawcaSmart(new)

        // Then
        coVerify {
            service.update(withArg {
                Assert.assertEquals("existing-id", it.id)
                Assert.assertEquals("Firma A", it.nazwa)
            })
        }

        Assert.assertEquals("existing-id", resultId)
    }


    @Test
    fun `upsertSprzedawcaSmart should insert when no match found`() = runTest {
        // Given
        val new = Sprzedawca(id = "", nazwa = "Unikalna Firma", nip = "555444333", adres = "ul. Rzadko 5")
        val generatedId = "new-id-001"

        coEvery { service.getAll() } returns emptyList()
        coEvery { service.insert(any()) } returns generatedId

        // When
        val resultId = repository.upsertSprzedawcaSmart(new)

        // Then
        coVerify {
            service.insert(withArg {
                Assert.assertEquals("Unikalna Firma", it.nazwa)
                Assert.assertEquals("555444333", it.nip)
            })
        }

        Assert.assertEquals(generatedId, resultId)
    }


    @Test
    fun `determineSaveModeForSprzedawca should return Update if NIP matches`() = runTest {
        // Given
        val new = Sprzedawca(id = "", nazwa = "Firma X", nip = "1234567890", adres = "ul. X")
        val existing = Sprzedawca(id = "existing-id", nazwa = "Inna Firma", nip = "1234567890", adres = "ul. Y")
        val list = listOf(existing)

        // When
        val result = repository.determineSaveModeForSprzedawca(new, list)

        // Then
        Assert.assertTrue(result is SaveMode.Update)
        Assert.assertEquals("existing-id", (result as SaveMode.Update).existingId)
    }

    @Test
    fun `determineSaveModeForSprzedawca should return Insert if no match`() = runTest {
        // Given
        val new = Sprzedawca(id = "", nazwa = "Nowy Sprzedawca", nip = "0000000000", adres = "ul. Z")
        val existing = Sprzedawca(id = "existing-id", nazwa = "Inna Firma", nip = "1234567890", adres = "ul. Y")
        val list = listOf(existing)

        // When
        val result = repository.determineSaveModeForSprzedawca(new, list)

        // Then
        Assert.assertTrue(result is SaveMode.Insert)
    }

    @Test
    fun `determineSaveModeForSprzedawca should normalize name and nip before comparing`() = runTest {
        // Given
        val new = Sprzedawca(id = "", nazwa = "  firma X  ", nip = " 1234567890 ", adres = "ul. X")
        val existing = Sprzedawca(id = "existing-id", nazwa = "FIRMA x", nip = "1234567890", adres = "ul. Y")
        val list = listOf(existing)

        // When
        val result = repository.determineSaveModeForSprzedawca(new, list)

        // Then
        Assert.assertTrue(result is SaveMode.Update)
        Assert.assertEquals("existing-id", (result as SaveMode.Update).existingId)
    }
}
