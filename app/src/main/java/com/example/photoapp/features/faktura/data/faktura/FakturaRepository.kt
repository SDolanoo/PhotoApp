package com.example.photoapp.features.faktura.data.faktura

import com.example.photoapp.core.utils.calculateGrossValue
import com.example.photoapp.core.utils.calculateNetValueQuantity
import com.example.photoapp.core.utils.calculateSubstraction
import com.example.photoapp.core.utils.calculateSum
import com.example.photoapp.features.faktura.presentation.details.ProduktFakturaZProduktem
import com.example.photoapp.features.produkt.data.Produkt
import com.example.photoapp.features.produkt.data.ProduktFaktura
import com.example.photoapp.features.produkt.data.ProduktFakturaService
import com.example.photoapp.features.produkt.data.ProduktService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject

class FakturaRepository @Inject constructor(
    private val fakturaService: FakturaService,
    private val produktFakturaService: ProduktFakturaService,
    private val produktService: ProduktService
) {
    fun getAllLiveFaktury(): Flow<List<Faktura>> = fakturaService.getAllLive()
    fun getAllLiveProduktFaktura(): Flow<List<ProduktFaktura>> = produktFakturaService.getAllLive()
    fun getAllLiveProdukt(): Flow<List<Produkt>> = produktService.getAllLive()

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

    suspend fun updateProduktAndRelativeData(produkt: Produkt) {
        updateProdukt(produkt)

        val produktyFaktura = produktFakturaService.getAllProduktFakturaForProduktId(produkt.id)
        produktyFaktura.forEach { pf ->
            val faktura = fakturaService.getById(pf.id)!!


            val wartoscNetto = calculateNetValueQuantity(pf.ilosc, produkt.cenaNetto)
            val wartoscBrutto = calculateGrossValue(wartoscNetto, produkt.stawkaVat) ?: wartoscNetto

            val newRazemNetto = calculateSum(calculateSubstraction(faktura.razemNetto, pf.wartoscNetto), wartoscNetto)
            val newRazemBrutto = calculateSum(calculateSubstraction(faktura.razemBrutto, pf.wartoscBrutto), wartoscBrutto)
            // WIEM TO JEST OKROPNE, ALE MUSI WYSTARCZYĆ
            // obliczamy tutaj nowe wartości dla faktury i produktFaktury po zmianie Produktu

            produktFakturaService.update(pf.copy(wartoscNetto = wartoscNetto, wartoscBrutto = wartoscBrutto))
            fakturaService.update(faktura.copy(
                razemNetto = newRazemNetto,
                razemVAT = calculateSubstraction(newRazemBrutto, newRazemNetto),
                razemBrutto = newRazemBrutto,
                doZaplaty = newRazemBrutto))
        }
    }
}