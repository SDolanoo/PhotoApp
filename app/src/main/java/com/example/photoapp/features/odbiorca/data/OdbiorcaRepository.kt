package com.example.photoapp.features.odbiorca.data

import javax.inject.Inject

class OdbiorcaRepository @Inject constructor(
    private val service: OdbiorcaService
) {

    suspend fun getAllOdbiorcy(): List<Odbiorca> = service.getAll()

    suspend fun getByNip(nip: String): Odbiorca? = service.getByNip(nip)

    suspend fun getById(id: String): Odbiorca? = service.getById(id)

    suspend fun insert(odbiorca: Odbiorca): String = service.insert(odbiorca)

    suspend fun update(odbiorca: Odbiorca) = service.update(odbiorca)

    suspend fun delete(odbiorca: Odbiorca) = service.delete(odbiorca)

    suspend fun addOrGetOdbiorca(nazwa: String, nip: String, adres: String): Odbiorca {
        val existing = getByNip(nip)
        return existing ?: run {
            val newOdbiorca = Odbiorca(
                nazwa = nazwa, nip = nip, adres = adres
            )
            val id = insert(newOdbiorca)
            newOdbiorca.copy(id = id)
        }
    }

    suspend fun upsertOdbiorcaSmart(new: Odbiorca): String {
        val existingList = getAllOdbiorcy()

        return when (val mode = determineSaveModeForOdbiorca(new, existingList)) {
            is SaveMode.Update -> {
                val updated = new.copy(id = mode.existingId)
                update(updated)
                mode.existingId
            }

            SaveMode.Insert -> {
                insert(new.copy(id = ""))
            }

            SaveMode.Skip -> {
                insert(new.copy(id = ""))
            }
        }.toString()
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

            if (normalizedNewNip.isNotEmpty() && normalizedNewNip == normalizedExistingNip) {
                return SaveMode.Update(existing.id)
            }

            if (normalizedNewNip.isEmpty() && normalizedNewName == normalizedExistingName) {
                return SaveMode.Update(existing.id)
            }

            if (normalizedNewName == normalizedExistingName) {
                return SaveMode.Update(existing.id)
            }
        }

        return SaveMode.Insert
    }
}

sealed class SaveMode {
    data class Update(val existingId: String) : SaveMode()
    object Insert : SaveMode()
    object Skip : SaveMode()
}
