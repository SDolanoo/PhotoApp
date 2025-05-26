package com.example.photoapp.database.data.dao

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.photoapp.database.data.entities.Uzytkownik

@Dao
interface UzytkownikDao {

    @Query("SELECT * FROM Uzytkownik")
    fun getAll(): LiveData<List<Uzytkownik>>

    @Query("SELECT * FROM Uzytkownik WHERE id = :id")
    fun getById(id: Int): Uzytkownik?

    @Insert
    fun insert(uzytkownik: Uzytkownik): Long

    @Update
    fun update(uzytkownik: Uzytkownik)

    @Delete
    fun delete(uzytkownik: Uzytkownik)

    @Transaction
    fun addUser(login: String, password: String, email: String) {
        val newUser = Uzytkownik(login = login, password = password, email = email)
        val id = insert(newUser)
        Log.i("Dolan", "Użytkownik '$login' został dodany z ID: $id")
    }
}
