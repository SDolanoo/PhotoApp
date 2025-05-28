package com.example.photoapp.features.paragon.data

import android.util.Log
import com.example.photoapp.core.database.data.ParagonDTO
import com.example.photoapp.core.utils.jsonTransformer
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

    fun getParagonById(id: Int): Paragon? = paragonDao.getById(id)

    fun getProduktyForParagonId(id: Int): List<ProduktParagon> =
        produktParagonDao.getByParagonId(id)

    fun insertParagon(paragon: Paragon): Long = paragonDao.insert(paragon)

    fun insertProdukt(produkt: ProduktParagon) {
        produktParagonDao.insert(produkt)
    }

    fun updateParagon(paragon: Paragon) {
        paragonDao.update(paragon)
    }

    fun updateProdukt(produkt: ProduktParagon) {
        produktParagonDao.update(produkt)
    }

    fun deleteParagon(paragon: Paragon) {
        Log.i("ParagonRepo", "Deleting Paragon: ${paragon.id}")
        getProduktyForParagonId(paragon.id).forEach {
            produktParagonDao.delete(it)
        }
        paragonDao.delete(paragon)
    }

    fun deleteProdukt(produkt: ProduktParagon) {
        produktParagonDao.delete(produkt)
    }

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

//    fun addParagonFromJson(jsonString: String): Long {
//        val coercingJson = Json { coerceInputValues = true }
//        val transformedJson = jsonTransformer(jsonString)
//        val dto = coercingJson.decodeFromString<ParagonDTO>(transformedJson)
//
//        val paragon = Paragon(
//            uzytkownikId = 1,
//            dataZakupu = SimpleDateFormat("yyyy-MM-dd").parse(dto.dataZakupu),
//            nazwaSklepu = dto.nazwaSklepu,
//            kwotaCalkowita = dto.kwotaCalkowita.replace(",", ".").toDouble()
//        )
//
//        val paragonId = paragonDao.insert(paragon)
//        Log.i("ParagonRepo", "Inserted Paragon: $paragonId")
//
//        dto.produkty.forEach { produktDTO ->
//            val produkt = ProduktParagon(
//                paragonId = paragonId.toInt(),
//                kategoriaId = null,
//                nazwaProduktu = produktDTO.nazwaProduktu,
//                ilosc = produktDTO.ilosc.toDouble().toInt(),
//                cenaSuma = produktDTO.cenaSuma.replace(",", ".").toDouble()
//            )
//            produktParagonDao.insert(produkt)
//        }
//
////        checkForDuplicateNames(paragonId.toInt())
//        return paragonId
//    }

//    fun addProduktyFromJson(jsonInput: String, paragon: Paragon) {
//        val coercingJson = Json { coerceInputValues = true }
//        val transformedJson = jsonTransformer(jsonInput)
//        val produkty = coercingJson.decodeFromString<OnlyProduktyParagonDTO>(transformedJson)
//
//        produkty.produkty.forEach { produktDTO ->
//            val produkt = ProduktParagon(
//                paragonId = paragon.id,
//                kategoriaId = null,
//                nazwaProduktu = produktDTO.nazwaProduktu,
//                ilosc = produktDTO.ilosc.toDouble().toInt(),
//                cenaSuma = produktDTO.cenaSuma.replace(",", ".").toDouble()
//            )
//            produktParagonDao.insert(produkt)
//        }
//
//        checkForDuplicateNames(paragon.id)
//    }

//    private fun checkForDuplicateNames(paragonId: Int) {
//        val produkty = getProduktyForParagonId(paragonId)
//        val seen = mutableSetOf<String>()
//
//        produkty.forEach { produkt ->
//            val name = produkt.nazwaProduktu.trim().lowercase()
//            if (seen.contains(name)) {
//                produktParagonDao.delete(produkt)
//                Log.i("ParagonRepo", "Duplicate Produkt deleted: $name")
//            } else {
//                seen.add(name)
//                Log.i("ParagonRepo", "Produkt kept: $name")
//            }
//        }
//    }
}
