package com.example.photoapp.core.database.data.dao

import com.example.photoapp.database.data.entities.Odbiorca

import android.util.Log
import androidx.room.*

@Dao
interface OdbiorcaDao {

    @Query("SELECT * FROM Odbiorca")
    fun getAll(): List<Odbiorca>

    @Query("SELECT * FROM Odbiorca WHERE nip = :nip LIMIT 1")
    fun getByNip(nip: String): Odbiorca?

    @Insert
    fun insert(odbiorca: Odbiorca): Long

    @Update
    fun update(odbiorca: Odbiorca)

    @Delete
    fun delete(odbiorca: Odbiorca)

    @Transaction
    fun addOrGetOdbiorca(nazwa: String, nip: String, adres: String): Odbiorca {
        var odbiorca = getByNip(nip)
        if (odbiorca == null) {
            odbiorca = Odbiorca(nazwa, nip, adres)
            odbiorca.id = insert(odbiorca).toInt()
        }
        Log.i("Dolan", "Odbiorca ID: ${odbiorca.id}, NIP: ${odbiorca.nip}")
        return odbiorca
    }
}
