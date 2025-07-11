package com.example.photoapp.features.faktura.data.odbiorca

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.photoapp.features.faktura.data.sprzedawca.Sprzedawca

@Dao
interface OdbiorcaDao {

    @Query("SELECT * FROM Odbiorca")
    fun getAll(): List<Odbiorca>

    @Query("SELECT * FROM Odbiorca WHERE id = :id")
    fun getById(id: Long): Odbiorca?

    @Query("SELECT * FROM Odbiorca WHERE nip = :nip LIMIT 1")
    fun getByNip(nip: String): Odbiorca?

    @Insert
    fun insert(odbiorca: Odbiorca): Long

    @Update
    fun update(odbiorca: Odbiorca)

    @Delete
    fun delete(odbiorca: Odbiorca)
}