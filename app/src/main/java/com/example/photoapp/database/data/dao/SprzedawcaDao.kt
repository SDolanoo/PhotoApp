package com.example.photoapp.database.data.dao

import android.util.Log
import androidx.room.*
import com.example.photoapp.database.data.entities.Sprzedawca

@Dao
interface SprzedawcaDao {

    @Query("SELECT * FROM Sprzedawca")
    fun getAll(): List<Sprzedawca>

    @Query("SELECT * FROM Sprzedawca WHERE id = :id")
    fun getById(id: Int): Sprzedawca?

    @Query("SELECT * FROM Sprzedawca WHERE nip = :nip LIMIT 1")
    fun getByNip(nip: String): Sprzedawca?

    @Insert
    fun insert(sprzedawca: Sprzedawca): Long

    @Update
    fun update(sprzedawca: Sprzedawca)

    @Delete
    fun delete(sprzedawca: Sprzedawca)

    @Transaction
    fun addOrGetSprzedawca(nazwa: String, nip: String, adres: String): Sprzedawca {
        var sprzedawca = getByNip(nip)
        if (sprzedawca == null) {
            sprzedawca = Sprzedawca(1, nazwa, nip, adres)
            sprzedawca.id = insert(sprzedawca).toInt()
        }
        Log.i("Dolan", "Sprzedawca ID: ${sprzedawca.id}, NIP: ${sprzedawca.nip}")
        return sprzedawca
    }
}
