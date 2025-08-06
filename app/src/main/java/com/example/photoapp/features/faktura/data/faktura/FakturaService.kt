package com.example.photoapp.features.faktura.data.faktura

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date

class FakturaService {

    private val db = FirebaseFirestore.getInstance()
    private val fakturaCol = db.collection("faktury")

    // Zastępuje LiveData
    fun getAllLive(): Flow<List<Faktura>> = callbackFlow {
        val listener = fakturaCol
            .orderBy("dataSprzedazy")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { it.toObject<Faktura>() }
                    trySend(list.reversed())
                }
            }
        awaitClose { listener.remove() }
    }

    suspend fun getAllFaktury(): List<Faktura> {
        val snapshot = fakturaCol.get().await()
        return snapshot.documents.mapNotNull { doc ->
            doc.toObject<Faktura>()?.copy(id = doc.id)
        }
    }

    suspend fun getById(id: String): Faktura? {
        val doc = fakturaCol.document(id).get().await()
        return doc.toObject<Faktura>()?.copy(id = id)
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
        val snapshot = fakturaCol.get().await()
        return snapshot.documents.mapNotNull { doc ->
            val faktura = doc.toObject<Faktura>() ?: return@mapNotNull null

            val dateField = if (filterDate == "dataWystawienia") faktura.dataWystawienia else faktura.dataSprzedazy
            val priceField = when (filterPrice) {
                "brutto" -> faktura.razemBrutto.toDoubleOrNull()
                "netto" -> faktura.razemNetto.toDoubleOrNull()
                else -> null
            }

            val inDateRange = (startDate == null || (dateField != null && dateField >= startDate)) &&
                    (endDate == null || (dateField != null && dateField <= endDate))

            val inPriceRange = (minPrice == null || (priceField != null && priceField >= minPrice)) &&
                    (maxPrice == null || (priceField != null && priceField <= maxPrice))

            if (inDateRange && inPriceRange) faktura else null
        }.sortedByDescending {
            if (filterDate == "dataWystawienia") it.dataWystawienia else it.dataSprzedazy
        }
    }
}
