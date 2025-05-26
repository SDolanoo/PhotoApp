package com.example.photoapp.ui.paragon.data

import android.util.Log
import com.example.photoapp.database.data.ParagonDTO
import com.example.photoapp.utils.jsonTransformer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ParagonRepository @Inject constructor(
    private val paragonDao: ParagonDao,
    private val produktParagonDao: ProduktParagonDao
) {

    fun getAllLiveParagony() = paragonDao.getAllLive()

    fun getAllParagony(): List<Paragon> = paragonDao.getAll()

    fun getProductForParagon(paragonId: Int): List<ProduktParagon> =
        produktParagonDao.getByParagonId(paragonId)

    fun fetchFilteredParagony(
        startDate: Date?,
        endDate: Date?,
        minPrice: Double?,
        maxPrice: Double?
    ): List<Paragon> = paragonDao.getFilteredParagony(startDate, endDate, minPrice, maxPrice)

    fun addRecipeFromJson(jsonString: String) {
        val coercingJson = Json { coerceInputValues = true }
        val transformedJson = jsonTransformer(jsonString)
        val paragonDTO = coercingJson.decodeFromString<ParagonDTO>(transformedJson)

        val paragon = Paragon(
            uzytkownikId = 1,
            dataZakupu = SimpleDateFormat("yyyy-MM-dd").parse(paragonDTO.dataZakupu),
            nazwaSklepu = paragonDTO.nazwaSklepu,
            kwotaCalkowita = paragonDTO.kwotaCalkowita.replace(",", ".").toDouble()
        )

        val paragonId = paragonDao.insert(paragon).toInt()

        paragonDTO.produkty.forEach { produktDTO ->
            val produktParagon = ProduktParagon(
                paragonId = paragonId,
                kategoriaId = null,
                nazwaProduktu = produktDTO.nazwaProduktu,
                ilosc = produktDTO.ilosc.toDouble().toInt(),
                cenaSuma = produktDTO.cenaSuma.replace(",", ".").toDouble()
            )
            produktParagonDao.insert(produktParagon)
        }

        Log.i("ParagonRepository", "Inserted Paragon + Produkty with ID: $paragonId")
    }

//    fun addTestParagony() {
//        paragonDao.addTestRecipe()
//    }
//
//    fun addTestProducts() {
//        produktParagonDao.addTestRecipeProducts(paragonDao)
//    }
}
