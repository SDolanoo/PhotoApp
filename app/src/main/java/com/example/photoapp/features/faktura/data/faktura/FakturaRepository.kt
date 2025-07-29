package com.example.photoapp.features.faktura.data.faktura

import android.util.Log
import com.example.photoapp.core.database.data.FakturaDTO
import com.example.photoapp.features.odbiorca.data.OdbiorcaRepository
import com.example.photoapp.features.sprzedawca.data.SprzedawcaRepository
import com.example.photoapp.core.utils.convertStringToDate
import com.example.photoapp.core.utils.jsonTransformer
import com.example.photoapp.features.odbiorca.data.SaveMode
import com.example.photoapp.features.faktura.presentation.details.ProduktFakturaZProduktem
import com.example.photoapp.features.odbiorca.data.Odbiorca
import com.example.photoapp.features.produkt.data.ProduktDao
import com.example.photoapp.features.produkt.data.ProduktFakturaDao
import com.example.photoapp.features.sprzedawca.data.Sprzedawca
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.util.Date
import javax.inject.Inject

class FakturaRepository @Inject constructor(
    private val fakturaDao: FakturaDao,
    private val produktFakturaDao: ProduktFakturaDao,
    private val produktDao: ProduktDao,
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

    fun getProduktyFakturaForFaktura(faktura: Faktura): List<ProduktFaktura> {
        return runBlocking {
            withContext(Dispatchers.IO) {
                produktFakturaDao.getAllProduktFakturaForFakturaId(faktura.id)
            }
        }
    }

    fun getProduktForProduktFaktura(produktFaktura: ProduktFaktura): Produkt {
        return runBlocking {
            withContext(Dispatchers.IO) {
                produktFakturaDao.getProduktForProduktFaktura(produktFaktura.produktId)
            }
        }
    }

    fun insertFaktura(faktura: Faktura): Long {
        return runBlocking {
            withContext(Dispatchers.IO) {
                fakturaDao.insert(faktura)
            }
        }
    }

    fun insertProduktFaktura(produkt: ProduktFaktura): Long {
        return produktFakturaDao.insert(produkt)
    }

    fun insertProdukt(produkt: Produkt): Long {
        return produktDao.insert(produkt)
    }

    fun upsertProduktSmart(produkt: Produkt): Long {
        val existingList = produktDao.getAll()
        when (val mode = determineSaveModeForProdukt(produkt, existingList)) {
            is SaveMode.Update -> {
                val updated = produkt.copy(id = mode.existingId)
                updateProdukt(updated)
                updated
                return mode.existingId
            }
            SaveMode.Insert -> {
                val newId = insertProdukt(produkt)
                return newId
            }
            SaveMode.Skip -> {
                return 1L
            }
        }
        return 1L
    }

    fun determineSaveModeForProdukt(
        produkt: Produkt,
        existingList: List<Produkt>
    ): SaveMode {
        val normalizedNewName = produkt.nazwaProduktu.trim().lowercase()
        val normalizedNewPrice = produkt.cenaNetto.trim()

        for (existing in existingList) {
            val normalizedExistingName = existing.nazwaProduktu.trim().lowercase()
            val normalizedExistingPrice = existing.cenaNetto.trim()

            // 🔁 MATCH 2: Nazwa ta sama, różne inne dane
            if (normalizedNewName == normalizedExistingName) {
                return SaveMode.Skip
            }
        }

        return SaveMode.Insert
    }

    fun addProductToFaktura(fakturaId: Long, produktFaktura: ProduktFaktura) {
        val faktura = fakturaDao.getById(fakturaId)
        produktFakturaDao.insert(produktFaktura.copy(fakturaId = faktura!!.id))
    }

    fun updateFaktura(faktura: Faktura) {
        fakturaDao.update(faktura)
    }

    fun updateProduktFaktura(produkt: ProduktFaktura) {
        produktFakturaDao.update(produkt)
    }

    fun updateProdukt(produkt: Produkt) {
        produktDao.update(produkt)
    }

    fun deleteFaktura(faktura: Faktura) {
        Log.i("RaportRepo", "Deleting Raport: ${faktura.id}")
        fakturaDao.delete(faktura)
    }

    fun deleteProduktFakturaFromFaktura(produktFaktura: ProduktFaktura) {
        produktFakturaDao.delete(produktFaktura)
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
            miejsceWystawienia = ""
        )

        val fakturaId = fakturaDao.insert(faktura)

        fakturaDTO.produkty.forEach { dto ->
            val produkt = Produkt(
                nazwaProduktu = dto.nazwaProduktu,
                jednostkaMiary = dto.jednostkaMiary,
                cenaNetto = dto.cenaNetto,
                stawkaVat = dto.stawkaVat
            )

            val produktId = produktDao.insert(produkt)

            val pozycja = ProduktFaktura(
                fakturaId = fakturaId,
                produktId = produktId,
                ilosc = dto.ilosc,
                rabat = dto.rabat,
                wartoscNetto = dto.wartoscNetto,
                wartoscBrutto = dto.wartoscBrutto
            )

            produktFakturaDao.insert(pozycja)
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
        return runBlocking {
            withContext(Dispatchers.IO) {
                fakturaDao.getFilteredFaktury(
                    startDate,
                    endDate,
                    minPrice,
                    maxPrice,
                    filterDate,
                    filterPrice
                )
            }
        }
    }

    fun getAllProdukty(): List<Produkt> {
        return produktDao.getAll()
    }

    fun getProduktById(id: Long): Produkt {
        return produktDao.getOneProduktById(id)
    }

    fun getListProduktyFakturaZProduktemForListFaktura(faktury: List<Faktura>): List<ProduktFakturaZProduktem> {
        val resultList = mutableListOf<ProduktFakturaZProduktem>()

        for (faktura in faktury) {
            val produktyFaktura = getProduktyFakturaForFaktura(faktura)
            for (produktFaktura in produktyFaktura) {
                val produkt: Produkt = getProduktForProduktFaktura(produktFaktura)
                val pfzp: ProduktFakturaZProduktem = ProduktFakturaZProduktem(
                    produktFaktura = produktFaktura,
                    produkt = produkt
                )
                resultList.add(pfzp)
            }
        }
        return resultList
    }


}