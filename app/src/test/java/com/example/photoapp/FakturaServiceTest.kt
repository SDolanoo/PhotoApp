package com.example.photoapp

import com.example.photoapp.core.utils.convertStringToDate
import com.example.photoapp.features.faktura.data.faktura.Faktura
import com.example.photoapp.features.faktura.data.faktura.FakturaService
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Query
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class FakturaServiceTest {

    private val firestore: FirebaseFirestore = mockk()
    private val collection: CollectionReference = mockk()
    private val document: DocumentReference = mockk()
    private val query: Query = mockk()

    private val userId = "test-user-id"

    private lateinit var service: FakturaService

    @Before
    fun setup() {
        every { firestore.collection("faktury") } returns collection
        service = FakturaService(firestore = firestore, currentUserIdProvider = { userId })
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getAllFaktury returns mapped faktury list`() = runTest {
        // Arrange
        val snapshot = mockk<QuerySnapshot>()
        val doc1 = mockk<DocumentSnapshot>()
        val doc2 = mockk<DocumentSnapshot>()
        val faktura1 = Faktura.default().copy(id = "1")
        val faktura2 = Faktura.default().copy(id = "2")

        every { collection.whereEqualTo("uzytkownikId", userId) } returns query
        every { query.get() } returns Tasks.forResult(snapshot)
        every { snapshot.documents } returns listOf(doc1, doc2)
        every { doc1.toObject(Faktura::class.java) } returns faktura1
        every { doc2.toObject(Faktura::class.java) } returns faktura2
        every { doc1.id } returns "1"
        every { doc2.id } returns "2"

        // Act
        val result = service.getAllFaktury()

        // Assert
        Assert.assertEquals(2, result.size)
        Assert.assertEquals("1", result[0].id)
        Assert.assertEquals("2", result[1].id)
    }

    @Test
    fun `getAllFaktury should return parsed list of Faktura from snapshot`() = runTest {
        // Given
        val snapshot = mockk<QuerySnapshot>()
        val doc1 = mockk<DocumentSnapshot>()
        val doc2 = mockk<DocumentSnapshot>()

        val faktura1 = Faktura.default().copy(id = "1", numerFaktury = "FV1")
        val faktura2 = Faktura.default().copy(id = "2", numerFaktury = "FV2")

        every { collection.whereEqualTo("uzytkownikId", userId) } returns query
        every { query.get() } returns Tasks.forResult(snapshot)
        every { snapshot.documents } returns listOf(doc1, doc2)

        every { doc1.toObject(Faktura::class.java) } returns faktura1.copy(id = "") // id nadpisujemy niżej
        every { doc1.id } returns "1"

        every { doc2.toObject(Faktura::class.java) } returns faktura2.copy(id = "")
        every { doc2.id } returns "2"

        // When
        val result = service.getAllFaktury()

        // Then
        Assert.assertEquals(2, result.size)
        Assert.assertEquals("1", result[0].id)
        Assert.assertEquals("FV1", result[0].numerFaktury)
        Assert.assertEquals("2", result[1].id)
        Assert.assertEquals("FV2", result[1].numerFaktury)
    }

    @Test
    fun `getAllFaktury should skip null objects from toObject`() = runTest {
        // Given
        val snapshot = mockk<QuerySnapshot>()
        val doc1 = mockk<DocumentSnapshot>()
        val doc2 = mockk<DocumentSnapshot>()

        val faktura1 = Faktura.default().copy(id = "1", numerFaktury = "FV1")

        every { collection.whereEqualTo("uzytkownikId", userId) } returns query
        every { query.get() } returns Tasks.forResult(snapshot)
        every { snapshot.documents } returns listOf(doc1, doc2)

        // doc1 parsuje się poprawnie
        every { doc1.toObject(Faktura::class.java) } returns faktura1.copy(id = "")
        every { doc1.id } returns "1"

        // doc2 nieparsowalny (np. błędne dane)
        every { doc2.toObject(Faktura::class.java) } returns null

        // When
        val result = service.getAllFaktury()

        // Then
        Assert.assertEquals(1, result.size)
        Assert.assertEquals("1", result.first().id)
    }

    @Test
    fun `getById should return faktura if userId matches`() = runTest {
        // Given
        val docSnap = mockk<DocumentSnapshot>()
        val faktura = Faktura.default().copy(id = "", uzytkownikId = userId)

        every { collection.document("abc") } returns document
        every { document.get() } returns Tasks.forResult(docSnap)
        every { docSnap.toObject(Faktura::class.java) } returns faktura

        // When
        val result = service.getById("abc")

        // Then
        Assert.assertNotNull(result)
        Assert.assertEquals(userId, result?.uzytkownikId)
        Assert.assertEquals("abc", result?.id) // id powinien być nadpisany
    }

    @Test
    fun `getById should return null if userId does not match`() = runTest {
        // Given
        val docSnap = mockk<DocumentSnapshot>()
        val faktura = Faktura.default().copy(id = "", uzytkownikId = "inny-uzytkownik")

        every { collection.document("xyz") } returns document
        every { document.get() } returns Tasks.forResult(docSnap)
        every { docSnap.toObject(Faktura::class.java) } returns faktura

        // When
        val result = service.getById("xyz")

        // Then
        Assert.assertNull(result)
    }

    @Test
    fun `getById should return null if document is missing or null`() = runTest {
        // Given
        val docSnap = mockk<DocumentSnapshot>()

        every { collection.document("missing-id") } returns document
        every { document.get() } returns Tasks.forResult(docSnap)
        every { docSnap.toObject(Faktura::class.java) } returns null // <- to symuluje brak dokumentu

        // When
        val result = service.getById("missing-id")

        // Then
        Assert.assertNull(result)
    }

    @Test
    fun `insert should set document with generated ID and return it`() = runTest {
        // Given
        val faktura = Faktura.default().copy(id = "", numerFaktury = "FV123")
        val newDoc = mockk<DocumentReference>()
        val generatedId = "generated-faktura-id"

        every { collection.document() } returns newDoc
        every { newDoc.id } returns generatedId
        every { newDoc.set(any()) } returns Tasks.forResult(null)

        // When
        val result = service.insert(faktura)

        // Then
        Assert.assertEquals(generatedId, result)

        // Sprawdź, że set() zostało wywołane z kopią faktury z nadanym ID
        verify {
            newDoc.set(match { fakturaSet ->
                val faktura = fakturaSet as? Faktura ?: return@match false

                val idMatches = faktura.id == generatedId
                val numerMatches = faktura.numerFaktury == "FV123"

                idMatches && numerMatches
            })
        }
    }

    @Test
    fun `update should call set on document with given ID`() = runTest {
        // Given
        val faktura = Faktura.default().copy(id = "123", numerFaktury = "FV-123")

        every { collection.document("123") } returns document
        every { document.set(faktura) } returns Tasks.forResult(null)

        // When
        service.update(faktura)

        // Then
        verify(exactly = 1) {
            collection.document("123")
            document.set(faktura)
        }
    }

    @Test
    fun `delete should call delete on document with given ID`() = runTest {
        // Given
        val faktura = Faktura.default().copy(id = "123")

        every { collection.document("123") } returns document
        every { document.delete() } returns Tasks.forResult(null)

        // When
        service.delete(faktura)

        // Then
        verify(exactly = 1) {
            collection.document("123")
            document.delete()
        }
    }

    @Test
    fun `getFilteredFaktury should return only faktury in date range`() = runTest {
        // Given
        val f1 = Faktura.default().copy(id = "1", dataSprzedazy = convertStringToDate("2023-01-01"))
        val f2 = Faktura.default().copy(id = "2", dataSprzedazy = convertStringToDate("2023-06-01"))
        val f3 = Faktura.default().copy(id = "3", dataSprzedazy = convertStringToDate("2024-01-01"))

        coEvery { collection.whereEqualTo("uzytkownikId", userId) } returns query
        coEvery { query.get() } returns Tasks.forResult(mockk<QuerySnapshot> {
            every { documents } returns listOf(
                mockDoc("1", f1),
                mockDoc("2", f2),
                mockDoc("3", f3)
            )
        })

        val start = convertStringToDate("2023-03-01")
        val end = convertStringToDate("2023-12-31")

        // When
        val result = service.getFilteredFaktury(
            startDate = start,
            endDate = end,
            minPrice = null,
            maxPrice = null,
            filterDate = "dataSprzedazy",
            filterPrice = "brutto"
        )

        // Then
        Assert.assertEquals(1, result.size)
        Assert.assertEquals("2", result.first().id)
    }

    @Test
    fun `getFilteredFaktury should return only faktury in price range`() = runTest {
        // Given
        val f1 = Faktura.default().copy(id = "1", razemBrutto = "50.0")
        val f2 = Faktura.default().copy(id = "2", razemBrutto = "150.0")
        val f3 = Faktura.default().copy(id = "3", razemBrutto = "300.0")

        coEvery { collection.whereEqualTo("uzytkownikId", userId) } returns query
        coEvery { query.get() } returns Tasks.forResult(mockk<QuerySnapshot> {
            every { documents } returns listOf(
                mockDoc("1", f1),
                mockDoc("2", f2),
                mockDoc("3", f3)
            )
        })

        // When
        val result = service.getFilteredFaktury(
            startDate = null,
            endDate = null,
            minPrice = 100.0,
            maxPrice = 250.0,
            filterDate = "dataSprzedazy",
            filterPrice = "brutto"
        )

        // Then
        Assert.assertEquals(1, result.size)
        Assert. assertEquals("2", result.first().id)
    }

    @Test
    fun `getFilteredFaktury should apply both date and price filters`() = runTest {
        // Given
        val f1 = Faktura.default().copy(
            id = "1",
            dataSprzedazy = convertStringToDate("2023-05-01"),
            razemBrutto = "80.0"
        )
        val f2 = Faktura.default().copy(
            id = "2",
            dataSprzedazy = convertStringToDate("2023-07-01"),
            razemBrutto = "200.0"
        )
        val f3 = Faktura.default().copy(
            id = "3",
            dataSprzedazy = convertStringToDate("2024-01-01"),
            razemBrutto = "500.0"
        )

        coEvery { collection.whereEqualTo("uzytkownikId", userId) } returns query
        coEvery { query.get() } returns Tasks.forResult(mockk<QuerySnapshot> {
            every { documents } returns listOf(
                mockDoc("1", f1),
                mockDoc("2", f2),
                mockDoc("3", f3)
            )
        })

        val start = convertStringToDate("2023-06-01")
        val end = convertStringToDate("2023-12-31")

        // When
        val result = service.getFilteredFaktury(
            startDate = start,
            endDate = end,
            minPrice = 100.0,
            maxPrice = 300.0,
            filterDate = "dataSprzedazy",
            filterPrice = "brutto"
        )

        // Then
        Assert.assertEquals(1, result.size)
        Assert.assertEquals("2", result.first().id)
    }

    @Test
    fun `getFilteredFaktury should sort by dataWystawienia if selected`() = runTest {
        // Given
        val f1 = Faktura.default().copy(id = "1", dataWystawienia = convertStringToDate("2022-01-01"))
        val f2 = Faktura.default().copy(id = "2", dataWystawienia = convertStringToDate("2023-01-01"))
        val f3 = Faktura.default().copy(id = "3", dataWystawienia = convertStringToDate("2021-01-01"))

        coEvery { collection.whereEqualTo("uzytkownikId", userId) } returns query
        coEvery { query.get() } returns Tasks.forResult(mockk<QuerySnapshot> {
            every { documents } returns listOf(
                mockDoc("1", f1),
                mockDoc("2", f2),
                mockDoc("3", f3)
            )
        })

        // When
        val result = service.getFilteredFaktury(
            startDate = null,
            endDate = null,
            minPrice = null,
            maxPrice = null,
            filterDate = "dataWystawienia",
            filterPrice = "brutto"
        )

        // Then
        val idsSorted = result.map { it.id }
        Assert.assertEquals(listOf("2", "1", "3"), idsSorted) // 2023 -> 2022 -> 2021
    }

    @Test
    fun `getFilteredFaktury should sort by dataSprzedazy by default`() = runTest {
        // Given
        val f1 = Faktura.default().copy(id = "1", dataSprzedazy = convertStringToDate("2023-05-01"))
        val f2 = Faktura.default().copy(id = "2", dataSprzedazy = convertStringToDate("2024-01-01"))
        val f3 = Faktura.default().copy(id = "3", dataSprzedazy = convertStringToDate("2022-01-01"))

        coEvery { collection.whereEqualTo("uzytkownikId", userId) } returns query
        coEvery { query.get() } returns Tasks.forResult(mockk<QuerySnapshot> {
            every { documents } returns listOf(
                mockDoc("1", f1),
                mockDoc("2", f2),
                mockDoc("3", f3)
            )
        })

        // When
        val result = service.getFilteredFaktury(
            startDate = null,
            endDate = null,
            minPrice = null,
            maxPrice = null,
            filterDate = "cokolwiek_innego",
            filterPrice = "brutto"
        )

        // Then
        val idsSorted = result.map { it.id }
        Assert.assertEquals(listOf("2", "1", "3"), idsSorted) // 2024 -> 2023 -> 2022
    }

    fun mockDoc(id: String, faktura: Faktura): DocumentSnapshot {
        return mockk {
            every { toObject(Faktura::class.java) } returns faktura
            every { this@mockk.id } returns id
        }
    }
}


