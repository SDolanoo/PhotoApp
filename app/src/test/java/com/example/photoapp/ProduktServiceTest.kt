package com.example.photoapp

import android.util.Log
import com.example.photoapp.features.produkt.data.Produkt
import com.example.photoapp.features.produkt.data.ProduktService
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProduktServiceTest {

    private val firestore: FirebaseFirestore = mockk()
    private val collection: CollectionReference = mockk()
    private val document: DocumentReference = mockk()
    private val query: Query = mockk()

    private val userId = "test-user-id"

    private lateinit var service: ProduktService

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.e(any(), any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { firestore.collection("produkty") } returns collection
        service = ProduktService(firestore = firestore, currentUserIdProvider = { userId })
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
        unmockkAll()
    }

    @Test
    fun `getAll should return parsed list of Produkty`() = runTest {
        // Given: snapshot with 3 documents parsable to Produkt
        val snapshot = mockk<QuerySnapshot>()
        val doc1 = mockk<DocumentSnapshot>()
        val doc2 = mockk<DocumentSnapshot>()
        val doc3 = mockk<DocumentSnapshot>()

        coEvery { collection.get() } returns Tasks.forResult(snapshot)
        every { snapshot.documents } returns listOf(doc1, doc2, doc3)

        every { doc1.id } returns "p1"
        every { doc1.toObject(Produkt::class.java) } returns Produkt(id = "", uzytkownikId = "", nazwaProduktu = "Produkt1", cenaNetto = "10.0")

        every { doc2.id } returns "p2"
        every { doc2.toObject(Produkt::class.java) } returns Produkt(id = "", uzytkownikId = "", nazwaProduktu = "Produkt2", cenaNetto = "20.0")

        every { doc3.id } returns "p3"
        every { doc3.toObject(Produkt::class.java) } returns Produkt(id = "", uzytkownikId = "", nazwaProduktu = "Produkt3", cenaNetto = "30.0")

        // When
        val result = service.getAll()

        // Then
        Assert.assertEquals(3, result.size)
        Assert.assertEquals("Produkt1", result[0].nazwaProduktu)
        Assert.assertEquals("Produkt2", result[1].nazwaProduktu)
        Assert.assertEquals("Produkt3", result[2].nazwaProduktu)
    }

    @Test
    fun `getProduktyByIds should return only Produkty with matching IDs`() = runTest {
        // Given: 3 documents in collection, but only 2 match IDs
        val snapshot = mockk<QuerySnapshot>()
        val doc1 = mockk<DocumentSnapshot>()
        val doc2 = mockk<DocumentSnapshot>()
        val doc3 = mockk<DocumentSnapshot>()

        val matchingIds = listOf("p1", "p3")

        coEvery { collection.get() } returns Tasks.forResult(snapshot)
        every { snapshot.documents } returns listOf(doc1, doc2, doc3)

        every { doc1.id } returns "p1"
        every { doc1.toObject(Produkt::class.java) } returns Produkt(id = "", uzytkownikId = "", nazwaProduktu = "A", cenaNetto = "1.0")

        every { doc2.id } returns "p2"
        every { doc2.toObject(Produkt::class.java) } returns Produkt(id = "", uzytkownikId = "", nazwaProduktu = "B", cenaNetto = "2.0")

        every { doc3.id } returns "p3"
        every { doc3.toObject(Produkt::class.java) } returns Produkt(id = "", uzytkownikId = "", nazwaProduktu = "C", cenaNetto = "3.0")

        // When
        val result = service.getProduktyByIds(matchingIds)

        // Then
        Assert.assertEquals(2, result.size)
        Assert.assertTrue(result.any { it.id == "p1" })
        Assert.assertTrue(result.any { it.id == "p3" })
    }

    @Test
    fun `getOneProduktById should return Produkt if found`() = runTest {
        // Given: document with matching ID exists
        val id = "prod-123"
        val docSnap = mockk<DocumentSnapshot>()

        every { collection.document(id) } returns document
        coEvery { document.get() } returns Tasks.forResult(docSnap)
        every { docSnap.toObject(Produkt::class.java) } returns Produkt(id = "", uzytkownikId = "", nazwaProduktu = "TestProd", cenaNetto = "9.99")

        // When
        val result = service.getOneProduktById(id)

        // Then
        Assert.assertEquals("TestProd", result.nazwaProduktu)
        Assert.assertEquals(id, result.id)
    }

    @Test(expected = NoSuchElementException::class)
    fun `getOneProduktById should throw if Produkt not found`() = runTest {
        // Given: document exists but not parsable
        val id = "missing-prod"
        val docSnap = mockk<DocumentSnapshot>()

        every { collection.document(id) } returns document
        coEvery { document.get() } returns Tasks.forResult(docSnap)
        every { docSnap.toObject(Produkt::class.java) } returns null

        // When
        service.getOneProduktById(id)

        // Then: exception is thrown
    }

    @Test
    fun `insert should store Produkt and return generated ID`() = runTest {
        // Given: Produkt to insert
        val newDoc = mockk<DocumentReference>()
        val newId = "prod-new"
        val produkt = Produkt(id = "", uzytkownikId = "", nazwaProduktu = "Nowy", cenaNetto = "12.5")

        every { collection.document() } returns newDoc
        every { newDoc.id } returns newId
        every { newDoc.set(produkt.copy(id = newId)) } returns Tasks.forResult(null)

        // When
        val result = service.insert(produkt)

        // Then
        Assert.assertEquals(newId, result)
    }

    @Test
    fun `update should set Produkt data on document`() = runTest {
        // Given: Produkt with ID to update
        val produkt = Produkt(id = "prod-1", uzytkownikId = "", nazwaProduktu = "Zmieniony", cenaNetto = "99.0")

        every { collection.document(produkt.id) } returns document
        every { document.set(produkt) } returns Tasks.forResult(null)

        // When
        service.update(produkt)

        // Then
        coVerify { document.set(produkt) }
    }

    @Test
    fun `delete should delete Produkt document by ID`() = runTest {
        // Given: Produkt to delete
        val produkt = Produkt(id = "prod-del", uzytkownikId = "", nazwaProduktu = "Do usuniecia",cenaNetto = "0.0")

        every { collection.document(produkt.id) } returns document
        every { document.delete() } returns Tasks.forResult(null)

        // When
        service.delete(produkt)

        // Then
        coVerify { document.delete() }
    }



}
