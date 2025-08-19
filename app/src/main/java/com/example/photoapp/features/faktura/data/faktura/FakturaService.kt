package com.example.photoapp.features.faktura.data.faktura

import com.example.photoapp.core.utils.currentUserId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date

class FakturaService(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val currentUserIdProvider: () -> String = { currentUserId() } // testowalne źródło userId
) {
    private val fakturaCol = firestore.collection("faktury")

    // Live update tylko dla użytkownika
    fun getAllLive(): Flow<List<Faktura>> = callbackFlow {
        val listener = fakturaCol
            .whereEqualTo("uzytkownikId", currentUserIdProvider())
            .orderBy("dataSprzedazy")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { it.toObject<Faktura>() }
                    trySend(list.reversed()) // najnowsze na górze
                }
            }
        awaitClose { listener.remove() }
    }

    // Jednorazowe pobranie faktur użytkownika
    suspend fun getAllFaktury(): List<Faktura> {
        val snapshot = fakturaCol
            .whereEqualTo("uzytkownikId", currentUserIdProvider())
            .get().await()

        return snapshot.documents.mapNotNull { doc ->
            doc.toObject<Faktura>()?.copy(id = doc.id)
        }
    }

    suspend fun getById(id: String): Faktura? {
        val doc = fakturaCol.document(id).get().await()
        val faktura = doc.toObject<Faktura>()?.copy(id = id)

        // Filtrujemy dodatkowo po userId
        return if (faktura?.uzytkownikId == currentUserIdProvider()) faktura else null
    }


    suspend fun insert(faktura: Faktura): String {
        val newDoc = fakturaCol.document()
        val newId = newDoc.id
        newDoc.set(faktura.copy(id = newId)).await()
        return newId
    }

    suspend fun update(faktura: Faktura) {
        fakturaCol.document(faktura.id).set(faktura).await()
    }

    suspend fun delete(faktura: Faktura) {
        fakturaCol.document(faktura.id).delete().await()
    }

    suspend fun getFilteredFaktury(
        startDate: Date?,
        endDate: Date?,
        minPrice: Double?,
        maxPrice: Double?,
        filterDate: String,
        filterPrice: String
    ): List<Faktura> {
        /**
         * filterDate = dataWystawienia or dataSprzedazy
         * filterPrice = brutto or netto
         */
        val allFaktury = getAllFaktury() // 🧠 pobieramy tylko raz i z cache jeśli się da

        return allFaktury.filter { faktura ->
            val dateField = when (filterDate) {
                "dataWystawienia" -> faktura.dataWystawienia
                else -> faktura.dataSprzedazy
            }

            val priceField = when (filterPrice) {
                "brutto" -> faktura.razemBrutto.toDoubleOrNull()
                "netto" -> faktura.razemNetto.toDoubleOrNull()
                else -> null
            }

            val inDateRange = (startDate == null || (dateField != null && dateField >= startDate)) &&
                    (endDate == null || (dateField != null && dateField <= endDate))

            val inPriceRange = (minPrice == null || (priceField != null && priceField >= minPrice)) &&
                    (maxPrice == null || (priceField != null && priceField <= maxPrice))

            inDateRange && inPriceRange
        }.sortedByDescending { faktura ->
            when (filterDate) {
                "dataWystawienia" -> faktura.dataWystawienia
                else -> faktura.dataSprzedazy
            }
        }
    }
}
