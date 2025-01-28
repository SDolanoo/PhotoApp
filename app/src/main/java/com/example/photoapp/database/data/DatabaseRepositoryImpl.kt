package com.example.photoapp.database.data

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.photoapp.utils.jsonTransformer
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

@Serializable
data class ProduktParagonDTO(
    @SerialName("nazwaProduktu") val nazwaProduktu: String = "none",
    @SerialName("cenaSuma") val cenaSuma: String = "999.9",
    @SerialName("ilosc") val ilosc: String = "1"
)

@Serializable
data class ParagonDTO(
    @SerialName("dataZakupu") val dataZakupu: String = "1999-01-01",
    @SerialName("nazwaSklepu") val nazwaSklepu: String = "none",
    @SerialName("kwotaCalkowita") val kwotaCalkowita: String = "999.9",
    val produkty: List<ProduktParagonDTO>
)

@Serializable
data class ProduktFakturaDTO(
    @SerialName("nazwaProduktu") val nazwaProduktu: String = "none",
    @SerialName("jednostkaMiary") val jednostkaMiary: String = "none",
    @SerialName("ilosc") val ilosc: String = "999",
    @SerialName("wartoscNetto") val wartoscNetto: String = "999",
    @SerialName("stawkaVat") val stawkaVat: String = "999",
    @SerialName("podatekVat") val podatekVat: String = "999",
    @SerialName("brutto") val brutto: String = "999",
)

@Serializable
data class FakturaDTO(
    @SerialName("numerFaktury") val numerFaktury: String = "none",
    @SerialName("nrRachunkuBankowego") val nrRachunkuBankowego: String = "none",
    @SerialName("dataWystawienia") val dataWystawienia: String = "1999-01-01",
    @SerialName("dataSprzedazy") val dataSprzedazy: String = "1999-01-01",
    @SerialName("razemNetto") val razemNetto: String = "999",
    @SerialName("razemStawka") val razemStawka: String? = "999",
    @SerialName("razemPodatek") val razemPodatek: String = "999",
    @SerialName("razemBrutto") val razemBrutto: String = "999",
    @SerialName("waluta") val waluta: String = "999",
    @SerialName("formaPlatnosci") val formaPlatnosci: String = "none",
    val odbiorca: OdbiorcaDTO,
    val sprzedawca: SprzedawcaDTO,
    val produkty: List<ProduktFakturaDTO>
)

@Serializable
data class OdbiorcaDTO(
    @SerialName("nazwa") val nazwa: String = "none",
    @SerialName("nip") val nip: String = "none",
    @SerialName("adres") val adres: String = "none",
)

@Serializable
data class SprzedawcaDTO(
    @SerialName("nazwa") val nazwa: String = "none",
    @SerialName("nip") val nip: String = "none",
    @SerialName("adres") val adres: String = "none",
)

class DatabaseRepository @Inject constructor(
    private val uzytkownikDao: UzytkownikDao,
    private val odbiorcaDao: OdbiorcaDao,
    private val sprzedawcaDao: SprzedawcaDao,
    private val paragonDao: ParagonDao,
    private val produktParagonDao: ProduktParagonDao,
    private val fakturaDao: FakturaDao,
    private val produktFakturaDao: ProduktFakturaDao,
    private val kategoriaDao: KategoriaDao
){
    val allLiveParagony: LiveData<List<Paragon>> = paragonDao.getAllLiveParagony()

    val allLiveFaktury: LiveData<List<Faktura>> = fakturaDao.getAllLiveFaktury()

    fun addUser(login: String, password: String, email: String) {
        uzytkownikDao.addUser(login, password, email)
    }

    fun addTestRecipe() {
        paragonDao.addTestRecipe()
    }

    fun addTestRecipeProducts() {
        produktParagonDao.addTestRecipeProducts(paragonDao)
    }

    fun getAllParagony(callback: (List<Paragon>) -> Unit) {
        val result: List<Paragon> =  paragonDao.getAll()
        callback(result)
    }

    fun addRecipe(jsonString: String) {
        saveParagonToDatabase(jsonInput = jsonString, uzytkownikId = 1)
    }

    fun addFaktura(jsonString: String) {
        saveFakturaToDatabase(jsonInput = jsonString, uzytkownikId = 1)
    }

    fun getProductForParagon(paragonID: Int): List<ProduktParagon> {
        return produktParagonDao.getProductByParagonId(paragonID)
    }

    fun getProductForFaktura(fakturaId: Int): List<ProduktFaktura> {
        return produktFakturaDao.getProductByFakturaId(fakturaId)
    }

    fun fetchFilteredParagony(
        startDate: Date?,
        endDate: Date?,
        minPrice: Double?,
        maxPrice: Double?,
    ): List<Paragon> {
        return paragonDao.getFilteredParagony(startDate, endDate, minPrice, maxPrice)
    }

    fun fetchFilteredFaktury(
        startDate: Date?,
        endDate: Date?,
        minPrice: Double?,
        maxPrice: Double?,
        filterDate: String,
        filterPrice: String,
    ): List<Faktura> {
        return fakturaDao.getFilteredFaktury(startDate, endDate, minPrice, maxPrice, filterDate, filterPrice)
    }

    fun saveParagonToDatabase(
        jsonInput: String,
        uzytkownikId: Int
    ) {
        // Deserializacja JSON
        val coercingJson = Json { coerceInputValues = true }
        val transformedJson = jsonTransformer(jsonInput)
        val paragonDTO = coercingJson.decodeFromString<ParagonDTO>(transformedJson)
        Log.i("Dolan", paragonDTO.dataZakupu)

        // Konwersja na obiekty Room
        val paragon = Paragon(
            uzytkownikId = uzytkownikId,
            dataZakupu = SimpleDateFormat("yyyy-MM-dd").parse(paragonDTO.dataZakupu),
            nazwaSklepu = paragonDTO.nazwaSklepu,
            kwotaCalkowita = paragonDTO.kwotaCalkowita.replace(",", ".").toDouble()
        )

        // Zapis do bazy danych
        val paragonId = paragonDao.insertParagon(paragon)
        Log.i("Dolan", "Paragon inserted $paragonId")
        // Zapis produktów związanych z paragonem
        paragonDTO.produkty.forEach { produktDTO ->
            val produktParagon = ProduktParagon(
                paragonId = paragonId.toInt(),
                kategoriaId = null, // Możesz przypisać kategorię, jeśli dostępna
                nazwaProduktu = produktDTO.nazwaProduktu,
                ilosc = produktDTO.ilosc.toDouble().toInt(),
                cenaSuma = produktDTO.cenaSuma.replace(",", ".").toDouble()
            )
            produktParagonDao.insert(produktParagon)
        }
    }

    fun saveFakturaToDatabase(jsonInput: String, uzytkownikId: Int) {
        val coercingJson = Json { coerceInputValues = true }
        val transformedJson = jsonTransformer(jsonInput)
        val fakturaDTO = coercingJson.decodeFromString<FakturaDTO>(transformedJson)
        Log.i("Dolan", fakturaDTO.dataSprzedazy)

        val odbiorca = odbiorcaDao.addOrGetOdbiorca(
            fakturaDTO.odbiorca.nazwa,
            fakturaDTO.odbiorca.nip,
            fakturaDTO.odbiorca.adres
        )
        val sprzedawca = sprzedawcaDao.addOrGetSprzedawca(
            fakturaDTO.sprzedawca.nazwa,
            fakturaDTO.sprzedawca.nip,
            fakturaDTO.sprzedawca.adres
        )

        val newStawka = fakturaDTO.razemStawka ?: "null"

        val faktura = Faktura(
            uzytkownikId = uzytkownikId,
            odbiorcaId = odbiorca.id,
            sprzedawcaId = sprzedawca.id,
            numerFaktury = fakturaDTO.numerFaktury,
            nrRachunkuBankowego = fakturaDTO.nrRachunkuBankowego,
            dataWystawienia = SimpleDateFormat("yyyy-MM-dd").parse(fakturaDTO.dataWystawienia),
            dataSprzedazy = SimpleDateFormat("yyyy-MM-dd").parse(fakturaDTO.dataSprzedazy),
            razemNetto = fakturaDTO.razemNetto,
            razemStawka = newStawka,
            razemPodatek = fakturaDTO.razemPodatek,
            razemBrutto = fakturaDTO.razemBrutto,
            waluta = fakturaDTO.waluta,
            formaPlatnosci = fakturaDTO.formaPlatnosci
        )

        val fakturaID = fakturaDao.insertFaktura(faktura)
        Log.i("Dolan", "Faktura inserted $fakturaID")
        fakturaDTO.produkty.forEach { produktFakturaDTO ->
            val produktFaktura = ProduktFaktura(
                fakturaId = fakturaID.toInt(),
                nazwaProduktu = produktFakturaDTO.nazwaProduktu,
                jednostkaMiary = produktFakturaDTO.jednostkaMiary,
                ilosc = produktFakturaDTO.ilosc,
                wartoscNetto = produktFakturaDTO.wartoscNetto,
                stawkaVat = produktFakturaDTO.stawkaVat,
                podatekVat = produktFakturaDTO.podatekVat,
                brutto = produktFakturaDTO.brutto
            )
            produktFakturaDao.insert(produktFaktura)
        }
    }

}