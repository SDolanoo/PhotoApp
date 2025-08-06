package com.example.photoapp.features.produkt.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await

class ProduktService {

    private val db = FirebaseFirestore.getInstance()
    private val produktyCollection = db.collection("produkty")

    suspend fun getAll(): List<Produkt> {
        val snapshot = produktyCollection.get().await()
        return snapshot.documents.mapNotNull { doc ->
            doc.toObject<Produkt>()?.copy(id = doc.id.toString())
        }
    }

    suspend fun getProduktyByIds(ids: List<String>): List<Produkt> {
        val snapshot = produktyCollection.get().await()
        return snapshot.documents.mapNotNull { doc ->
            val produkt = doc.toObject<Produkt>()
            val docId = doc.id.toString()
            if (produkt != null && docId in ids) {
                produkt.copy(id = docId)
            } else null
        }
    }

    suspend fun getOneProduktById(id: String): Produkt {
        val doc = produktyCollection.document(id.toString()).get().await()
        return doc.toObject<Produkt>()?.copy(id = id)
            ?: throw NoSuchElementException("Produkt o id=$id nie istnieje.")
    }

    suspend fun insert(produkt: Produkt): String {
        val newDoc = produktyCollection.document() // Firestore wygeneruje ID
        val newId = newDoc.id.toString()
        newDoc.set(produkt.copy(id = newId)).await()
        return newId
    }

    suspend fun update(produkt: Produkt) {
        produktyCollection.document(produkt.id.toString()).set(produkt).await()
    }

    suspend fun delete(produkt: Produkt) {
        produktyCollection.document(produkt.id.toString()).delete().await()
    }
}
