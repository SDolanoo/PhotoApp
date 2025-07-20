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

    fun upsertOdbiorcaSmart(new: Odbiorca): Long {
        val existingList = getAllOdbiorcy()

        when (val mode = determineSaveModeForOdbiorca(new, existingList)) {
            is SaveMode.Update -> {
                val updated = new.copy(id = mode.existingId)
                update(updated)
                updated
                return mode.existingId
            }
            SaveMode.Insert -> {
                val newId = insert(new)
                new.copy(id = newId)
                return newId
            }
        }
    }


    fun determineSaveModeForOdbiorca(
        new: Odbiorca,
        existingList: List<Odbiorca>
    ): SaveMode {
        val normalizedNewName = new.nazwa.trim().lowercase()
        val normalizedNewNip = new.nip.trim()

        for (existing in existingList) {
            val normalizedExistingName = existing.nazwa.trim().lowercase()
            val normalizedExistingNip = existing.nip.trim()

            // 🔁 MATCH 1: Ten sam NIP (priorytetowo)
            if (normalizedNewNip.isNotEmpty() &&
                normalizedNewNip == normalizedExistingNip
            ) {
                return SaveMode.Update(existing.id)
            }

            // 🔁 MATCH 2: Brak NIPU, ale ta sama nazwa
            if (normalizedNewNip.isEmpty() &&
                normalizedNewName == normalizedExistingName
            ) {
                return SaveMode.Update(existing.id)
            }

            // 🔁 MATCH 3: Nazwa ta sama, różne inne dane
            if (normalizedNewName == normalizedExistingName) {
                return SaveMode.Update(existing.id)
            }
        }

        return SaveMode.Insert
    }

}

sealed class SaveMode {
    data class Update(val existingId: Long) : SaveMode()
    object Insert : SaveMode()
}
