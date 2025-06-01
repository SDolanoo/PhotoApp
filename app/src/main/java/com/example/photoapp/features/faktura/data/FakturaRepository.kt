package com.example.photoapp.features.faktura.data

import android.util.Log
import com.example.photoapp.core.database.data.FakturaDTO
import com.example.photoapp.core.database.data.dao.OdbiorcaDao
import com.example.photoapp.core.database.data.dao.SprzedawcaDao
import com.example.photoapp.core.database.data.entities.Odbiorca
import com.example.photoapp.core.database.data.entities.Sprzedawca
import com.example.photoapp.core.database.data.repos.OdbiorcaRepository
import com.example.photoapp.core.database.data.repos.SprzedawcaRepository
import com.example.photoapp.core.utils.jsonTransformer
import com.example.photoapp.features.raportFiskalny.data.ProduktRaportFiskalny
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class FakturaRepository @Inject constructor(
    private val fakturaDao: FakturaDao,
    private val produktFakturaDao: ProduktFakturaDao,
    private val odbiorcaRepository: OdbiorcaRepository,
    private val sprzedawcaRepository: SprzedawcaRepository
) {

    fun getAllLiveFaktury() = fakturaDao.getAllLive()

    fun getFakturaByID(id: Int): Faktura? = fakturaDao.getById(id)

    fun getProduktyForFaktura(fakturaId: Int): List<ProduktFaktura> {
        return produktFakturaDao.getProductsByFakturaId(fakturaId)
    }

    fun insertFaktura(faktura: Faktura) {
        fakturaDao.insert(faktura)
    }

    fun insertProdukt(produkt: ProduktFaktura) {
        produktFakturaDao.insert(produkt)
    }

    fun updateFaktura(faktura: Faktura) {
        fakturaDao.delete(faktura)
    }

    fun updateProdukt(produkt: ProduktFaktura) {
        produktFakturaDao.delete(produkt)
    }

    fun deleteFaktura(faktura: Faktura) {
        Log.i("RaportRepo", "Deleting Raport: ${faktura.id}")
        getProduktyForFaktura(faktura.id).forEach {
            produktFakturaDao.delete(it)
        }
        fakturaDao.delete(faktura)
    }

    fun deleteProdukt(produkt: ProduktFaktura) {
        produktFakturaDao.delete(produkt)
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
            numerFaktury = fakturaDTO.numerFaktury,
            nrRachunkuBankowego = fakturaDTO.nrRachunkuBankowego,
            dataWystawienia = SimpleDateFormat("yyyy-MM-dd").parse(fakturaDTO.dataWystawienia),
            dataSprzedazy = SimpleDateFormat("yyyy-MM-dd").parse(fakturaDTO.dataSprzedazy),
            razemNetto = fakturaDTO.razemNetto,
            razemStawka = fakturaDTO.razemStawka ?: "null",
            razemPodatek = fakturaDTO.razemPodatek,
            razemBrutto = fakturaDTO.razemBrutto,
            waluta = fakturaDTO.waluta,
            formaPlatnosci = fakturaDTO.formaPlatnosci
        )

        val fakturaId = fakturaDao.insert(faktura).toInt()

        fakturaDTO.produkty.forEach { produktDTO ->
            val produkt = ProduktFaktura(
                fakturaId = fakturaId,
                nazwaProduktu = produktDTO.nazwaProduktu,
                jednostkaMiary = produktDTO.jednostkaMiary,
                ilosc = produktDTO.ilosc,
                wartoscNetto = produktDTO.wartoscNetto,
                stawkaVat = produktDTO.stawkaVat,
                podatekVat = produktDTO.podatekVat,
                brutto = produktDTO.brutto
            )
            insertProdukt(produkt)
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
}
