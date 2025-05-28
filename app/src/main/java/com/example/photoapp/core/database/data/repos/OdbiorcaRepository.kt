package com.example.photoapp.core.database.data.repos

import com.example.photoapp.database.data.dao.OdbiorcaDao
import com.example.photoapp.database.data.entities.Odbiorca
import android.util.Log
import javax.inject.Inject

class OdbiorcaRepository @Inject constructor(
    private val odbiorcaDao: OdbiorcaDao
) {
    fun getAllOdbiorcy(): List<Odbiorca> = odbiorcaDao.getAll()

    fun getByNip(nip: String): Odbiorca? = odbiorcaDao.getByNip(nip)

    fun addOrGetOdbiorca(nazwa: String, nip: String, adres: String): Odbiorca {
        var odbiorca = getByNip(nip)
        if (odbiorca == null) {
            odbiorca = Odbiorca(1, nazwa, nip, adres)
            odbiorca.id = odbiorcaDao.insert(odbiorca).toInt()
            Log.i("OdbiorcaRepo", "Inserted new odbiorca: $nip")
        } else {
            Log.i("OdbiorcaRepo", "Using existing odbiorca: $nip")
        }
        return odbiorca
    }

    fun update(odbiorca: Odbiorca) = odbiorcaDao.update(odbiorca)

    fun delete(odbiorca: Odbiorca) = odbiorcaDao.delete(odbiorca)
}
