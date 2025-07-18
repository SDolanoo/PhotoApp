package com.example.photoapp.features.faktura.data.faktura

import android.util.Log
import androidx.compose.runtime.DisposableEffect
import com.example.photoapp.core.database.data.FakturaDTO
import com.example.photoapp.features.faktura.data.odbiorca.OdbiorcaRepository
import com.example.photoapp.features.faktura.data.sprzedawca.SprzedawcaRepository
import com.example.photoapp.core.utils.convertStringToDate
import com.example.photoapp.core.utils.jsonTransformer
import com.example.photoapp.features.faktura.data.odbiorca.Odbiorca
import com.example.photoapp.features.faktura.data.sprzedawca.Sprzedawca
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.apache.commons.math3.stat.StatUtils.product
import java.util.Date
import javax.inject.Inject

class FakturaRepository @Inject constructor(
    private val fakturaDao: FakturaDao,
    private val produktFakturaDao: ProduktFakturaDao,
    private val odbiorcaRepository: OdbiorcaRepository,
    private val sprzedawcaRepository: SprzedawcaRepository
) {

    fun getAllLiveFaktury() = fakturaDao.getAllLive()

    fun getAllFaktury(): List<Faktura> {
        return runBlocking {
            withContext(Dispatchers.IO) {
                fakturaDao.getAllFaktury()
            }
        }
    }

    fun getFakturaByID(id: Long): Faktura? = fakturaDao.getById(id)

    fun getProduktyForFaktura(faktura: Faktura): List<ProduktFaktura> {
        return runBlocking {
            withContext(Dispatchers.IO) {
                produktFakturaDao.getProduktyByIds(faktura.produktyId)
            }
        }
    }

    fun insertFaktura(faktura: Faktura) {
        fakturaDao.insert(faktura)
    }

    fun insertProdukt(produkt: ProduktFaktura): Long {
        return produktFakturaDao.insert(produkt)
    }

    fun addProductToFaktura(fakturaId: Long, newProductId: Long) {
        val faktura = fakturaDao.getById(fakturaId)
        val updatedList = faktura!!.produktyId.toMutableList().apply {
            if (!contains(newProductId)) add(newProductId)
        }
        val updatedFaktura = faktura.copy(produktyId = updatedList)
        fakturaDao.update(updatedFaktura)
    }

    fun updateFaktura(faktura: Faktura) {
        fakturaDao.update(faktura)
    }

    fun updateProdukt(produkt: ProduktFaktura) {
        produktFakturaDao.update(produkt)
    }

    fun deleteFaktura(faktura: Faktura) {
        Log.i("RaportRepo", "Deleting Raport: ${faktura.id}")
        fakturaDao.delete(faktura)
    }

    fun deleteProduktFromFaktura(produkt: ProduktFaktura, faktura: Faktura) {
        val updatedList = faktura.produktyId.filter { it != produkt.id }

        val updatedFaktura = faktura.copy(produktyId = updatedList)

        fakturaDao.update(updatedFaktura)
    }

    fun addFakturaFromJson(jsonString: String) {
        val coercingJson = Json { coerceInputValues = true }
        val transformedJson = jsonTransformer(jsonString)
        val fakturaDTO = coercingJson.decodeFromString<FakturaDTO>(transformedJson)

        val odbiorca: Odbiorca = odbiorcaRepository.addOrGetOdbiorca(
            fakturaDTO.odbiorca.nazwa,
            fakturaDTO.odbiorca.nip,
            fakturaDTO.odbiorca.adres
        )
        val sprzedawca: Sprzedawca = sprzedawcaRepository.addOrGetSprzedawca(
            fakturaDTO.sprzedawca.nazwa,
            fakturaDTO.sprzedawca.nip,
            fakturaDTO.sprzedawca.adres
        )

        val faktura = Faktura(
            uzytkownikId = 1,
            odbiorcaId = odbiorca.id,
            sprzedawcaId = sprzedawca.id,
            typFaktury = fakturaDTO.typFaktury,
            numerFaktury = fakturaDTO.numerFaktury,
            dataWystawienia = convertStringToDate(fakturaDTO.dataWystawienia),
            dataSprzedazy = convertStringToDate(fakturaDTO.dataSprzedazy),
            razemNetto = fakturaDTO.razemNetto,
            razemVAT = fakturaDTO.razemVAT ?: "null",
            razemBrutto = fakturaDTO.razemBrutto,
            doZaplaty = fakturaDTO.doZaplaty,
            waluta = fakturaDTO.waluta,
            formaPlatnosci = fakturaDTO.formaPlatnosci,
            miejsceWystawienia = "",
            produktyId = emptyList()
        )

        val fakturaId = fakturaDao.insert(faktura)

        fakturaDTO.produkty.forEach { produktDTO ->
            val produkt = ProduktFaktura(
                nazwaProduktu = produktDTO.nazwaProduktu,
                ilosc = produktDTO.ilosc,
                jednostkaMiary = produktDTO.jednostkaMiary,
                cenaNetto = produktDTO.cenaNetto,
                stawkaVat = produktDTO.stawkaVat,
                wartoscNetto = produktDTO.wartoscNetto,
                wartoscBrutto = produktDTO.wartoscBrutto,
                rabat = produktDTO.rabat,
                pkwiu = produktDTO.pkwiu,
            )
            val productId = insertProdukt(produkt)
            addProductToFaktura(fakturaId, productId)
        }

        Log.i("FakturaRepository", "Inserted Faktura and ${fakturaDTO.produkty.size} products")
    }

    fun fetchFilteredFaktury(
        startDate: Date?,
        endDate: Date?,
        minPrice: Double?,
        maxPrice: Double?,
        filterDate: String,
        filterPrice: String
    ): List<Faktura> {
        return fakturaDao.getFilteredFaktury(
            startDate,
            endDate,
            minPrice,
            maxPrice,
            filterDate,
            filterPrice
        )
    }

    fun getAllProdukty(): List<ProduktFaktura> {
        return produktFakturaDao.getAll()
    }
}