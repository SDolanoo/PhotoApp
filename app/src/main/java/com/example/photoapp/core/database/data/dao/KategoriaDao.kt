package com.example.photoapp.core.database.data.dao

import com.example.photoapp.database.data.entities.Kategoria

import androidx.room.*

@Dao
interface KategoriaDao {

    @Query("SELECT * FROM Kategoria")
    fun getAll(): List<Kategoria>

    @Query("SELECT * FROM Kategoria WHERE id = :id")
    fun getById(id: Int): Kategoria?

    @Insert
    fun insert(kategoria: Kategoria): Long

    @Update
    fun update(kategoria: Kategoria)

    @Delete
    fun delete(kategoria: Kategoria)
}
