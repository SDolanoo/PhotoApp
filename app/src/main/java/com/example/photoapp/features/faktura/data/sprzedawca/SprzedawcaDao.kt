package com.example.photoapp.features.faktura.data.sprzedawca

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

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