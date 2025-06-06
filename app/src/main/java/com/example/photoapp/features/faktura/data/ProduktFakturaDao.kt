package com.example.photoapp.features.faktura.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ProduktFakturaDao {

    @Query("SELECT * FROM ProduktFaktura")
    fun getAll(): List<ProduktFaktura>

    @Query("SELECT * FROM ProduktFaktura WHERE fakturaId = :fakturaId")
    fun getProductsByFakturaId(fakturaId: Long): List<ProduktFaktura>

    @Insert
    fun insert(produkt: ProduktFaktura): Long

    @Update
    fun update(produkt: ProduktFaktura)

    @Delete
    fun delete(produkt: ProduktFaktura)
}