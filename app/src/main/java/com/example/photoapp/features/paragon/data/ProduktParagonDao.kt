package com.example.photoapp.features.paragon.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ProduktParagonDao {

    @Query("SELECT * FROM ProduktParagon")
    fun getAll(): List<ProduktParagon>

    @Query("SELECT * FROM ProduktParagon WHERE paragonId = :paragonId")
    fun getByParagonId(paragonId: Int): List<ProduktParagon>

    @Insert
    fun insert(produkt: ProduktParagon): Long

    @Update
    fun update(produkt: ProduktParagon)

    @Delete
    fun delete(produkt: ProduktParagon)
}