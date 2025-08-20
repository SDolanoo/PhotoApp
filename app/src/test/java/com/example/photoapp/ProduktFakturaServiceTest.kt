package com.example.photoapp

import android.util.Log
import com.example.photoapp.features.produkt.data.Produkt
import com.example.photoapp.features.produkt.data.ProduktFaktura
import com.example.photoapp.features.produkt.data.ProduktFakturaService
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
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.assertThrows

@OptIn(ExperimentalCoroutinesApi::class)
class ProduktFakturaServiceTest {

    private val firestore: FirebaseFirestore = mockk()
    private val collection: CollectionReference = mockk()
    private val document: DocumentReference = mockk()
    private val query: Query = mockk()

    private val userId = "test-user-id"

    private lateinit var service: ProduktFakturaService

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.e(any(), any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { firestore.collection("produktFaktury") } returns collection
        every { firestore.collection("produkty") } returns collection
        service = ProduktFakturaService(firestore = firestore, currentUserIdProvider = { userId })
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
        every { snapshot.documents } returns listOf(doc1, doc2, doc3)

        every { doc1.id } returns "id1"
        every { doc1.toObject(ProduktFaktura::class.java) } returns ProduktFaktura(id = "", uzytkownikId = "", fakturaId = "f1", produktId = "p1", ilosc = "2")

        every { doc2.id } returns "id2"
        every { doc2.toObject(ProduktFaktura::class.java) } returns ProduktFaktura(id = "", uzytkownikId = "", fakturaId = "f2", produktId = "p2", ilosc = "3")

        every { doc3.id } returns "id3"
        every { doc3.toObject(ProduktFaktura::class.java) } returns ProduktFaktura(id = "", uzytkownikId = "", fakturaId = "f3", produktId = "p3", ilosc = "1")

        // When
        val result = service.getAll()

        // Then
        Assert.assertEquals(3, result.size)
        Assert.assertEquals("id1", result[0].id)
        Assert.assertEquals("id2", result[1].id)
        Assert.assertEquals("id3", result[2].id)
    }


    @Test
    fun `getProduktyByIds should return only ProduktFaktura with matching IDs`() = runTest {
        // Given
        val matchingIds = listOf("id1", "id3")
        val snapshot = mockk<QuerySnapshot>()
        val doc1 = mockk<DocumentSnapshot>()
        val doc2 = mockk<DocumentSnapshot>()
        val doc3 = mockk<DocumentSnapshot>()

        coEvery { collection.get() } returns Tasks.forResult(snapshot)
        every { snapshot.documents } returns listOf(doc1, doc2, doc3)

        every { doc1.id } returns "id1"
        every { doc1.toObject(ProduktFaktura::class.java) } returns ProduktFaktura(id = "", uzytkownikId = "", fakturaId = "f1", produktId = "p1", ilosc = "1")

        every { doc2.id } returns "id2"
        every { doc2.toObject(ProduktFaktura::class.java) } returns ProduktFaktura(id = "", uzytkownikId = "", fakturaId = "f2", produktId = "p2", ilosc = "1")

        every { doc3.id } returns "id3"
        every { doc3.toObject(ProduktFaktura::class.java) } returns ProduktFaktura(id = "", uzytkownikId = "", fakturaId = "f3", produktId = "p3", ilosc = "1")

        // When
        val result = service.getProduktyByIds(matchingIds)

        // Then
        Assert.assertEquals(2, result.size)
        Assert.assertTrue(result.all { it.id in matchingIds })
    }


    @Test
    fun `getProduktForProduktFaktura should return Produkt when document exists`() = runTest {
        // Given: dokument o podanym ID istnieje i można go sparsować
        val produktId = "prod-123"
        val produktSnapshot = mockk<DocumentSnapshot>()
        val expectedProdukt = Produkt(id = "", uzytkownikId = "", nazwaProduktu = "Test Produkt", cenaNetto = "123.45")

        every { collection.document(produktId) } returns document
        every { document.get() } returns Tasks.forResult(produktSnapshot)
        every { produktSnapshot.toObject(Produkt::class.java) } returns expectedProdukt

        // When
        val result = service.getProduktForProduktFaktura(produktId)

        // Then
        Assert.assertEquals("Test Produkt", result.nazwaProduktu)
        Assert.assertEquals("123.45", result.cenaNetto)
        Assert.assertEquals(produktId, result.id)
    }


    @Test
    fun `getProduktForProduktFaktura should throw exception if Produkt not found`() = runTest {
        // Given: dokument nie istnieje lub nie da się sparsować
        val produktId = "nonexistent-prod"
        val produktSnapshot = mockk<DocumentSnapshot>()

        every { collection.document(produktId) } returns document
        every { document.get() } returns Tasks.forResult(produktSnapshot)
        every { produktSnapshot.toObject(Produkt::class.java) } returns null // brak sparsowania

        // When + Then
        val exception = assertThrows(NoSuchElementException::class.java) {
            runBlocking {
                service.getProduktForProduktFaktura(produktId)
            }
        }

        Assert.assertEquals("Brak produktu o id=$produktId", exception.message)
    }


    @Test
    fun `getAllProduktFakturaForFakturaId should return list for given fakturaId`() = runTest {
        // Given: kolekcja zawiera dokumenty z tym fakturaId
        val fakturaId = "fak-001"
        val snapshot = mockk<QuerySnapshot>()
        val doc1 = mockk<DocumentSnapshot>()
        val doc2 = mockk<DocumentSnapshot>()

        every {
            collection.whereEqualTo("fakturaId", fakturaId)
        } returns query
        every { query.get() } returns Tasks.forResult(snapshot)

        every { snapshot.documents } returns listOf(doc1, doc2)

        every { doc1.id } returns "pf-1"
        every { doc1.toObject(ProduktFaktura::class.java) } returns ProduktFaktura(
            id = "", uzytkownikId = "", fakturaId = fakturaId, produktId = "prod-1", ilosc = "1"
        )

        every { doc2.id } returns "pf-2"
        every { doc2.toObject(ProduktFaktura::class.java) } returns ProduktFaktura(
            id = "", uzytkownikId = "", fakturaId = fakturaId, produktId = "prod-2", ilosc = "2"
        )

        // When
        val result = service.getAllProduktFakturaForFakturaId(fakturaId)

        // Then
        Assert.assertEquals(2, result.size)
        Assert.assertEquals("pf-1", result[0].id)
        Assert.assertEquals("pf-2", result[1].id)
    }


    @Test
    fun `getAllProduktFakturaForProduktId should return list for given produktId`() = runTest {
        // Given: kolekcja zawiera dokumenty z tym produktId
        val produktId = "prod-123"
        val snapshot = mockk<QuerySnapshot>()
        val doc1 = mockk<DocumentSnapshot>()
        val doc2 = mockk<DocumentSnapshot>()

        every {
            collection.whereEqualTo("produktId", produktId)
        } returns query
        every { query.get() } returns Tasks.forResult(snapshot)

        every { snapshot.documents } returns listOf(doc1, doc2)

        every { doc1.id } returns "pf-10"
        every { doc1.toObject(ProduktFaktura::class.java) } returns ProduktFaktura(
            id = "", uzytkownikId = "", fakturaId = "fak-1", produktId = produktId, ilosc = "3"
        )

        every { doc2.id } returns "pf-11"
        every { doc2.toObject(ProduktFaktura::class.java) } returns ProduktFaktura(
            id = "", uzytkownikId = "", fakturaId = "fak-2", produktId = produktId, ilosc = "5"
        )

        // When
        val result = service.getAllProduktFakturaForProduktId(produktId)

        // Then
        Assert.assertEquals(2, result.size)
        Assert.assertEquals("pf-10", result[0].id)
        Assert.assertEquals("pf-11", result[1].id)
    }


    @Test
    fun `insert should store ProduktFaktura with generated ID and return it`() = runTest {
        // Given: new ProduktFaktura object
        val newDoc = mockk<DocumentReference>()
        val newId = "pf-new"
        val produktFaktura = ProduktFaktura(id = "", uzytkownikId = "", fakturaId = "fak-1", produktId = "prod-1", ilosc = "5")

        every { collection.document() } returns newDoc
        every { newDoc.id } returns newId
        every { newDoc.set(produktFaktura.copy(id = newId)) } returns Tasks.forResult(null)

        // When
        val result = service.insert(produktFaktura)

        // Then
        Assert.assertEquals(newId, result)
    }


    @Test
    fun `update should call set on document with correct data`() = runTest {
        // Given: existing ProduktFaktura with ID
        val produktFaktura = ProduktFaktura(id = "pf-123", uzytkownikId = "", fakturaId = "fak-1", produktId = "prod-2", ilosc = "3")

        every { collection.document(produktFaktura.id) } returns document
        every { document.set(produktFaktura) } returns Tasks.forResult(null)

        // When
        service.update(produktFaktura)

        // Then
        coVerify(exactly = 1) { document.set(produktFaktura) }
    }

    @Test
    fun `delete should call delete on document with given ID`() = runTest {
        // Given: ProduktFaktura with specific ID
        val produktFaktura = ProduktFaktura(id = "pf-del", uzytkownikId = "", fakturaId = "fak-2", produktId = "prod-9", ilosc = "2")

        every { collection.document(produktFaktura.id) } returns document
        every { document.delete() } returns Tasks.forResult(null)

        // When
        service.delete(produktFaktura)

        // Then
        coVerify(exactly = 1) { document.delete() }
    }

}