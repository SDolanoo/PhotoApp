package com.example.photoapp

import android.util.Log
import com.example.photoapp.features.sprzedawca.data.Sprzedawca
import com.example.photoapp.features.sprzedawca.data.SprzedawcaService
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SprzedawcaServiceTest {

    private val firestore: FirebaseFirestore = mockk()
    private val collection: CollectionReference = mockk()
    private val document: DocumentReference = mockk()
    private val query: Query = mockk()

    private val userId = "test-user-id"

    private lateinit var service: SprzedawcaService

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.e(any(), any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { firestore.collection("sprzedawcy") } returns collection
        service = SprzedawcaService(firestore = firestore, currentUserIdProvider = { userId })
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
        unmockkAll()
    }

    @Test
    fun `getAll should return parsed list from snapshot`() = runTest {
        // Given
        val snapshot = mockk<QuerySnapshot>()
        val doc1 = mockk<DocumentSnapshot>()
        val doc2 = mockk<DocumentSnapshot>()
        val doc3 = mockk<DocumentSnapshot>()

        coEvery { collection.get() } returns Tasks.forResult(snapshot)
        coEvery { snapshot.documents } returns listOf(doc1, doc2, doc3)

        every { doc1.id } returns "id1"
        every { doc1.toObject(Sprzedawca::class.java) } returns Sprzedawca(id = "", uzytkownikId = "", nazwa = "Firma1", nip = "111", adres = "ul. A")

        every { doc2.id } returns "id2"
        every { doc2.toObject(Sprzedawca::class.java) } returns Sprzedawca(id = "", uzytkownikId = "", nazwa = "Firma2", nip = "222", adres = "ul. B")

        every { doc3.id } returns "id3"
        every { doc3.toObject(Sprzedawca::class.java) } returns Sprzedawca(id = "", uzytkownikId = "", nazwa = "Firma3", nip = "333", adres = "ul. C")

        // When
        val result = service.getAll()

        // Then
        Assert.assertEquals(3, result.size)
        Assert.assertEquals("Firma1", result[0].nazwa)
        Assert.assertEquals("Firma2", result[1].nazwa)
        Assert.assertEquals("Firma3", result[2].nazwa)
    }

    @Test
    fun `getAll should return empty list on exception`() = runTest {
        // Given
        coEvery { collection.get() } throws Exception("Firestore failure")

        // When
        val result = service.getAll()

        // Then
        Assert.assertTrue(result.isEmpty())
    }

    @Test
    fun `getById should return parsed Sprzedawca from document`() = runTest {
        // Given
        val id = "spr-123"
        val snapshot = mockk<DocumentSnapshot>()

        coEvery { collection.document(id) } returns document
        every { document.get() } returns Tasks.forResult(snapshot)

        every { snapshot.id } returns id
        every { snapshot.toObject(Sprzedawca::class.java) } returns Sprzedawca(id = "", uzytkownikId = "", nazwa = "FirmaX", nip = "999", adres = "ul. X")

        // When
        val result = service.getById(id)

        // Then
        Assert.assertNotNull(result)
        Assert.assertEquals(id, result?.id)
        Assert.assertEquals("FirmaX", result?.nazwa)
    }

    @Test
    fun `getById should return null if exception is thrown`() = runTest {
        // Given
        val id = "spr-err"
        coEvery { collection.document(id) } returns document
        every { document.get() } throws RuntimeException("Firestore error")

        // When
        val result = service.getById(id)

        // Then
        Assert.assertNull(result)
    }

    @Test
    fun `getByNip should return Sprzedawca if found`() = runTest {
        // Given
        val nip = "1234567890"
        val snapshot = mockk<QuerySnapshot>()
        val documentSnap = mockk<DocumentSnapshot>()

        every { collection.whereEqualTo("nip", nip).limit(1) } returns query
        every { query.get() } returns Tasks.forResult(snapshot)

        every { snapshot.documents } returns listOf(documentSnap)
        every { documentSnap.id } returns "spr-1"
        every { documentSnap.toObject(Sprzedawca::class.java) } returns Sprzedawca(id = "", uzytkownikId = "", nazwa = "FirmaA", nip = nip, adres = "ul. A")

        // When
        val result = service.getByNip(nip)

        // Then
        Assert.assertNotNull(result)
        Assert.assertEquals(nip, result?.nip)
        Assert.assertEquals("FirmaA", result?.nazwa)
        Assert.assertEquals("spr-1", result?.id)
    }

    @Test
    fun `getByNip should return null if none found`() = runTest {
        // Given
        val nip = "0000000000"
        val snapshot = mockk<QuerySnapshot>()

        every { collection.whereEqualTo("nip", nip).limit(1) } returns query
        every { query.get() } returns Tasks.forResult(snapshot)
        every { snapshot.documents } returns emptyList()

        // When
        val result = service.getByNip(nip)

        // Then
        Assert.assertNull(result)
    }

    @Test
    fun `insert should set document and return ID`() = runTest {
        // Given
        val newDoc = mockk<DocumentReference>()
        val newId = "spr-new"
        val sprzedawca = Sprzedawca(id = "", uzytkownikId = "", nazwa = "Nowa Firma", nip = "999", adres = "ul. Nowa")

        every { collection.document() } returns newDoc
        every { newDoc.id } returns newId
        every { newDoc.set(any()) } returns Tasks.forResult(null)

        // When
        val result = service.insert(sprzedawca)

        // Then
        Assert.assertEquals(newId, result)
    }

    @Test
    fun `update should call set on existing document`() = runTest {
        // Given
        val sprzedawca = Sprzedawca(id = "spr-123", uzytkownikId = "", nazwa = "FirmaX", nip = "123", adres = "ul. Testowa")

        every { collection.document(sprzedawca.id) } returns document
        every { document.set(sprzedawca) } returns Tasks.forResult(null)

        // When
        service.update(sprzedawca)

        // Then
        coVerify { document.set(sprzedawca) }
    }

    @Test
    fun `delete should call delete on document`() = runTest {
        // Given
        val sprzedawca = Sprzedawca(id = "spr-123", uzytkownikId = "", nazwa = "FirmaX", nip = "123", adres = "ul. Testowa")

        every { collection.document(sprzedawca.id) } returns document
        every { document.delete() } returns Tasks.forResult(null)

        // When
        service.delete(sprzedawca)

        // Then
        verify(exactly = 1) {
            collection.document("spr-123")
            document.delete()
        }
    }
}
