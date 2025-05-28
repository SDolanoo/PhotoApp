package com.example.photoapp.core.database.data.repos

import android.util.Log
import com.example.photoapp.core.database.data.dao.UzytkownikDao
import com.example.photoapp.core.database.data.entities.Uzytkownik
import javax.inject.Inject

class UzytkownikRepository @Inject constructor(
    private val uzytkownikDao: UzytkownikDao
) {
    fun getAll() = uzytkownikDao.getAll()

    fun getById(id: Int) = uzytkownikDao.getById(id)

    fun insert(login: String, password: String, email: String): Long {
        val newUser = Uzytkownik(login = login, password = password, email = email)
        val id = uzytkownikDao.insert(newUser)
        Log.i("UzytkownikRepo", "Inserted new user: $login with ID $id")
        return id
    }

    fun update(uzytkownik: Uzytkownik) = uzytkownikDao.update(uzytkownik)

    fun delete(uzytkownik: Uzytkownik) = uzytkownikDao.delete(uzytkownik)
}
