package com.example.photoapp.features.faktura.data.faktura

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface ProduktFakturaDao {

    @Query("SELECT * FROM ProduktFaktura")
    fun getAll(): List<ProduktFaktura>

    @Query("SELECT * FROM ProduktFaktura WHERE id IN (:ids)")
    fun getProduktyByIds(ids: List<Long>): List<ProduktFaktura>

    @Query("SELECT * FROM Produkt WHERE id IN (:produktId)")
    fun getProduktForProduktFaktura(produktId: Long): Produkt

    @Query("SELECT * FROM ProduktFaktura WHERE fakturaId IN (:fakturaId)")
    fun getAllProduktFakturaForFakturaId(fakturaId: Long): List<ProduktFaktura>

    @Insert
    fun insert(produktFaktura: ProduktFaktura): Long

    @Upsert
    fun update(produktFaktura: ProduktFaktura)

    @Delete
    fun delete(produktFaktura: ProduktFaktura)
}