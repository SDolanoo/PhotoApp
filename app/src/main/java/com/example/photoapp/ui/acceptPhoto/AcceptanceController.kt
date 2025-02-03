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
import com.example.photoapp.database.data.ProduktRaportFiskalnyDTO
import com.example.photoapp.database.data.RaportFiskalnyDTO

class AcceptanceController(private val databaseViewModel: DatabaseViewModel) {
    private var geminiPromptResult: String = ""

    fun retry(imagePath: String) {
        // Handle retry logic here
    }

    fun getPrompt(addingPhotoFor: String?, geminiKey: String, bitmapPhoto: Bitmap?, callback: (String) -> Unit) {
        // Symulacja asynchronicznego pobierania danych
        bitmapPhoto?.let {
            if (addingPhotoFor == "paragon") {
                Log.i("Dolan", "getting Prompt for paragon")
                // Przykład wywołania asynchronicznej operacji, np. API call do chatWithGemini
                chatWithGemini(geminiKey, bitmapPhoto, DocumentType.PARAGON) { result ->
                    geminiPromptResult = result
                    callback(result)
                }
            } else if (addingPhotoFor == "faktura") {
                Log.i("Dolan", "getting Prompt for faktura")
                // Przykład wywołania asynchronicznej operacji, np. API call do chatWithGemini
                chatWithGemini(geminiKey, bitmapPhoto, DocumentType.FAKTURA) { result ->
                    geminiPromptResult = result
                    callback(result)
                }
            } else if (addingPhotoFor == "raportFiskalny") {
                Log.i("Dolan", "getting Prompt for faktura")
                // Przykład wywołania asynchronicznej operacji, np. API call do chatWithGemini
                chatWithGemini(geminiKey, bitmapPhoto, DocumentType.RAPORT_FISKALNY) { result ->
                    geminiPromptResult = result
                    callback(result)
                }
            } else if (addingPhotoFor == "produktRaportFiskalny") {
                Log.i("Dolan", "getting Prompt for faktura")
                // Przykład wywołania asynchronicznej operacji, np. API call do chatWithGemini
                chatWithGemini(geminiKey, bitmapPhoto, DocumentType.PRODUCTS_RAPORT_FISKALNY) { result ->
                    geminiPromptResult = result
                    callback(result)
                }
            }
        } ?: run {
            callback("No valid image provided")
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

    fun addRaportFiskalny() {
        if (geminiPromptResult != "") {
            databaseViewModel.addRaportFiskalny(jsonString = geminiPromptResult)
        }
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

    fun addProduktRaportFiskalny() {
        if (geminiPromptResult != "") {
            databaseViewModel.addProduktyRaportFiskalny(jsonString = geminiPromptResult)
        }
    }

    fun formatPromptForProductRaportFiskalny(): String {
        val coercingJson = Json { coerceInputValues = true }
        val p = coercingJson.decodeFromString<ProduktRaportFiskalnyDTO>(geminiPromptResult)
        val resultString = """
                nrPLU: ${p.nrPLU}
                    ilosc: ${p.ilosc}
            """.trimIndent()
        return resultString
    }

}