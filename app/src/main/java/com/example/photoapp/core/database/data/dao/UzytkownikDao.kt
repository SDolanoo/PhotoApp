package com.example.photoapp.core.database.data.dao

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.photoapp.core.database.data.entities.Uzytkownik

@Dao
interface UzytkownikDao {

    @Query("SELECT * FROM Uzytkownik")
    fun getAll(): LiveData<List<Uzytkownik>>

    @Query("SELECT * FROM Uzytkownik WHERE id = :id")
    fun getById(id: Long): Uzytkownik?

    @Insert
    fun insert(uzytkownik: Uzytkownik): Long

    @Update
    fun update(uzytkownik: Uzytkownik)

    @Delete
    fun delete(uzytkownik: Uzytkownik)
}
