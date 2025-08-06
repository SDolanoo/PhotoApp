package com.example.photoapp.features.sprzedawca.data

import javax.inject.Inject

class SprzedawcaRepository @Inject constructor(
    private val service: SprzedawcaService
) {

    suspend fun getAll(): List<Sprzedawca> = service.getAll()

    suspend fun getByNip(nip: String): Sprzedawca? = service.getByNip(nip)

    suspend fun getById(id: String): Sprzedawca? = service.getById(id)

    suspend fun insert(sprzedawca: Sprzedawca): String = service.insert(sprzedawca)

    suspend fun update(sprzedawca: Sprzedawca) = service.update(sprzedawca)

    suspend fun delete(sprzedawca: Sprzedawca) = service.delete(sprzedawca)

    suspend fun addOrGetSprzedawca(nazwa: String, nip: String, adres: String): Sprzedawca {
        val existing = getByNip(nip)
        return existing ?: run {
            val new = Sprzedawca(
                nazwa = nazwa, nip = nip, adres = adres
            )
            val id = insert(new)
            new.copy(id = id)
        }
    }

    suspend fun upsertSprzedawcaSmart(new: Sprzedawca): String {
        val existingList = getAll()

        return when (val mode = determineSaveModeForSprzedawca(new, existingList)) {
            is SaveMode.Update -> {
                val updated = new.copy(id = mode.existingId)
                update(updated)
                mode.existingId
            }
            SaveMode.Insert -> {
                insert(new.copy(id = ""))
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

            if (normalizedNewNip.isNotEmpty() && normalizedNewNip == normalizedExistingNip)
                return SaveMode.Update(existing.id)

            if (normalizedNewNip.isEmpty() && normalizedNewName == normalizedExistingName)
                return SaveMode.Update(existing.id)

            if (normalizedNewName == normalizedExistingName)
                return SaveMode.Update(existing.id)
        }

        return SaveMode.Insert
    }
}

sealed class SaveMode {
    data class Update(val existingId: String) : SaveMode()
    object Insert : SaveMode()
}
