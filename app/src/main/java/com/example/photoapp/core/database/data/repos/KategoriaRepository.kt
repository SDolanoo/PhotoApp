package com.example.photoapp.core.database.data.repos

import com.example.photoapp.core.database.data.dao.KategoriaDao
import com.example.photoapp.core.database.data.entities.Kategoria
import javax.inject.Inject

class KategoriaRepository @Inject constructor(
    private val kategoriaDao: KategoriaDao
) {
    fun getAllKategorii(): List<Kategoria> = kategoriaDao.getAll()

    fun getById(id: Int): Kategoria? = kategoriaDao.getById(id)

    fun getByName(name: String): Kategoria? = kategoriaDao.getByName(name)

    fun insert(kategoria: Kategoria): Long = kategoriaDao.insert(kategoria)

    fun update(kategoria: Kategoria) = kategoriaDao.update(kategoria)

    fun delete(kategoria: Kategoria) = kategoriaDao.delete(kategoria)
}
