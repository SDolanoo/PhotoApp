package com.example.photoapp.features.faktura.data.faktura

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import java.util.Date

@Dao
interface FakturaDao {

    @Query("SELECT * FROM Faktura ORDER BY dataSprzedazy DESC")
    fun getAllLive(): LiveData<List<Faktura>>

    @Query("SELECT * FROM Faktura")
    fun getAllFaktury(): List<Faktura>

    @Query("SELECT * FROM Faktura WHERE id = :id")
    fun getById(id: Long): Faktura?

    @Insert
    fun insert(faktura: Faktura): Long

    @Update
    fun update(faktura: Faktura)

    @Delete
    fun delete(faktura: Faktura)

    @Query("""
    SELECT * FROM Faktura 
    WHERE 
        (
            (:filterDate = 'dataWystawienia' AND 
             (:startDate IS NULL OR dataWystawienia >= :startDate) AND 
             (:endDate IS NULL OR dataWystawienia <= :endDate))
            OR
            (:filterDate = 'dataSprzedazy' AND 
             (:startDate IS NULL OR dataSprzedazy >= :startDate) AND 
             (:endDate IS NULL OR dataSprzedazy <= :endDate))
        )
        AND 
        (
            (:filterPrice = 'brutto' AND 
             (:minPrice IS NULL OR razemBrutto >= :minPrice) AND 
             (:maxPrice IS NULL OR razemBrutto <= :maxPrice))
            OR
            (:filterPrice = 'netto' AND 
             (:minPrice IS NULL OR razemNetto >= :minPrice) AND 
             (:maxPrice IS NULL OR razemNetto <= :maxPrice))
        )
    ORDER BY 
        CASE WHEN :filterDate = 'dataWystawienia' THEN dataWystawienia
             WHEN :filterDate = 'dataSprzedazy' THEN dataSprzedazy
        END DESC
""")
    fun getFilteredFaktury(
        startDate: Date?,
        endDate: Date?,
        minPrice: Double?,
        maxPrice: Double?,
        filterDate: String,
        filterPrice: String
    ): List<Faktura>
}