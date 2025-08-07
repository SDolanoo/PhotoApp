package com.example.photoapp.features.produkt.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await

class ProduktFakturaService {

    private val db = FirebaseFirestore.getInstance()
    private val produktFakturaCol = db.collection("produktFaktury")
    private val produktyCol = db.collection("produkty")

    suspend fun getAll(): List<ProduktFaktura> {
        val snapshot = produktFakturaCol.get().await()
        return snapshot.documents.mapNotNull { doc ->
            doc.toObject<ProduktFaktura>()?.copy(id = doc.id)
        }
    }

    suspend fun getProduktyByIds(ids: List<String>): List<ProduktFaktura> {
        val snapshot = produktFakturaCol.get().await()
        return snapshot.documents.mapNotNull { doc ->
            val obj = doc.toObject<ProduktFaktura>()
            val docId = doc.id
            if (obj != null && docId in ids) obj.copy(id = docId) else null
        }
    }

    suspend fun getProduktForProduktFaktura(produktId: String): Produkt {
        val doc = produktyCol.document(produktId).get().await()
        return doc.toObject<Produkt>()?.copy(id = produktId)
            ?: throw NoSuchElementException("Brak produktu o id=$produktId")
    }

    suspend fun getAllProduktFakturaForFakturaId(fakturaId: String): List<ProduktFaktura> {
        val snapshot = produktFakturaCol
            .whereEqualTo("fakturaId", fakturaId)
            .get()
            .await()
        return snapshot.documents.mapNotNull { doc ->
            doc.toObject<ProduktFaktura>()?.copy(id = doc.id)
        }
    }

    suspend fun getAllProduktFakturaForProduktId(produktId: String): List<ProduktFaktura> {
        val snapshot = produktFakturaCol
            .whereEqualTo("produktId", produktId)
            .get()
            .await()
        return snapshot.documents.mapNotNull { doc ->
            doc.toObject<ProduktFaktura>()?.copy(id = doc.id)
        }
    }

    suspend fun insert(produktFaktura: ProduktFaktura): String {
        val newDoc = produktFakturaCol.document()
        val newId = newDoc.id
        newDoc.set(produktFaktura.copy(id = newId)).await()
        return newId
    }

    suspend fun update(produktFaktura: ProduktFaktura) {
        produktFakturaCol.document(produktFaktura.id).set(produktFaktura).await()
    }

    suspend fun delete(produktFaktura: ProduktFaktura) {
        produktFakturaCol.document(produktFaktura.id).delete().await()
    }
}
