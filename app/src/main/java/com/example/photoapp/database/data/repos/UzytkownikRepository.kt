package com.example.photoapp.database.data.repos

import com.example.photoapp.database.data.dao.UzytkownikDao
import com.example.photoapp.database.data.entities.Uzytkownik
import android.util.Log
import javax.inject.Inject

class UzytkownikRepository @Inject constructor(
    private val uzytkownikDao: UzytkownikDao
) {
    fun getAllUsers() = uzytkownikDao.getAll()

    fun getUserById(id: Int) = uzytkownikDao.getById(id)

    fun insertUser(login: String, password: String, email: String): Long {
        val newUser = Uzytkownik(login = login, password = password, email = email)
        val id = uzytkownikDao.insert(newUser)
        Log.i("UzytkownikRepo", "Inserted new user: $login with ID $id")
        return id
    }

    fun updateUser(uzytkownik: Uzytkownik) = uzytkownikDao.update(uzytkownik)

    fun deleteUser(uzytkownik: Uzytkownik) = uzytkownikDao.delete(uzytkownik)
}
