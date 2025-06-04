package com.example.photoapp.ui.acceptPhoto

import android.graphics.Bitmap
import com.example.photoapp.core.AI.chatWithGemini
import kotlinx.serialization.json.Json
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.photoapp.core.AI.DocumentType
import com.example.photoapp.core.database.data.FakturaDTO
import com.example.photoapp.core.database.data.OnlyProduktyRaportFiskalnyDTO
import com.example.photoapp.core.database.data.ParagonDTO
import com.example.photoapp.core.database.data.ProduktFakturaDTO
import com.example.photoapp.core.database.data.ProduktParagonDTO
import com.example.photoapp.core.database.data.ProduktRaportFiskalnyDTO
import com.example.photoapp.core.database.data.RaportFiskalnyDTO
import com.example.photoapp.features.faktura.data.FakturaRepository
import com.example.photoapp.features.paragon.data.ParagonRepository
import com.example.photoapp.features.raportFiskalny.data.RaportFiskalny
import com.example.photoapp.features.raportFiskalny.data.RaportFiskalnyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.collections.joinToString

@HiltViewModel
class AcceptanceController @Inject constructor(
    private val paragonRepository: ParagonRepository,
    private val fakturaRepository: FakturaRepository,
    private val raportFiskalnyRepository: RaportFiskalnyRepository
) : ViewModel() {
    private var geminiPromptResult: String = ""

    fun retry(imagePath: String) {
        // Handle retry logic here
    }

    fun processPhoto(
        addingPhotoFor: String?,
        geminiKey: String,
        bitmapPhoto: Bitmap?,
        onResult: (Boolean, String) -> Unit
    ) {
        getPrompt(addingPhotoFor, geminiKey, bitmapPhoto) { response, result ->
            if (response == 1) {
                val formattedText = when (addingPhotoFor) {
                    "paragon" -> formatPromptForParagon()
                    "faktura" -> formatPromptForFaktura()
                    "raportFiskalny" -> formatPromptForRaportFiskalny()
                    else -> formatPromptForProductRaportFiskalny()
                }
                onResult(true, formattedText)
            } else {
                onResult(false, result)
            }
        }
    }

    fun getPrompt(addingPhotoFor: String?, geminiKey: String, bitmapPhoto: Bitmap?, callback: (Int, String) -> Unit) {
        // Symulacja asynchronicznego pobierania danych
        bitmapPhoto?.let {
            if (addingPhotoFor == "paragon") {
                Log.i("Dolan", "getting Prompt for paragon")
                // Przykład wywołania asynchronicznej operacji, np. API call do chatWithGemini
                chatWithGemini(geminiKey, bitmapPhoto, DocumentType.PARAGON) { response, result ->
                    geminiPromptResult = result
                    callback(response, result)
                }
            } else if (addingPhotoFor == "faktura") {
                Log.i("Dolan", "getting Prompt for faktura")
                // Przykład wywołania asynchronicznej operacji, np. API call do chatWithGemini
                chatWithGemini(geminiKey, bitmapPhoto, DocumentType.FAKTURA) { response, result ->
                    geminiPromptResult = result
                    callback(response, result)
                }
            } else if (addingPhotoFor == "raportFiskalny") {
                Log.i("Dolan", "getting Prompt for raportFiskalny")
                // Przykład wywołania asynchronicznej operacji, np. API call do chatWithGemini
                chatWithGemini(geminiKey, bitmapPhoto, DocumentType.RAPORT_FISKALNY) { response, result ->
                    geminiPromptResult = result
                    callback(response, result)
                }
            } else if (addingPhotoFor == "produktRaportFiskalny") {
                Log.i("Dolan", "getting Prompt for produktRaportFiskalny")
                // Przykład wywołania asynchronicznej operacji, np. API call do chatWithGemini
                chatWithGemini(geminiKey, bitmapPhoto, DocumentType.PRODUCTS_RAPORT_FISKALNY) { response, result ->
                    geminiPromptResult = result
                    callback(response, result)
                }
            }
        } ?: run {
            callback(2, "No valid image provided")
        }
    }

    fun addRecipe() {
        if (geminiPromptResult != "") {
            Log.i("Dolan", "Adding Recipe")
            paragonRepository.addRecipeFromJson(jsonString = geminiPromptResult)
            Log.i("Dolan", "added Recipe")
        }
    }

    fun formatEachProduktParagon(produkty: List<ProduktParagonDTO>): String{
        return produkty.joinToString(separator = "\n"){ produkt ->
            """
                nazwaProduktu: ${produkt.nazwaProduktu}
                    cenaSuma: ${produkt.cenaSuma}
                    ilosc: ${produkt.ilosc}
            """
        }
    }

    fun formatPromptForParagon(): String {
        val coercingJson = Json { coerceInputValues = true }
        val p = coercingJson.decodeFromString<ParagonDTO>(geminiPromptResult)
        val resultString = """
            Dane paragon:
                nazwaSklepu: ${p.nazwaSklepu}
                dataZakupu: ${p.dataZakupu}
                kwotaCalkowita: ${p.kwotaCalkowita}
            Produkty:
                |${formatEachProduktParagon(p.produkty)}          
        """.trimIndent().trimMargin("|")
        return resultString
    }

    fun addInvoice() {
        if (geminiPromptResult != "") {
            Log.i("Dolan", "Adding Invoice")
            fakturaRepository.addFakturaFromJson(jsonString = geminiPromptResult)
            Log.i("Dolan", "added Invoice")
        }
    }

    fun formatEachProduktFaktura(produkty: List<ProduktFakturaDTO>): String{
        return produkty.joinToString(separator = "\n"){ produkt ->
            """
                nazwaProduktu: ${produkt.nazwaProduktu}
                    jednostkaMiary: ${produkt.jednostkaMiary}
                    ilosc: ${produkt.ilosc}
                    cenaNetto: ${produkt.cenaNetto}
                    wartoscNetto: ${produkt.wartoscNetto}
                    wartoscBrutto: ${produkt.wartoscBrutto}
                    stawkaVat: ${produkt.stawkaVat}
            """
        }
    }

    fun formatPromptForFaktura(): String {
        val coercingJson = Json { coerceInputValues = true }
        val f = coercingJson.decodeFromString<FakturaDTO>(geminiPromptResult)
        val resultString = """
            Sprzedawca: 
                nazwa: ${f.sprzedawca.nazwa}
                nip: ${f.sprzedawca.nip}
                adres: ${f.sprzedawca.adres}
            Odbiorca:
                nazwa: ${f.odbiorca.nazwa}
                nip: ${f.odbiorca.nip}
                adres: ${f.odbiorca.adres}
            Dane faktura:
                numerFaktury: ${f.numerFaktury}
                status: ${f.status}
                dataWystawienia: ${f.dataWystawienia}
                dataSprzedazy: ${f.dataSprzedazy}
                terminPlatnosci: ${f.terminPlatnosci}
                razemNetto: ${f.razemNetto}
                razemVAT: ${f.razemVAT}
                razemBrutto: ${f.razemBrutto}
                doZaplaty: ${f.doZaplaty}
                waluta: ${f.waluta}
                formaPlatnosci: ${f.formaPlatnosci}
            Produkty:
                |${formatEachProduktFaktura(f.produkty)}          
        """.trimIndent().trimMargin("|")
        return resultString
    }

    fun addRaportFiskalny(): Long {
        if (geminiPromptResult != "") {
            val coercingJson = Json { coerceInputValues = true }
            val f = coercingJson.decodeFromString<RaportFiskalnyDTO>(geminiPromptResult)
            return raportFiskalnyRepository.addRaportFromJson(jsonString = geminiPromptResult)
        }
        return 0
    }

    fun getRaportByID(id: Long): RaportFiskalny {
        return raportFiskalnyRepository.getRaportById(id)
    }

    fun formatEachProduktRaportFiskalny(produkty: List<ProduktRaportFiskalnyDTO>): String {
        return produkty.joinToString(separator = "\n") { produkt ->
            """
                nrPLU: ${produkt.nrPLU}
                    ilosc: ${produkt.ilosc}
            """
        }
    }

    fun formatPromptForRaportFiskalny(): String {
        val coercingJson = Json { coerceInputValues = true }
        val rf = coercingJson.decodeFromString<RaportFiskalnyDTO>(geminiPromptResult)
        val resultString = """
            Data dodania: 
                ${rf.dataDodania}
            Produkty:
                |${formatEachProduktRaportFiskalny(rf.produkty)}
        """.trimIndent().trimMargin("|")
        return resultString
    }

    fun addProduktRaportFiskalny(raport: RaportFiskalny) {
        if (geminiPromptResult != "") {
            raportFiskalnyRepository.addProduktyFromJson(jsonInput = geminiPromptResult, raport = raport)
        }
    }

    fun formatPromptForProductRaportFiskalny(): String {
        val coercingJson = Json { coerceInputValues = true }
        val p = coercingJson.decodeFromString<OnlyProduktyRaportFiskalnyDTO>(geminiPromptResult)
        val resultString = """
            Produkty:
                |${formatEachProduktRaportFiskalny(p.produkty)}
        """.trimIndent().trimMargin("|")
        return resultString
    }

}


