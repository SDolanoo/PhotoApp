package com.example.photoapp.core.database.data.dao

import android.util.Log
import androidx.room.*
import com.example.photoapp.core.database.data.entities.Odbiorca

@Dao
interface OdbiorcaDao {

    @Query("SELECT * FROM Odbiorca")
    fun getAll(): List<Odbiorca>

    @Query("SELECT * FROM Odbiorca WHERE nip = :nip LIMIT 1")
    fun getByNip(nip: String): Odbiorca?

    @Insert
    fun insert(odbiorca: Odbiorca): Long

    @Update
    fun update(odbiorca: Odbiorca)

    @Delete
    fun delete(odbiorca: Odbiorca)
}
