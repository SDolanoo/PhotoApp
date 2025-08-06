package com.example.photoapp.core.database.data.repos

import androidx.lifecycle.LiveData
import com.example.photoapp.core.database.data.dao.UzytkownikService
import com.example.photoapp.core.database.data.entities.Uzytkownik
import com.example.photoapp.core.utils.convertDateToString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject

class UzytkownikRepository @Inject constructor(
    private val uzytkownikService: UzytkownikService
) {

    fun getAll(): LiveData<List<Uzytkownik>> = uzytkownikService.getAll()

    fun getById(id: String): Uzytkownik? = runBlocking {
        withContext(Dispatchers.IO) {
            uzytkownikService.getById(id)
        }
    }

    fun insert(user: Uzytkownik): String = runBlocking {
        withContext(Dispatchers.IO) {
            uzytkownikService.insert(user)
        }
    }

    fun update(mUser: Uzytkownik) {
        runBlocking {
            withContext(Dispatchers.IO) {
                uzytkownikService.update(mUser)
            }
        }
    }

    fun delete(mUser: Uzytkownik) {
        runBlocking {
            withContext(Dispatchers.IO) {
                uzytkownikService.delete(mUser)
            }
        }
    }
}
