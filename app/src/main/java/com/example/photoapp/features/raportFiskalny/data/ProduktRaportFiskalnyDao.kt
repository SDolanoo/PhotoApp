package com.example.photoapp.features.raportFiskalny.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ProduktRaportFiskalnyDao {

    @Query("SELECT * FROM ProduktRaportFiskalny WHERE raportFiskalnyId = :raportFiskalnyId ORDER BY CAST(nrPLU AS INTEGER) ASC")
    fun getByRaportId(raportFiskalnyId: Int): List<ProduktRaportFiskalny>

    @Insert
    fun insert(produkt: ProduktRaportFiskalny): Long

    @Update
    fun update(produkt: ProduktRaportFiskalny)

    @Delete
    fun delete(produkt: ProduktRaportFiskalny)
}