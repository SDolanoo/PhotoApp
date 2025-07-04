package com.example.photoapp.archive.features.paragon.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import java.util.Date

@Dao
interface ParagonDao {

    @Query("SELECT * FROM Paragon ORDER BY dataZakupu DESC")
    fun getAllLive(): LiveData<List<Paragon>>

    @Query("SELECT * FROM Paragon")
    fun getAll(): List<Paragon>

    @Query("SELECT * FROM Paragon WHERE id = :id")
    fun getById(id: Long): Paragon?

    @Insert
    fun insert(paragon: Paragon): Long

    @Update
    fun update(paragon: Paragon)

    @Delete
    fun delete(paragon: Paragon)

    @Query("""
        SELECT * FROM Paragon 
        WHERE (:startDate IS NULL OR dataZakupu >= :startDate) 
        AND (:endDate IS NULL OR dataZakupu <= :endDate) 
        AND (:minPrice IS NULL OR kwotaCalkowita >= :minPrice) 
        AND (:maxPrice IS NULL OR kwotaCalkowita <= :maxPrice)
        ORDER BY dataZakupu DESC
    """)
    fun getFilteredParagony(
        startDate: Date?,
        endDate: Date?,
        minPrice: Double?,
        maxPrice: Double?
    ): List<Paragon>
}