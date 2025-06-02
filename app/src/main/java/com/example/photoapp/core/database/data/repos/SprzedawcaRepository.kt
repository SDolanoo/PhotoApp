package com.example.photoapp.core.database.data.repos

import android.util.Log
import com.example.photoapp.core.database.data.dao.SprzedawcaDao
import com.example.photoapp.core.database.data.entities.Sprzedawca
import javax.inject.Inject

class SprzedawcaRepository @Inject constructor(
    private val sprzedawcaDao: SprzedawcaDao
) {
    fun getAll(): List<Sprzedawca> = sprzedawcaDao.getAll()

    fun getByNip(nip: String): Sprzedawca? = sprzedawcaDao.getByNip(nip)

    fun addOrGetSprzedawca(nazwa: String, nip: String, adres: String): Sprzedawca {
        return getByNip(nip)?.also {
            Log.i("Dolan", "Existing Sprzedawca ID: ${it.id}, NIP: ${it.nip}")
        } ?: run {
            val sprzedawca = Sprzedawca(nazwa = nazwa, nip = nip, adres = adres)
            var id = insert(sprzedawca)
            var newSprzedawca = sprzedawca.copy(id = id)
            Log.i("Dolan", "Inserted new Odbiorca ID: ${newSprzedawca.id}, NIP: ${newSprzedawca.nip}")
            newSprzedawca
        }
    }

    fun insert(sprzedawca: Sprzedawca): Long = sprzedawcaDao.insert(sprzedawca)

    fun update(sprzedawca: Sprzedawca) = sprzedawcaDao.update(sprzedawca)

    fun delete(sprzedawca: Sprzedawca) = sprzedawcaDao.delete(sprzedawca)
}
