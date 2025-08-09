package com.example.photoapp.features.odbiorca.data

import android.util.Log
import com.example.photoapp.core.utils.currentUserId
import com.example.photoapp.features.faktura.data.faktura.Faktura
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class OdbiorcaService {

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("odbiorcy")

    // Live update tylko dla użytkownika
    fun getAllLive(): Flow<List<Odbiorca>> = callbackFlow {
        val listener = collection
            .whereEqualTo("uzytkownikId", currentUserId())
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { it.toObject<Odbiorca>() }
                    trySend(list) // najnowsze na górze
                }
            }
        awaitClose { listener.remove() }
    }

    suspend fun getAll(): List<Odbiorca> {
        return try {
            val snapshot = collection.get().await()
            snapshot.documents.mapNotNull { it.toObject(Odbiorca::class.java)?.copy(id = it.id) }
        } catch (e: Exception) {
            Log.e("OdbiorcaService", "getAll error: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun getById(id: String): Odbiorca? {
        return try {
            val doc = collection.document(id).get().await()
            doc.toObject(Odbiorca::class.java)?.copy(id = doc.id)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getByNip(nip: String): Odbiorca? {
        return try {
            val query = collection.whereEqualTo("nip", nip).limit(1).get().await()
            query.documents.firstOrNull()?.let {
                it.toObject(Odbiorca::class.java)?.copy(id = it.id)
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun insert(odbiorca: Odbiorca): String {
        val newDoc = collection.document()
        val odbiorcaWithId = odbiorca.copy(id = newDoc.id)
        newDoc.set(odbiorcaWithId).await()
        return newDoc.id
    }

    suspend fun update(odbiorca: Odbiorca) {
        collection.document(odbiorca.id).set(odbiorca).await()
    }

    suspend fun delete(odbiorca: Odbiorca) {
        collection.document(odbiorca.id).delete().await()
    }
}
