package com.example.photoapp.database.data
//
//import android.util.Log
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.viewModelScope
//import com.example.photoapp.database.data.dao.KategoriaDao
//import com.example.photoapp.database.data.dao.OdbiorcaDao
//import com.example.photoapp.database.data.dao.SprzedawcaDao
//import com.example.photoapp.database.data.dao.UzytkownikDao
//import com.example.photoapp.ui.faktura.data.Faktura
//import com.example.photoapp.ui.faktura.data.FakturaDao
//import com.example.photoapp.ui.faktura.data.ProduktFaktura
//import com.example.photoapp.ui.faktura.data.ProduktFakturaDao
//import com.example.photoapp.ui.paragon.data.Paragon
//import com.example.photoapp.ui.paragon.data.ParagonDao
//import com.example.photoapp.ui.paragon.data.ProduktParagon
//import com.example.photoapp.ui.paragon.data.ProduktParagonDao
//import com.example.photoapp.ui.raportFiskalny.data.ProduktRaportFiskalny
//import com.example.photoapp.ui.raportFiskalny.data.ProduktRaportFiskalnyDao
//import com.example.photoapp.ui.raportFiskalny.data.RaportFiskalny
//import com.example.photoapp.ui.raportFiskalny.data.RaportFiskalnyDao
//import com.example.photoapp.utils.jsonTransformer
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.serialization.*
//import kotlinx.serialization.json.*
//import java.text.SimpleDateFormat
//import java.util.Date
//import javax.inject.Inject
//
//@Serializable
//data class ProduktParagonDTO(
//    @SerialName("nazwaProduktu") val nazwaProduktu: String = "none",
//    @SerialName("cenaSuma") val cenaSuma: String = "999.9",
//    @SerialName("ilosc") val ilosc: String = "1"
//)
//
//@Serializable
//data class ParagonDTO(
//    @SerialName("dataZakupu") val dataZakupu: String = "1999-01-01",
//    @SerialName("nazwaSklepu") val nazwaSklepu: String = "none",
//    @SerialName("kwotaCalkowita") val kwotaCalkowita: String = "999.9",
//    val produkty: List<ProduktParagonDTO>
//)
//
//@Serializable
//data class ProduktFakturaDTO(
//    @SerialName("nazwaProduktu") val nazwaProduktu: String = "none",
//    @SerialName("jednostkaMiary") val jednostkaMiary: String = "none",
//    @SerialName("ilosc") val ilosc: String = "999",
//    @SerialName("wartoscNetto") val wartoscNetto: String = "999",
//    @SerialName("stawkaVat") val stawkaVat: String = "999",
//    @SerialName("podatekVat") val podatekVat: String = "999",
//    @SerialName("brutto") val brutto: String = "999",
//)
//
//@Serializable
//data class FakturaDTO(
//    @SerialName("numerFaktury") val numerFaktury: String = "none",
//    @SerialName("nrRachunkuBankowego") val nrRachunkuBankowego: String = "none",
//    @SerialName("dataWystawienia") val dataWystawienia: String = "1999-01-01",
//    @SerialName("dataSprzedazy") val dataSprzedazy: String = "1999-01-01",
//    @SerialName("razemNetto") val razemNetto: String = "999",
//    @SerialName("razemStawka") val razemStawka: String? = "999",
//    @SerialName("razemPodatek") val razemPodatek: String = "999",
//    @SerialName("razemBrutto") val razemBrutto: String = "999",
//    @SerialName("waluta") val waluta: String = "999",
//    @SerialName("formaPlatnosci") val formaPlatnosci: String = "none",
//    val odbiorca: OdbiorcaDTO,
//    val sprzedawca: SprzedawcaDTO,
//    val produkty: List<ProduktFakturaDTO>
//)
//
//@Serializable
//data class OdbiorcaDTO(
//    @SerialName("nazwa") val nazwa: String = "none",
//    @SerialName("nip") val nip: String = "none",
//    @SerialName("adres") val adres: String = "none",
//)
//
//@Serializable
//data class SprzedawcaDTO(
//    @SerialName("nazwa") val nazwa: String = "none",
//    @SerialName("nip") val nip: String = "none",
//    @SerialName("adres") val adres: String = "none",
//)
//
//@Serializable
//data class ProduktRaportFiskalnyDTO(
//    @SerialName("nrPLU") val nrPLU: String = "none",
//    @SerialName("ilosc") val ilosc: String = "none"
//)
//
//@Serializable
//data class RaportFiskalnyDTO(
//    @SerialName("dataDodania") val dataDodania: String = "1999-01-01",
//    val produkty: List<ProduktRaportFiskalnyDTO>
//)
//
//@Serializable
//data class OnlyProduktyRaportFiskalnyDTO(
//    val produkty: List<ProduktRaportFiskalnyDTO>
//)
//
//class DatabaseRepository @Inject constructor(
//    private val uzytkownikDao: UzytkownikDao,
//    private val odbiorcaDao: OdbiorcaDao,
//    private val sprzedawcaDao: SprzedawcaDao,
//    private val paragonDao: ParagonDao,
//    private val produktParagonDao: ProduktParagonDao,
//    private val fakturaDao: FakturaDao,
//    private val produktFakturaDao: ProduktFakturaDao,
//    private val kategoriaDao: KategoriaDao,
//    private val raportFiskalnyDao: RaportFiskalnyDao,
//    private val produktRaportFiskalnyDao: ProduktRaportFiskalnyDao,
//){
//    val allLiveParagony: LiveData<List<Paragon>> = paragonDao.getAllLiveParagony()
//
//    val allLiveFaktury: LiveData<List<Faktura>> = fakturaDao.getAllLiveFaktury()
//
//    val allLiveRaportFiskalny: LiveData<List<RaportFiskalny>> = raportFiskalnyDao.getAllLiveRaportFiskalny()
//
////    val allLiveProduktRaportFiskalny: LiveData<List<ProduktRaportFiskalny>> = produktRaportFiskalnyDao.getAllLiveProduktForRaportFiskalny(raportFiskalnyId = raportFiskalnyId)
//
//    fun addUser(login: String, password: String, email: String) {
//        uzytkownikDao.addUser(login, password, email)
//    }
//
//    fun addTestRecipe() {
//        paragonDao.addTestRecipe()
//    }
//
//    fun addTestRecipeProducts() {
//        produktParagonDao.addTestRecipeProducts(paragonDao)
//    }
//
//    // [START] PARAGON
//
//    fun getAllParagony(callback: (List<Paragon>) -> Unit) {
//        val result: List<Paragon> =  paragonDao.getAll()
//        callback(result)
//    }
//
//    fun addRecipe(jsonString: String) {
//        // Deserializacja JSON
//        val coercingJson = Json { coerceInputValues = true }
//        val transformedJson = jsonTransformer(jsonString)
//        val paragonDTO = coercingJson.decodeFromString<ParagonDTO>(transformedJson)
//        Log.i("Dolan", paragonDTO.dataZakupu)
//
//        // Konwersja na obiekty Room
//        val paragon = Paragon(
//            uzytkownikId = 1,
//            dataZakupu = SimpleDateFormat("yyyy-MM-dd").parse(paragonDTO.dataZakupu),
//            nazwaSklepu = paragonDTO.nazwaSklepu,
//            kwotaCalkowita = paragonDTO.kwotaCalkowita.replace(",", ".").toDouble()
//        )
//
//        // Zapis do bazy danych
//        val paragonId = paragonDao.insertParagon(paragon)
//        Log.i("Dolan", "Paragon inserted $paragonId")
//        // Zapis produktów związanych z paragonem
//        paragonDTO.produkty.forEach { produktDTO ->
//            val produktParagon = ProduktParagon(
//                paragonId = paragonId.toInt(),
//                kategoriaId = null, // Możesz przypisać kategorię, jeśli dostępna
//                nazwaProduktu = produktDTO.nazwaProduktu,
//                ilosc = produktDTO.ilosc.toDouble().toInt(),
//                cenaSuma = produktDTO.cenaSuma.replace(",", ".").toDouble()
//            )
//            produktParagonDao.insert(produktParagon)
//        }
//    }
//
//    fun getProductForParagon(paragonID: Int): List<ProduktParagon> {
//        return produktParagonDao.getProductByParagonId(paragonID)
//    }
//
//    fun fetchFilteredParagony(
//        startDate: Date?,
//        endDate: Date?,
//        minPrice: Double?,
//        maxPrice: Double?,
//    ): List<Paragon> {
//        return paragonDao.getFilteredParagony(startDate, endDate, minPrice, maxPrice)
//    }
//
//    // [END] PARAGON
//
//    // [START] FAKTURA
//
//    fun addFaktura(jsonString: String) {
//        val coercingJson = Json { coerceInputValues = true }
//        val transformedJson = jsonTransformer(jsonString)
//        val fakturaDTO = coercingJson.decodeFromString<FakturaDTO>(transformedJson)
//        Log.i("Dolan", fakturaDTO.dataSprzedazy)
//
//        val odbiorca = odbiorcaDao.addOrGetOdbiorca(
//            fakturaDTO.odbiorca.nazwa,
//            fakturaDTO.odbiorca.nip,
//            fakturaDTO.odbiorca.adres
//        )
//        val sprzedawca = sprzedawcaDao.addOrGetSprzedawca(
//            fakturaDTO.sprzedawca.nazwa,
//            fakturaDTO.sprzedawca.nip,
//            fakturaDTO.sprzedawca.adres
//        )
//
//        val newStawka = fakturaDTO.razemStawka ?: "null"
//
//        val faktura = Faktura(
//            uzytkownikId = 1,
//            odbiorcaId = odbiorca.id,
//            sprzedawcaId = sprzedawca.id,
//            numerFaktury = fakturaDTO.numerFaktury,
//            nrRachunkuBankowego = fakturaDTO.nrRachunkuBankowego,
//            dataWystawienia = SimpleDateFormat("yyyy-MM-dd").parse(fakturaDTO.dataWystawienia),
//            dataSprzedazy = SimpleDateFormat("yyyy-MM-dd").parse(fakturaDTO.dataSprzedazy),
//            razemNetto = fakturaDTO.razemNetto,
//            razemStawka = newStawka,
//            razemPodatek = fakturaDTO.razemPodatek,
//            razemBrutto = fakturaDTO.razemBrutto,
//            waluta = fakturaDTO.waluta,
//            formaPlatnosci = fakturaDTO.formaPlatnosci
//        )
//
//        val fakturaID = fakturaDao.insertFaktura(faktura)
//        Log.i("Dolan", "Faktura inserted $fakturaID")
//        fakturaDTO.produkty.forEach { produktFakturaDTO ->
//            val produktFaktura = ProduktFaktura(
//                fakturaId = fakturaID.toInt(),
//                nazwaProduktu = produktFakturaDTO.nazwaProduktu,
//                jednostkaMiary = produktFakturaDTO.jednostkaMiary,
//                ilosc = produktFakturaDTO.ilosc,
//                wartoscNetto = produktFakturaDTO.wartoscNetto,
//                stawkaVat = produktFakturaDTO.stawkaVat,
//                podatekVat = produktFakturaDTO.podatekVat,
//                brutto = produktFakturaDTO.brutto
//            )
//            produktFakturaDao.insert(produktFaktura)
//        }
//    }
//
//    fun getProductForFaktura(fakturaId: Int): List<ProduktFaktura> {
//        return produktFakturaDao.getProductByFakturaId(fakturaId)
//    }
//
//    fun fetchFilteredFaktury(
//        startDate: Date?,
//        endDate: Date?,
//        minPrice: Double?,
//        maxPrice: Double?,
//        filterDate: String,
//        filterPrice: String,
//    ): List<Faktura> {
//        return fakturaDao.getFilteredFaktury(startDate, endDate, minPrice, maxPrice, filterDate, filterPrice)
//    }
//
//    // [END] FAKTURA
//
//    // [START] RAPORT FISKALNY
//
//    fun getAllRaportFiskalny(): List<RaportFiskalny> {
//        return raportFiskalnyDao.getAll()
//    }
//
//    fun getRaportFiskalnyByID(id: Int): RaportFiskalny {
//        return raportFiskalnyDao.getRaportByID(id)
//    }
//
//    fun addRaportFiskalny(jsonString: String): Long {
//        // Deserializacja JSON
//        val coercingJson = Json { coerceInputValues = true }
//        val transformedJson = jsonTransformer(jsonString)
//        val rfDTO = coercingJson.decodeFromString<RaportFiskalnyDTO>(transformedJson)
//        Log.i("Dolan", rfDTO.dataDodania)
//
//        // Konwersja na obiekty Room
//        val raport = RaportFiskalny(
//            dataDodania = SimpleDateFormat("yyyy-MM-dd").parse(rfDTO.dataDodania)
//        )
//
//        // Zapis do bazy danych
//        val raportId = raportFiskalnyDao.insert(raport)
//        Log.i("Dolan", "Raport inserted $raportId")
//        rfDTO.produkty.forEach { produktDTO ->
//            val produktRF = ProduktRaportFiskalny(
//                raportFiskalnyId = raportId.toInt(),
//                nrPLU = produktDTO.nrPLU,
//                ilosc = produktDTO.ilosc
//            )
//            produktRaportFiskalnyDao.insert(produktRF)
//        }
//        return raportId
//    }
//
//    fun insertRaportFiskalny(raportFiskalny: RaportFiskalny): Long {
//        return raportFiskalnyDao.insert(raportFiskalny)
//    }
//
//    fun insertProduktRaportFiskalny(produktRaportFiskalny: ProduktRaportFiskalny) {
//        produktRaportFiskalnyDao.insert(produktRaportFiskalny)
//    }
//
//    fun getProductForRaportFiskalny(raportFiskalnyId: Int): List<ProduktRaportFiskalny> {
//        return produktRaportFiskalnyDao.getProductForRaportFiskalny(raportFiskalnyId)
//    }
//
//    fun updateRaportFiskalny(raportFiskalny: RaportFiskalny) {
//        raportFiskalnyDao.update(raportFiskalny)
//    }
//
//    fun updateProduktRaportFiskalny(produktRaportFiskalny: ProduktRaportFiskalny) {
//        produktRaportFiskalnyDao.update(produktRaportFiskalny)
//    }
//
//    fun deleteRaportFiskalny(raportFiskalny: RaportFiskalny) {
//        Log.i("Dolan", "IM IN REPO NOW, DELETING OBJECT")
//        val productsForRaport = getProductForRaportFiskalny(raportFiskalny.id)
//        productsForRaport.forEach { produkt ->
//            produktRaportFiskalnyDao.delete(produkt)
//        }
//        raportFiskalnyDao.delete(raportFiskalny)
//    }
//
//    fun deleteProduktRaportFiskalny(produktRaportFiskalny: ProduktRaportFiskalny) {
//        produktRaportFiskalnyDao.delete(produktRaportFiskalny)
//    }
//
//    fun addProduktyRaportFiskalny(jsonInput: String, raport: RaportFiskalny){
//        // Deserializacja JSON
//        val coercingJson = Json { coerceInputValues = true }
//        val transformedJson = jsonTransformer(jsonInput)
//        // Dekodowanie listy produktów z JSON
//        val produktyList = coercingJson.decodeFromString<OnlyProduktyRaportFiskalnyDTO>(transformedJson)
//
//
//        // Zapis do bazy danych
//        val raportId = raport.id
//        produktyList.produkty.forEach { produktDTO ->
//            val produktRF = ProduktRaportFiskalny(
//                raportFiskalnyId = raportId,
//                nrPLU = produktDTO.nrPLU,
//                ilosc = produktDTO.ilosc
//            )
//            produktRaportFiskalnyDao.insert(produktRF)
//        }
//
//        checkForProductsRFDuplicates(raport)
//    }
//
//    fun checkForProductsRFDuplicates(raportFiskalny: RaportFiskalny) {
//        val allProductsForRF: List<ProduktRaportFiskalny> = getProductForRaportFiskalny(raportFiskalny.id)
//
//        val uniquePLU = mutableListOf<Int>()
//
//        for (product in allProductsForRF) {
//            val n = product.nrPLU.toInt()
//            if (n !in uniquePLU) {
//                uniquePLU.add(n)
//                Log.i("Dolan" , "SAVED PLU NUMBER: $n")
//            } else {
//                deleteProduktRaportFiskalny(product)
//                Log.i("Dolan" , "DELETED PLU NUMBER: $n")
//            }
//        }
//    }
//    // [END] REPORT
//
//}