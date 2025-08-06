package com.example.photoapp.features.faktura.data.faktura

import android.util.Log
import com.example.photoapp.core.database.data.FakturaDTO
import com.example.photoapp.features.odbiorca.data.OdbiorcaRepository
import com.example.photoapp.features.sprzedawca.data.SprzedawcaRepository
import com.example.photoapp.core.utils.convertStringToDate
import com.example.photoapp.core.utils.jsonTransformer
import com.example.photoapp.features.faktura.presentation.details.ProduktFakturaZProduktem
import com.example.photoapp.features.odbiorca.data.Odbiorca
import com.example.photoapp.features.odbiorca.data.SaveMode
import com.example.photoapp.features.produkt.data.Produkt
import com.example.photoapp.features.produkt.data.ProduktFaktura
import com.example.photoapp.features.produkt.data.ProduktFakturaService
import com.example.photoapp.features.produkt.data.ProduktService
import com.example.photoapp.features.sprzedawca.data.Sprzedawca
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.util.Date
import javax.inject.Inject

class FakturaRepository @Inject constructor(
    private val fakturaService: FakturaService,
    private val produktFakturaService: ProduktFakturaService,
    private val produktService: ProduktService,
    private val odbiorcaRepository: OdbiorcaRepository,
    private val sprzedawcaRepository: SprzedawcaRepository
) {
    fun getAllLiveFaktury(): Flow<List<Faktura>> = fakturaService.getAllLive()

    fun getAllFaktury(): List<Faktura> = runBlocking {
        withContext(Dispatchers.IO) {
            fakturaService.getAllFaktury()
        }
    }

    fun getFakturaByID(id: String): Faktura? = runBlocking {
        withContext(Dispatchers.IO) {
            fakturaService.getById(id)
        }
    }

    fun getProduktyFakturaForFaktura(faktura: Faktura): List<ProduktFaktura> = runBlocking {
        withContext(Dispatchers.IO) {
            produktFakturaService.getAllProduktFakturaForFakturaId(faktura.id)
        }
    }

    fun getProduktForProduktFaktura(produktFaktura: ProduktFaktura): Produkt = runBlocking {
        withContext(Dispatchers.IO) {
            produktFakturaService.getProduktForProduktFaktura(produktFaktura.produktId)
        }
    }

    fun insertFaktura(faktura: Faktura): String = runBlocking {
        withContext(Dispatchers.IO) {
            fakturaService.insert(faktura)
        }
    }

    fun insertProduktFaktura(produkt: ProduktFaktura): String = runBlocking {
        withContext(Dispatchers.IO) {
            produktFakturaService.insert(produkt)
        }
    }

    fun insertProdukt(produkt: Produkt): String = runBlocking {
        withContext(Dispatchers.IO) {
            produktService.insert(produkt)
        }
    }

    fun upsertProduktSmart(produkt: Produkt): String {
        val existingList = getAllProdukty()
        return when (val mode = determineSaveModeForProdukt(produkt, existingList)) {
            is SaveMode.Update -> {
                val updated = produkt.copy(id = mode.existingId)
                updateProdukt(updated)
                mode.existingId
            }
            SaveMode.Insert -> insertProdukt(produkt)
            SaveMode.Skip -> TODO()
        }
    }

    fun determineSaveModeForProdukt(
        produkt: Produkt,
        existingList: List<Produkt>
    ): SaveMode {
        val normalizedNewName = produkt.nazwaProduktu.trim().lowercase()

        for (existing in existingList) {
            val normalizedExistingName = existing.nazwaProduktu.trim().lowercase()
            if (normalizedNewName == normalizedExistingName) {
                return SaveMode.Skip
            }
        }

        return SaveMode.Insert
    }

    fun addProductToFaktura(fakturaId: String, produktFaktura: ProduktFaktura) {
        val faktura = getFakturaByID(fakturaId) ?: return
        insertProduktFaktura(produktFaktura.copy(fakturaId = faktura.id))
    }

    fun updateFaktura(faktura: Faktura) {
        runBlocking {
            withContext(Dispatchers.IO) {
                fakturaService.update(faktura)
            }
        }
    }

    fun updateProduktFaktura(produkt: ProduktFaktura) {
        runBlocking {
            withContext(Dispatchers.IO) {
                produktFakturaService.update(produkt)
            }
        }
    }

    fun updateProdukt(produkt: Produkt) {
        runBlocking {
            withContext(Dispatchers.IO) {
                produktService.update(produkt)
            }
        }
    }

    fun deleteFaktura(faktura: Faktura) {
        runBlocking {
            withContext(Dispatchers.IO) {
                fakturaService.delete(faktura)
            }
        }
    }

    fun deleteProduktFakturaFromFaktura(produktFaktura: ProduktFaktura) {
        runBlocking {
            withContext(Dispatchers.IO) {
                produktFakturaService.delete(produktFaktura)
            }
        }
    }

    suspend fun addFakturaFromJson(jsonString: String) {
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
            uzytkownikId = FirebaseAuth.getInstance().currentUser?.uid.isNullOrEmpty().toString(),
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

        val fakturaId = insertFaktura(faktura)

        fakturaDTO.produkty.forEach { dto ->
            val produkt = Produkt(
                nazwaProduktu = dto.nazwaProduktu,
                jednostkaMiary = dto.jednostkaMiary,
                cenaNetto = dto.cenaNetto,
                stawkaVat = dto.stawkaVat
            )

            val produktId = insertProdukt(produkt)

            val pozycja = ProduktFaktura(
                fakturaId = fakturaId,
                produktId = produktId,
                ilosc = dto.ilosc,
                rabat = dto.rabat,
                wartoscNetto = dto.wartoscNetto,
                wartoscBrutto = dto.wartoscBrutto
            )

            insertProduktFaktura(pozycja)
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
    ): List<Faktura> = runBlocking {
        withContext(Dispatchers.IO) {
            fakturaService.getFilteredFaktury(
                startDate,
                endDate,
                minPrice,
                maxPrice,
                filterDate,
                filterPrice
            )
        }
    }

    fun getAllProdukty(): List<Produkt> = runBlocking {
        withContext(Dispatchers.IO) {
            produktService.getAll()
        }
    }

    fun getProduktById(id: String): Produkt = runBlocking {
        withContext(Dispatchers.IO) {
            produktService.getOneProduktById(id)
        }
    }

    fun getListProduktyFakturaZProduktemForListFaktura(faktury: List<Faktura>): List<ProduktFakturaZProduktem> {
        val resultList = mutableListOf<ProduktFakturaZProduktem>()

        for (faktura in faktury) {
            val produktyFaktura = getProduktyFakturaForFaktura(faktura)
            for (produktFaktura in produktyFaktura) {
                val produkt: Produkt = getProduktForProduktFaktura(produktFaktura)
                val pfzp = ProduktFakturaZProduktem(
                    produktFaktura = produktFaktura,
                    produkt = produkt
                )
                resultList.add(pfzp)
            }
        }
        return resultList
    }
}