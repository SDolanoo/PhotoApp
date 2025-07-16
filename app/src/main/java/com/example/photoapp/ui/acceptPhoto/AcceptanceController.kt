package com.example.photoapp.ui.acceptPhoto

import android.graphics.Bitmap
import com.example.photoapp.core.AI.chatWithGemini
import kotlinx.serialization.json.Json
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.photoapp.core.AI.DocumentType
import com.example.photoapp.core.database.data.FakturaDTO
import com.example.photoapp.core.database.data.ProduktFakturaDTO
import com.example.photoapp.features.faktura.data.faktura.FakturaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.collections.joinToString

@HiltViewModel
class AcceptanceController @Inject constructor(
    private val fakturaRepository: FakturaRepository,
) : ViewModel() {
    private var geminiPromptResult: String = ""

    fun processPhoto(
        addingPhotoFor: String?,
        geminiKey: String,
        bitmapPhoto: Bitmap?,
        onResult: (Boolean, String) -> Unit
    ) {
        getPrompt(addingPhotoFor, geminiKey, bitmapPhoto) { response, result ->
            if (response == 1) {
                val formattedText = when (addingPhotoFor) {
                    "faktura" -> formatPromptForFaktura()
                    else -> {"no adding photo for"}
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
            if (addingPhotoFor == "faktura") {
                Log.i("Dolan", "getting Prompt for faktura")
                // Przykład wywołania asynchronicznej operacji, np. API call do chatWithGemini
                chatWithGemini(geminiKey, bitmapPhoto, DocumentType.FAKTURA) { response, result ->
                    geminiPromptResult = result
                    callback(response, result)
                }
            }
        } ?: run {
            callback(2, "No valid image provided")
        }
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
                typFaktury: ${f.typFaktury}
                dataWystawienia: ${f.dataWystawienia}
                dataSprzedazy: ${f.dataSprzedazy}
                miejsceWystawienia: ${f.miejsceWystawienia}
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
}


