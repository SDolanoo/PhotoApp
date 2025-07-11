package com.example.photoapp.features.faktura.data.sprzedawca

import android.util.Log
import javax.inject.Inject

class SprzedawcaRepository @Inject constructor(
    private val sprzedawcaDao: SprzedawcaDao
) {
    fun getAll(): List<Sprzedawca> = sprzedawcaDao.getAll()

    fun getByNip(nip: String): Sprzedawca? = sprzedawcaDao.getByNip(nip)

    fun getById(id: Long): Sprzedawca? = sprzedawcaDao.getById(id)

    fun addOrGetSprzedawca(nazwa: String, nip: String, adres: String): Sprzedawca {
        return getByNip(nip)?.also {
            Log.i("Dolan", "Existing Sprzedawca ID: ${it.id}, NIP: ${it.nip}")
        } ?: run {
            val sprzedawca = Sprzedawca(nazwa = nazwa, nip = nip, adres = adres,
                kodPocztowy = "",
                miejscowosc = "",
                kraj = "",
                opis = "",
                email = "",
                telefon = "")
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