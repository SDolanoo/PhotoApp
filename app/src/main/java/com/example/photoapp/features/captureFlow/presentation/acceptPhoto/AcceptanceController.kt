package com.example.photoapp.features.captureFlow.presentation.acceptPhoto

import android.graphics.Bitmap
import com.example.photoapp.core.AI.chatWithGemini
import kotlinx.serialization.json.Json
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.photoapp.core.AI.DocumentType
import com.example.photoapp.core.database.data.FakturaDTO
import com.example.photoapp.core.utils.convertStringToDate
import com.example.photoapp.features.faktura.data.faktura.Faktura
import com.example.photoapp.features.faktura.data.faktura.FakturaRepository
import com.example.photoapp.features.faktura.data.faktura.Produkt
import com.example.photoapp.features.faktura.data.faktura.ProduktFaktura
import com.example.photoapp.features.faktura.presentation.details.ProduktFakturaZProduktem
import com.example.photoapp.features.odbiorca.data.Odbiorca
import com.example.photoapp.features.sprzedawca.data.Sprzedawca
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AcceptanceController @Inject constructor(
    private val fakturaRepository: FakturaRepository,
) : ViewModel() {
    private var geminiPromptResult: String = ""

    private val _faktura = MutableStateFlow<Faktura>(Faktura.default())
    val faktura: StateFlow<Faktura> = _faktura.asStateFlow()

    private val _sprzedawca = MutableStateFlow<Sprzedawca>(Sprzedawca.empty())
    val sprzedawca: StateFlow<Sprzedawca> = _sprzedawca.asStateFlow()

    private val _odbiorca = MutableStateFlow<Odbiorca>(Odbiorca.empty())
    val odbiorca: StateFlow<Odbiorca> = _odbiorca.asStateFlow()

    private val _produkty = MutableStateFlow<List<ProduktFakturaZProduktem>>(emptyList())
    val produkty: StateFlow<List<ProduktFakturaZProduktem>> = _produkty.asStateFlow()

    fun processPhoto(
        geminiKey: String,
        bitmapPhoto: Bitmap?,
        onResult: (Boolean, String) -> Unit
    ) {
        getPrompt(geminiKey, bitmapPhoto) { response, result ->
            if (response == 1) {
                formObjectsFromPrompt(geminiPromptResult) {
                    onResult(true, "text")
                }
            } else {
                onResult(false, result)
            }
        }
    }

    fun getPrompt(geminiKey: String, bitmapPhoto: Bitmap?, callback: (Int, String) -> Unit) {
        // Symulacja asynchronicznego pobierania danych
        bitmapPhoto?.let {
            Log.i("Dolan", "getting Prompt for faktura")
            // Przykład wywołania asynchronicznej operacji, np. API call do chatWithGemini
            chatWithGemini(geminiKey, bitmapPhoto, DocumentType.FAKTURA) { response, result ->
                geminiPromptResult = result
                callback(response, result)
            }

        } ?: run {
            callback(2, "No valid image provided")
        }
    }

    fun formObjectsFromPrompt(geminiPromptResult: String, callback: () -> Unit) {
        val coercingJson = Json { coerceInputValues = true }

        try {
            val fakturaDTO = coercingJson.decodeFromString<FakturaDTO>(geminiPromptResult)

            // 👉 FAKTURA
            val faktura = Faktura(
                uzytkownikId = 1L, // zakładamy, że masz domyślnego użytkownika
                odbiorcaId = 0L,   // tymczasowo — ustawiasz gdzieś później z bazy
                sprzedawcaId = 0L, // tymczasowo
                typFaktury = fakturaDTO.typFaktury,
                numerFaktury = fakturaDTO.numerFaktury,
                dataWystawienia = convertStringToDate(fakturaDTO.dataWystawienia),
                dataSprzedazy = convertStringToDate(fakturaDTO.dataSprzedazy),
                miejsceWystawienia = fakturaDTO.miejsceWystawienia,
                razemNetto = fakturaDTO.razemNetto,
                razemVAT = fakturaDTO.razemVAT ?: "0",
                razemBrutto = fakturaDTO.razemBrutto,
                doZaplaty = fakturaDTO.doZaplaty,
                waluta = fakturaDTO.waluta,
                formaPlatnosci = fakturaDTO.formaPlatnosci
            )
            setFaktura(faktura)
            Log.i("Dolan", "setFaktura $faktura")

            // 👉 SPRZEDAWCA
            val sprzedawca = Sprzedawca(
                id = 0L,
                nazwa = fakturaDTO.sprzedawca.nazwa,
                nip = fakturaDTO.sprzedawca.nip,
                adres = fakturaDTO.sprzedawca.adres,
                kodPocztowy = fakturaDTO.sprzedawca.kodPocztowy,
                miejscowosc = fakturaDTO.sprzedawca.miejscowosc,
                kraj = fakturaDTO.sprzedawca.kraj,
                opis = fakturaDTO.sprzedawca.opis,
                email = fakturaDTO.sprzedawca.email,
                telefon = fakturaDTO.sprzedawca.telefon
            )
            setSprzedawca(sprzedawca)
            Log.i("Dolan", "setSprzedawca $sprzedawca")

            // 👉 ODBIORCA
            val odbiorca = Odbiorca(
                id = 0L,
                nazwa = fakturaDTO.odbiorca.nazwa,
                nip = fakturaDTO.odbiorca.nip,
                adres = fakturaDTO.odbiorca.adres,
                kodPocztowy = fakturaDTO.odbiorca.kodPocztowy,
                miejscowosc = fakturaDTO.odbiorca.miejscowosc,
                kraj = fakturaDTO.odbiorca.kraj,
                opis = fakturaDTO.odbiorca.opis,
                email = fakturaDTO.odbiorca.email,
                telefon = fakturaDTO.odbiorca.telefon
            )
            setOdbiorca(odbiorca)
            Log.i("Dolan", "setOdbiorca $odbiorca")

            // 👉 PRODUKTY
            val produkty = fakturaDTO.produkty.mapIndexed { index, dto ->
                val produkt = Produkt(
                    id = 0L,
                    nazwaProduktu = dto.nazwaProduktu,
                    jednostkaMiary = dto.jednostkaMiary,
                    cenaNetto = dto.cenaNetto,
                    stawkaVat = dto.stawkaVat
                )
                val produktFaktura = ProduktFaktura(
                    id = -1L - index, // tymczasowy ID do odróżnienia
                    fakturaId = 0L,   // ustawisz po zapisaniu faktury
                    produktId = 0L,   // ustawisz po zapisaniu produktu
                    ilosc = dto.ilosc,
                    rabat = dto.rabat,
                    wartoscNetto = dto.wartoscNetto,
                    wartoscBrutto = dto.wartoscBrutto
                )
                ProduktFakturaZProduktem(produktFaktura = produktFaktura, produkt = produkt)
            }

            setProdukty(produkty)
            Log.i("Dolan", "setProdukty $produkty")

            "done ✅"
            callback()

        } catch (e: Exception) {
            Log.e("formObjectsFromPrompt", "Błąd: ${e.message}")
            "błąd ❌"
            callback()
        }
    }


    fun setFaktura(faktura: Faktura) {
        _faktura.value = faktura
    }

    fun setSprzedawca(sprzedawca: Sprzedawca) {
        _sprzedawca.value = sprzedawca
    }

    fun setOdbiorca(odbiorca: Odbiorca) {
        _odbiorca.value = odbiorca
    }

    fun setProdukty(produkty: List<ProduktFakturaZProduktem>) {
        _produkty.value = produkty
    }
}


