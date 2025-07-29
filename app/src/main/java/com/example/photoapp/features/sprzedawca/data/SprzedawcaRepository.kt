package com.example.photoapp.features.sprzedawca.data

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

    fun upsertSprzedawcaSmart(new: Sprzedawca): Long {
        val existingList = getAll()

        when (val mode = determineSaveModeForSprzedawca(new, existingList)) {
            is SaveMode.Update -> {
                val updated = new.copy(id = mode.existingId)
                update(updated)
                updated
                return mode.existingId
            }
            SaveMode.Insert -> {
                val newId = insert(new.copy(id = 0L))
                new.copy(id = newId)
                return newId
            }
        }
    }


    fun determineSaveModeForSprzedawca(
        new: Sprzedawca,
        existingList: List<Sprzedawca>
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
                Log.i("Dolan", "UPDATE")
                return SaveMode.Update(existing.id)
            }

            // 🔁 MATCH 2: Brak NIPU, ale ta sama nazwa
            if (normalizedNewNip.isEmpty() &&
                normalizedNewName == normalizedExistingName
            ) {
                Log.i("Dolan", "UPDATE")
                return SaveMode.Update(existing.id)
            }

            // 🔁 MATCH 3: Nazwa ta sama, różne inne dane
            if (normalizedNewName == normalizedExistingName) {
                Log.i("Dolan", "UPDATE")
                return SaveMode.Update(existing.id)
            }
        }
        Log.i("Dolan", "INSERT")
        return SaveMode.Insert
    }

}

sealed class SaveMode {
    data class Update(val existingId: Long) : SaveMode()
    object Insert : SaveMode()
}
