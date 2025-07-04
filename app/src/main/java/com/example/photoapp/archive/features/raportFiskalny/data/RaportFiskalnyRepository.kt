package com.example.photoapp.archive.features.raportFiskalny.data

import android.util.Log
import com.example.photoapp.core.database.data.OnlyProduktyRaportFiskalnyDTO
import com.example.photoapp.core.database.data.RaportFiskalnyDTO
import com.example.photoapp.core.utils.jsonTransformer
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import javax.inject.Inject

class RaportFiskalnyRepository @Inject constructor(
    private val raportDao: RaportFiskalnyDao,
    private val produktDao: ProduktRaportFiskalnyDao
) {

    fun getAllLiveRaporty() = raportDao.getAllLive()

    fun getRaportById(id: Long): RaportFiskalny = raportDao.getById(id)

    fun getProduktyForRaportId(id: Long): List<ProduktRaportFiskalny> =
        produktDao.getProductsByRaportId(id)

    fun insertRaport(raport: RaportFiskalny): Long = raportDao.insert(raport)

    fun insertProdukt(produkt: ProduktRaportFiskalny) {
        produktDao.insert(produkt)
    }

    fun updateRaport(raport: RaportFiskalny) {
        raportDao.update(raport)
    }

    fun updateProdukt(produkt: ProduktRaportFiskalny) {
        produktDao.update(produkt)
    }

    fun deleteRaport(raport: RaportFiskalny) {
        Log.i("RaportRepo", "Deleting Raport: ${raport.id}")
        getProduktyForRaportId(raport.id).forEach {
            produktDao.delete(it)
        }
        raportDao.delete(raport)
    }

    fun deleteProdukt(produkt: ProduktRaportFiskalny) {
        produktDao.delete(produkt)
    }

    fun addRaportFromJson(jsonString: String): Long {
        val coercingJson = Json { coerceInputValues = true }
        val transformedJson = jsonTransformer(jsonString)
        val dto = coercingJson.decodeFromString<RaportFiskalnyDTO>(transformedJson)

        val raport = RaportFiskalny(
            dataDodania = SimpleDateFormat("yyyy-MM-dd").parse(dto.dataDodania)
        )

        val raportId = raportDao.insert(raport)
        Log.i("RaportRepo", "Inserted Raport: $raportId")

        dto.produkty.forEach { produktDTO ->
            val produkt = ProduktRaportFiskalny(
                raportFiskalnyId = raportId,
                nrPLU = produktDTO.nrPLU,
                ilosc = produktDTO.ilosc
            )
            produktDao.insert(produkt)
        }

        checkForDuplicatePLU(raportId)
        return raportId
    }

    fun addProduktyFromJson(jsonInput: String, raport: RaportFiskalny) {
        val coercingJson = Json { coerceInputValues = true }
        val transformedJson = jsonTransformer(jsonInput)
        val produkty = coercingJson.decodeFromString<OnlyProduktyRaportFiskalnyDTO>(transformedJson)

        produkty.produkty.forEach { produktDTO ->
            val produkt = ProduktRaportFiskalny(
                raportFiskalnyId = raport.id,
                nrPLU = produktDTO.nrPLU,
                ilosc = produktDTO.ilosc
            )
            produktDao.insert(produkt)
        }

        checkForDuplicatePLU(raport.id)
    }

    private fun checkForDuplicatePLU(raportId: Long) {
        val produkty = getProduktyForRaportId(raportId)
        val seen = mutableSetOf<Int>()

        produkty.forEach { produkt ->
            val plu = produkt.nrPLU.toInt()
            if (seen.contains(plu)) {
                produktDao.delete(produkt)
                Log.i("RaportRepo", "Duplicate PLU deleted: $plu")
            } else {
                seen.add(plu)
                Log.i("RaportRepo", "PLU kept: $plu")
            }
        }
    }


}
