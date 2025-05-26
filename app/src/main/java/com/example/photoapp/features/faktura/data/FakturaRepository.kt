package com.example.photoapp.features.faktura.data

import com.example.photoapp.database.data.FakturaDTO
import android.util.Log
import com.example.photoapp.database.data.dao.OdbiorcaDao
import com.example.photoapp.database.data.dao.SprzedawcaDao
import com.example.photoapp.database.data.entities.Odbiorca
import com.example.photoapp.database.data.entities.Sprzedawca
import com.example.photoapp.utils.jsonTransformer
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class FakturaRepository @Inject constructor(
    private val fakturaDao: FakturaDao,
    private val produktFakturaDao: ProduktFakturaDao,
    private val odbiorcaDao: OdbiorcaDao,
    private val sprzedawcaDao: SprzedawcaDao
) {

    fun getAllLiveFaktury() = fakturaDao.getAllLive()

    fun getProductForFaktura(fakturaId: Int): List<ProduktFaktura> {
        return produktFakturaDao.getByFakturaId(fakturaId)
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

    fun addFakturaFromJson(jsonString: String) {
        val coercingJson = Json { coerceInputValues = true }
        val transformedJson = jsonTransformer(jsonString)
        val fakturaDTO = coercingJson.decodeFromString<FakturaDTO>(transformedJson)

        val odbiorca: Odbiorca = odbiorcaDao.addOrGetOdbiorca(
            fakturaDTO.odbiorca.nazwa,
            fakturaDTO.odbiorca.nip,
            fakturaDTO.odbiorca.adres
        )
        val sprzedawca: Sprzedawca = sprzedawcaDao.addOrGetSprzedawca(
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
            produktFakturaDao.insert(produkt)
        }

        Log.i("FakturaRepository", "Inserted Faktura and ${fakturaDTO.produkty.size} products")
    }
}
