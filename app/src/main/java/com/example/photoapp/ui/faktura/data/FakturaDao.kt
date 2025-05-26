package com.example.photoapp.ui.faktura.data

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

    @Query("SELECT * FROM Faktura WHERE id = :id")
    fun getById(id: Int): Faktura?

    @Insert
    fun insert(faktura: Faktura): Long

    @Update
    fun update(faktura: Faktura)

    @Delete
    fun delete(faktura: Faktura)

    @Query("""
        SELECT * FROM Faktura 
        WHERE (:startDate IS NULL OR 
              (:filterDate = 'dataWystawienia' AND dataWystawienia >= :startDate) OR 
              (:filterDate = 'dataSprzedazy' AND dataSprzedazy >= :startDate)) 
        AND (:endDate IS NULL OR 
              (:filterDate = 'dataWystawienia' AND dataWystawienia <= :endDate) OR 
              (:filterDate = 'dataSprzedazy' AND dataSprzedazy <= :endDate)) 
        AND (:minPrice IS NULL OR 
            (:filterPrice = 'brutto' AND razemBrutto >= :minPrice) OR
            (:filterPrice = 'netto' AND razemNetto >= :minPrice))
        AND (:maxPrice IS NULL OR
            (:filterPrice = 'brutto' AND razemBrutto <= :maxPrice) OR
            (:filterPrice = 'netto' AND razemNetto <= :maxPrice))
        ORDER BY 
            CASE :filterDate
                WHEN 'dataWystawienia' THEN dataWystawienia 
                WHEN 'dataSprzedazy' THEN dataSprzedazy 
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