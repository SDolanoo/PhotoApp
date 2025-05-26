package com.example.photoapp.ui.raportFiskalny.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface RaportFiskalnyDao {

    @Query("SELECT * FROM RaportFiskalny ORDER BY dataDodania")
    fun getAllLive(): LiveData<List<RaportFiskalny>>

    @Query("SELECT * FROM RaportFiskalny")
    fun getAll(): List<RaportFiskalny>

    @Query("SELECT * FROM RaportFiskalny WHERE id = :id")
    fun getById(id: Int): RaportFiskalny

    @Insert
    fun insert(raport: RaportFiskalny): Long

    @Update
    fun update(raport: RaportFiskalny)

    @Delete
    fun delete(raport: RaportFiskalny)
}