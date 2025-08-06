package com.example.photoapp.core.database.data.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.photoapp.core.database.data.entities.Uzytkownik
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await

class UzytkownikService {

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("uzytkownicy")

    fun getAll(): LiveData<List<Uzytkownik>> {
        val liveData = MutableLiveData<List<Uzytkownik>>()
        collection.addSnapshotListener { snapshot, _ ->
            val users = snapshot?.documents?.mapNotNull {
                it.toObject<Uzytkownik>()?.copy(id = it.id)
            } ?: emptyList()
            liveData.postValue(users)
        }
        return liveData
    }

    suspend fun getById(id: String): Uzytkownik? {
        val doc = collection.document(id).get().await()
        return doc.toObject<Uzytkownik>()?.copy(id = doc.id)
    }

    suspend fun insert(Uzytkownik: Uzytkownik): String {
        val newDoc = collection.document()
        val newId = newDoc.id
        newDoc.set(Uzytkownik.copy(id = newId)).await()
        return newId
    }

    suspend fun update(mUser: Uzytkownik) {
        mUser.id?.let { id ->
            collection.document(id).set(mUser).await()
        }
    }

    suspend fun delete(mUser: Uzytkownik) {
        mUser.id?.let { id ->
            collection.document(id).delete().await()
        }
    }
}
