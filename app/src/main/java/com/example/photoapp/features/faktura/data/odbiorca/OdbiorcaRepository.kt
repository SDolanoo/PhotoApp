package com.example.photoapp.features.faktura.data.odbiorca

import android.util.Log
import com.example.photoapp.features.faktura.data.sprzedawca.Sprzedawca
import javax.inject.Inject

class OdbiorcaRepository @Inject constructor(
    private val odbiorcaDao: OdbiorcaDao
) {
    fun getAllOdbiorcy(): List<Odbiorca> = odbiorcaDao.getAll()

    fun getByNip(nip: String): Odbiorca? = odbiorcaDao.getByNip(nip)

    fun getById(id: Long): Odbiorca? = odbiorcaDao.getById(id)

    fun addOrGetOdbiorca(nazwa: String, nip: String, adres: String): Odbiorca {
        return getByNip(nip)?.also {
            Log.i("Dolan", "Existing Odbiorca ID: ${it.id}, NIP: ${it.nip}")
        } ?: run {
            val odbiorca = Odbiorca(
                nazwa = nazwa, nip = nip, adres = adres,
                kodPocztowy = "",
                miejscowosc = "",
                kraj = "",
                opis = "",
                email = "",
                telefon = ""
            )
            val id = insert(odbiorca)
            val newOdbiorca = odbiorca.copy(id = id)
            Log.i("Dolan", "Inserted new Odbiorca ID: ${newOdbiorca.id}, NIP: ${newOdbiorca.nip}")
            newOdbiorca
        }
    }

    fun insert(odbiorca: Odbiorca): Long = odbiorcaDao.insert(odbiorca)

    fun update(odbiorca: Odbiorca) = odbiorcaDao.update(odbiorca)

    fun delete(odbiorca: Odbiorca) = odbiorcaDao.delete(odbiorca)
}