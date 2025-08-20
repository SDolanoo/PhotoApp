package com.example.photoapp.features.produkt.data

import com.example.photoapp.core.utils.currentUserId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ProduktService(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val currentUserIdProvider: () -> String = { currentUserId() }// testowalne źródło userId)
) {

    private val produktyCollection = firestore.collection("produkty")

    // Live update tylko dla użytkownika
    fun getAllLive(): Flow<List<Produkt>> = callbackFlow {
        val listener = produktyCollection
            .whereEqualTo("uzytkownikId", currentUserIdProvider())
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { it.toObject<Produkt>() }
                    trySend(list) // najnowsze na górze
                }
            }
        awaitClose { listener.remove() }
    }

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
