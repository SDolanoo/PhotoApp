package com.example.photoapp.features.sprzedawca.data

import android.util.Log
import com.example.photoapp.core.utils.currentUserId
import com.example.photoapp.features.produkt.data.ProduktFaktura
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class SprzedawcaService {

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("sprzedawcy")

    // Live update tylko dla użytkownika
    fun getAllLive(): Flow<List<Sprzedawca>> = callbackFlow {
        val listener = collection
            .whereEqualTo("uzytkownikId", currentUserId())
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { it.toObject<Sprzedawca>() }
                    trySend(list) // najnowsze na górze
                }
            }
        awaitClose { listener.remove() }
    }

    suspend fun getAll(): List<Sprzedawca> {
        return try {
            val snapshot = collection.get().await()
            snapshot.documents.mapNotNull { it.toObject(Sprzedawca::class.java)?.copy(id = it.id) }
        } catch (e: Exception) {
            Log.e("SprzedawcaService", "getAll error: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun getById(id: String): Sprzedawca? {
        return try {
            val doc = collection.document(id).get().await()
            doc.toObject(Sprzedawca::class.java)?.copy(id = doc.id)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getByNip(nip: String): Sprzedawca? {
        return try {
            val query = collection.whereEqualTo("nip", nip).limit(1).get().await()
            query.documents.firstOrNull()?.let {
                it.toObject(Sprzedawca::class.java)?.copy(id = it.id)
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun insert(sprzedawca: Sprzedawca): String {
        val newDoc = collection.document()
        val sprzedawcaWithId = sprzedawca.copy(id = newDoc.id)
        newDoc.set(sprzedawcaWithId).await()
        return newDoc.id
    }

    suspend fun update(sprzedawca: Sprzedawca) {
        collection.document(sprzedawca.id).set(sprzedawca).await()
    }

    suspend fun delete(sprzedawca: Sprzedawca) {
        collection.document(sprzedawca.id).delete().await()
    }
}
