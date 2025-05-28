package com.example.photoapp.core.database.data.repos

import com.example.photoapp.database.data.dao.SprzedawcaDao
import com.example.photoapp.database.data.entities.Sprzedawca
import android.util.Log
import javax.inject.Inject

class SprzedawcaRepository @Inject constructor(
    private val sprzedawcaDao: SprzedawcaDao
) {
    fun getAllSprzedawcy(): List<Sprzedawca> = sprzedawcaDao.getAll()

    fun getByNip(nip: String): Sprzedawca? = sprzedawcaDao.getByNip(nip)

    fun addOrGetSprzedawca(nazwa: String, nip: String, adres: String): Sprzedawca {
        var sprzedawca = getByNip(nip)
        if (sprzedawca == null) {
            sprzedawca = Sprzedawca(1, nazwa, nip, adres)
            sprzedawca.id = sprzedawcaDao.insert(sprzedawca).toInt()
            Log.i("SprzedawcaRepo", "Inserted new sprzedawca: $nip")
        } else {
            Log.i("SprzedawcaRepo", "Using existing sprzedawca: $nip")
        }
        return sprzedawca
    }

    fun update(sprzedawca: Sprzedawca) = sprzedawcaDao.update(sprzedawca)

    fun delete(sprzedawca: Sprzedawca) = sprzedawcaDao.delete(sprzedawca)
}
