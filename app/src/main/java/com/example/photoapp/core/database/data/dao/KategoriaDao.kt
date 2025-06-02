package com.example.photoapp.core.database.data.dao

import androidx.room.*
import com.example.photoapp.core.database.data.entities.Kategoria

@Dao
interface KategoriaDao {

    @Query("SELECT * FROM Kategoria")
    fun getAll(): List<Kategoria>

    @Query("SELECT * FROM Kategoria WHERE id = :id")
    fun getById(id: Long): Kategoria?

    @Query("SELECT * FROM Kategoria WHERE nazwa = :name LIMIT 1")
    fun getByName(name: String): Kategoria?

    @Insert
    fun insert(kategoria: Kategoria): Long

    @Update
    fun update(kategoria: Kategoria)

    @Delete
    fun delete(kategoria: Kategoria)
}
