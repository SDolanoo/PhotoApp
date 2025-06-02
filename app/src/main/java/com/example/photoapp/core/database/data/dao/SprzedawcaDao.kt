package com.example.photoapp.core.database.data.dao

import android.util.Log
import androidx.room.*
import com.example.photoapp.core.database.data.entities.Sprzedawca

@Dao
interface SprzedawcaDao {

    @Query("SELECT * FROM Sprzedawca")
    fun getAll(): List<Sprzedawca>

    @Query("SELECT * FROM Sprzedawca WHERE id = :id")
    fun getById(id: Long): Sprzedawca?

    @Query("SELECT * FROM Sprzedawca WHERE nip = :nip LIMIT 1")
    fun getByNip(nip: String): Sprzedawca?

    @Insert
    fun insert(sprzedawca: Sprzedawca): Long

    @Update
    fun update(sprzedawca: Sprzedawca)

    @Delete
    fun delete(sprzedawca: Sprzedawca)
}
