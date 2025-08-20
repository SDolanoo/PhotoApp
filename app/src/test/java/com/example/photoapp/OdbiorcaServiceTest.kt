package com.example.photoapp

import android.util.Log
import app.cash.turbine.test
import com.example.photoapp.features.odbiorca.data.Odbiorca
import com.example.photoapp.features.odbiorca.data.OdbiorcaService
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.tasks.await
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.test.runTest
import org.junit.Assert

@OptIn(ExperimentalCoroutinesApi::class)
class OdbiorcaServiceTest {

    private val firestore: FirebaseFirestore = mockk()
    private val collection: CollectionReference = mockk()
    private val document: DocumentReference = mockk()
    private val query: Query = mockk()

    private val userId = "test-user-id"

    private lateinit var service: OdbiorcaService

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.e(any(), any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { firestore.collection("odbiorcy") } returns collection
        service = OdbiorcaService(firestore = firestore, currentUserIdProvider = { userId })
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
        every { doc1.toObject(Odbiorca::class.java) } returns Odbiorca(id = "", uzytkownikId = "", nazwa = "Firma1", nip = "111", adres = "ul. A")

        every { doc2.id } returns "id2"
        every { doc2.toObject(Odbiorca::class.java) } returns Odbiorca(id = "", uzytkownikId = "", nazwa = "Firma2", nip = "222", adres = "ul. B")

        every { doc3.id } returns "id3"
        every { doc3.toObject(Odbiorca::class.java) } returns Odbiorca(id = "", uzytkownikId = "", nazwa = "Firma3", nip = "333", adres = "ul. C")

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
    fun `getById should return parsed Odbiorca from document`() = runTest {
        // Given
        val id = "odb-123"
        val task = mockk<Task<DocumentSnapshot>>()
        val snapshot = mockk<DocumentSnapshot>()

        coEvery { collection.document(id) } returns document
        every { document.get() } returns Tasks.forResult(snapshot)

        every { snapshot.id } returns id
        every { snapshot.toObject(Odbiorca::class.java) } returns Odbiorca(id = "", uzytkownikId = "", nazwa = "FirmaX", nip = "999", adres = "ul. X")

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
        val id = "odb-err"
        coEvery { collection.document(id) } returns document
        every { document.get() } throws RuntimeException("Firestore error")

        // When
        val result = service.getById(id)

        // Then
        Assert.assertNull(result)
    }

    @Test
    fun `getByNip should return Odbiorca if found`() = runTest {
        // Given
        val nip = "1234567890"
        val queryTask = mockk<Task<QuerySnapshot>>()
        val snapshot = mockk<QuerySnapshot>()
        val documentSnap = mockk<DocumentSnapshot>()

        every { collection.whereEqualTo("nip", nip).limit(1) } returns query
        every { query.get() } returns Tasks.forResult(snapshot)

        every { snapshot.documents } returns listOf(documentSnap)
        every { documentSnap.id } returns "odb-1"
        every { documentSnap.toObject(Odbiorca::class.java) } returns Odbiorca(id = "", uzytkownikId = "", nazwa = "FirmaA", nip = nip, adres = "ul. A")

        // When
        val result = service.getByNip(nip)

        // Then
        Assert.assertNotNull(result)
        Assert.assertEquals(nip, result?.nip)
        Assert.assertEquals("FirmaA", result?.nazwa)
        Assert.assertEquals("odb-1", result?.id)
    }

    @Test
    fun `getByNip should return null if none found`() = runTest {
        // Given
        val nip = "0000000000"
        val queryTask = mockk<Task<QuerySnapshot>>()
        val snapshot = mockk<QuerySnapshot>()

        every { collection.whereEqualTo("nip", nip).limit(1) } returns query
        every { query.get() } returns queryTask
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
        val newId = "odb-new"

        val odbiorca = Odbiorca(id = "", uzytkownikId = "", nazwa = "Nowa Firma", nip = "999", adres = "ul. Nowa")

        every { collection.document() } returns newDoc
        every { newDoc.id } returns newId
        every { newDoc.set(any()) } returns Tasks.forResult(null)

        // When
        val result = service.insert(odbiorca)

        // Then
        Assert.assertEquals(newId, result)
    }

    @Test
    fun `update should call set on existing document`() = runTest {
        // Given
        val odbiorca = Odbiorca(id = "odb-123", uzytkownikId = "", nazwa = "FirmaX", nip = "123", adres = "ul. Testowa")

        every { collection.document(odbiorca.id) } returns document
        every { document.set(odbiorca) } returns Tasks.forResult(null)

        // When
        service.update(odbiorca)

        // Then
        coVerify { document.set(odbiorca) }
    }

    @Test
    fun `delete should call delete on document`() = runTest {
        // Given
        val odbiorca = Odbiorca(id = "odb-123", uzytkownikId = "", nazwa = "FirmaX", nip = "123", adres = "ul. Testowa")

        every { collection.document(odbiorca.id) } returns document
        every { document.delete() } returns Tasks.forResult(null)

        // When
        service.delete(odbiorca)

        // Then
        verify(exactly = 1) {
            collection.document("odb-123")
            document.delete()
        }
    }
}