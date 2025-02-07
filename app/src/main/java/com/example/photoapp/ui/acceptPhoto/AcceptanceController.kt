package com.example.photoapp.ui.acceptPhoto

import android.graphics.Bitmap
import com.example.photoapp.AI.chatWithGemini
import com.example.photoapp.database.DatabaseViewModel
import com.example.photoapp.database.data.FakturaDTO
import com.example.photoapp.database.data.ParagonDTO
import com.example.photoapp.database.data.ProduktFakturaDTO
import com.example.photoapp.database.data.ProduktParagonDTO
import kotlinx.serialization.json.Json
import android.util.Log
import com.example.photoapp.AI.DocumentType
import com.example.photoapp.database.data.OnlyProduktyRaportFiskalnyDTO
import com.example.photoapp.database.data.ProduktRaportFiskalnyDTO
import com.example.photoapp.database.data.RaportFiskalny
import com.example.photoapp.database.data.RaportFiskalnyDTO

class AcceptanceController(private val databaseViewModel: DatabaseViewModel) {
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
                Log.i("Dolan", "getting Prompt for faktura")
                // Przykład wywołania asynchronicznej operacji, np. API call do chatWithGemini
                chatWithGemini(geminiKey, bitmapPhoto, DocumentType.RAPORT_FISKALNY) { response, result ->
                    geminiPromptResult = result
                    callback(response, result)
                }
            } else if (addingPhotoFor == "produktRaportFiskalny") {
                Log.i("Dolan", "getting Prompt for faktura")
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
            databaseViewModel.addRecipe(jsonString = geminiPromptResult)
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
            databaseViewModel.addFaktura(jsonString = geminiPromptResult)
            Log.i("Dolan", "added Invoice")
        }
    }

    fun formatEachProduktFaktura(produkty: List<ProduktFakturaDTO>): String{
        return produkty.joinToString(separator = "\n"){ produkt ->
            """
                nazwaProduktu: ${produkt.nazwaProduktu}
                    jednostkaMiary: ${produkt.jednostkaMiary}
                    ilosc: ${produkt.ilosc}
                    wartoscNetto: ${produkt.wartoscNetto}
                    stawkaVat: ${produkt.stawkaVat}
                    podatekVat: ${produkt.podatekVat}
                    brutto: ${produkt.brutto}
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
                nrRachunkuBankowego: ${f.nrRachunkuBankowego}
                dataWystawienia: ${f.dataWystawienia}
                dataSprzedazy: ${f.dataSprzedazy}
                razemNetto: ${f.razemNetto}
                razemStawka: ${f.razemStawka}
                razemPodatek: ${f.razemPodatek}
                razemBrutto: ${f.razemBrutto}
                waluta: ${f.waluta}
                formaPlatnosci: ${f.formaPlatnosci}
            Produkty:
                |${formatEachProduktFaktura(f.produkty)}          
        """.trimIndent().trimMargin("|")
        return resultString
    }

    fun addRaportFiskalny(): Long {
        if (geminiPromptResult != "") {
            return databaseViewModel.addRaportFiskalny(jsonString = geminiPromptResult)
        }
        return 0
    }

    fun getRaportByID(id: Int): RaportFiskalny {
        return databaseViewModel.getRaportByID(id)
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
            databaseViewModel.addProduktyRaportFiskalny(jsonString = geminiPromptResult, raport)
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