package com.example.photoapp.features.faktura.data.faktura

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface ProduktDao {

    @Query("SELECT * FROM Produkt")
    fun getAll(): List<Produkt>

    @Query("SELECT * FROM Produkt WHERE id IN (:ids)")
    fun getProduktyByIds(ids: List<Long>): List<Produkt>

    @Query("SELECT * FROM Produkt WHERE id  = :id")
    fun getOneProduktById(id: Long): List<Produkt>

    @Insert
    fun insert(produkt: Produkt): Long

    @Upsert
    fun update(produkt: Produkt)

    @Delete
    fun delete(produkt: Produkt)

}