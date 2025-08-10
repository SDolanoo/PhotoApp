package com.example.photoapp.features.captureFlow.presentation.acceptPhoto

import android.annotation.SuppressLint
import android.graphics.Bitmap
import com.example.photoapp.core.AI.chatWithGemini
import kotlinx.serialization.json.Json
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.photoapp.core.AI.DocumentType
import com.example.photoapp.core.database.data.FakturaDTO
import com.example.photoapp.core.database.data.FakturkaDTO
import com.example.photoapp.core.database.data.OdbiorcaxSprzedawcaDTO
import com.example.photoapp.core.database.data.ProduktyDTO
import com.example.photoapp.core.utils.convertDoubleToString
import com.example.photoapp.core.utils.convertStringToDate
import com.example.photoapp.features.faktura.data.faktura.Faktura
import com.example.photoapp.features.faktura.data.faktura.FakturaRepository
import com.example.photoapp.features.faktura.presentation.details.ProduktFakturaZProduktem
import com.example.photoapp.features.odbiorca.data.Odbiorca
import com.example.photoapp.features.produkt.data.Produkt
import com.example.photoapp.features.produkt.data.ProduktFaktura
import com.example.photoapp.features.sprzedawca.data.Sprzedawca
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AcceptanceController @Inject constructor(
    private val fakturaRepository: FakturaRepository,
) : ViewModel() {
    private var geminiPromptResult: List<String> = listOf()

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
        onResult: (Boolean, List<String>) -> Unit
    ) {
        getPrompt(geminiKey, bitmapPhoto) { response, result ->
            if (response == 1) {
                formObjectsFrom3Prompt(geminiPromptResult) {
                    onResult(true, listOf("text"))
                }
            } else {
                onResult(false, result)
            }
        }
    }

    fun getPrompt(geminiKey: String, bitmapPhoto: Bitmap?, callback: (Int, List<String>) -> Unit) {
        // Symulacja asynchronicznego pobierania danych
        bitmapPhoto?.let {
            Log.i("Dolan", "getting Prompt for faktura")
            // Przykład wywołania asynchronicznej operacji, np. API call do chatWithGemini
            chatWithGemini(geminiKey, bitmapPhoto, DocumentType.FAKTURA) { response, result ->
                geminiPromptResult = result
                callback(response, result)
            }

        } ?: run {
            callback(2, listOf("No valid image provided"))
        }
    }




    fun setFaktura(faktura: Faktura, callback: () -> Unit) {
        _faktura.value = faktura
        Log.i("Dolan", "setFakturafunction  ${_faktura.value}")
        callback()
    }

    fun setSprzedawca(sprzedawca: Sprzedawca, callback: () -> Unit) {
        _sprzedawca.value = sprzedawca
        callback()
    }

    fun setOdbiorca(odbiorca: Odbiorca, callback: () -> Unit) {
        _odbiorca.value = odbiorca
        callback()
    }

    fun setProdukty(produkty: List<ProduktFakturaZProduktem>, callback: () -> Unit) {
        _produkty.value = produkty
        callback()
    }

    @SuppressLint("DefaultLocale")
    fun formObjectsFromPrompt(geminiPromptResult: String, callback: () -> Unit) {
        val coercingJson = Json { coerceInputValues = true }

        fun extractJson(raw: String): String? {
            val cleaned = raw
                .replace("```json", "", ignoreCase = true)
                .replace("```", "")
                .trim()

            val startIndex = cleaned.indexOfFirst { it == '{' || it == '[' }
            if (startIndex == -1) return null

            return cleaned.substring(startIndex)
        }

        try {
            val jsonText = extractJson(geminiPromptResult)
            if (jsonText == null) {
                Log.e("formObjectsFromPrompt", "Nie znaleziono poprawnego JSON-a.")
                callback()
                return
            }

            val fakturaDTO = coercingJson.decodeFromString<FakturaDTO>(jsonText)

            val dataWystawienia = if (fakturaDTO.dataWystawienia == "null" && fakturaDTO.dataSprzedazy != "null") fakturaDTO.dataSprzedazy else fakturaDTO.dataWystawienia
            val dataSprzedazy = if (fakturaDTO.dataSprzedazy == "null" && fakturaDTO.dataWystawienia != "null") fakturaDTO.dataWystawienia else fakturaDTO.dataSprzedazy

            // 👉 SPRZEDAWCA
            val sprzedawca = Sprzedawca(
                id = "0L",
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


            // 👉 ODBIORCA
            val odbiorca = Odbiorca(
                id = "0L",
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
            var razemNetto = 0.0
            var razemBrutto = 0.0

            // 👉 PRODUKTY
            val produkty = fakturaDTO.produkty.mapIndexed { index, dto ->
                val produkt = Produkt(
                    id = "0L",
                    nazwaProduktu = dto.nazwaProduktu,
                    jednostkaMiary = dto.jednostkaMiary,
                    cenaNetto = dto.cenaNetto,
                    stawkaVat = dto.stawkaVat
                )
                val produktFaktura = ProduktFaktura(
                    id = (-1L - index).toString(), // tymczasowy ID do odróżnienia
                    fakturaId = "0L",   // ustawisz po zapisaniu faktury
                    produktId = "0L",   // ustawisz po zapisaniu produktu
                    ilosc = dto.ilosc,
                    rabat = dto.rabat,
                    wartoscNetto = dto.wartoscNetto,
                    wartoscBrutto = dto.wartoscBrutto
                )
                razemNetto += produktFaktura.wartoscNetto.replace(",", ".").toDoubleOrNull() ?: 0.0
                razemBrutto += produktFaktura.wartoscBrutto.replace(",", ".").toDoubleOrNull() ?: 0.0
                ProduktFakturaZProduktem(produktFaktura = produktFaktura, produkt = produkt)
            }

            // 👉 FAKTURA
            val faktura = Faktura(
                uzytkownikId = FirebaseAuth.getInstance().currentUser?.uid.toString(), // zakładamy, że masz domyślnego użytkownika
                odbiorcaId = "0L",   // tymczasowo — ustawiasz gdzieś później z bazy
                sprzedawcaId = "0L", // tymczasowo
                typFaktury = fakturaDTO.typFaktury,
                numerFaktury = fakturaDTO.numerFaktury,
                dataWystawienia = convertStringToDate(dataWystawienia),
                dataSprzedazy = convertStringToDate(dataSprzedazy),
                miejsceWystawienia = fakturaDTO.miejsceWystawienia,
                razemNetto = convertDoubleToString(razemNetto),
                razemVAT = convertDoubleToString(razemBrutto-razemNetto),
                razemBrutto = convertDoubleToString(razemBrutto),
                doZaplaty = convertDoubleToString(razemBrutto),
                waluta = fakturaDTO.waluta,
                formaPlatnosci = fakturaDTO.formaPlatnosci
            )

            setFaktura(faktura) {
                Log.i("Dolan", "setFaktura $faktura")
                setSprzedawca(sprzedawca) {
                    Log.i("Dolan", "setSprzedawca $sprzedawca")
                    setOdbiorca(odbiorca) {
                        Log.i("Dolan", "setOdbiorca $odbiorca")
                        setProdukty(produkty) {
                            Log.i("Dolan", "setProdukty $produkty")
                            "done ✅"
                            callback()
                        }
                    }
                }
            }




        } catch (e: Exception) {
            Log.e("formObjectsFromPrompt", "Błąd: ${e.message}")
            "błąd ❌"
            callback()
        }
    }

    @SuppressLint("DefaultLocale")
    fun formObjectsFrom3Prompt(geminiPromptResult: List<String>, callback: () -> Unit) {
        val coercingJson = Json { coerceInputValues = true }

        fun extractJson(raw: String): String? {
            val cleaned = raw
                .replace("```json", "", ignoreCase = true)
                .replace("```", "")
                .trim()

            val startIndex = cleaned.indexOfFirst { it == '{' || it == '[' }
            if (startIndex == -1) return null

            return cleaned.substring(startIndex)
        }

        try {
            val jsonText0 = extractJson(geminiPromptResult[0])
            if (jsonText0 == null) {
                Log.e("formObjectsFromPrompt", "Nie znaleziono poprawnego JSON-a.")
                callback()
                return
            }
            val jsonText1 = extractJson(geminiPromptResult[1])
            if (jsonText1 == null) {
                Log.e("formObjectsFromPrompt", "Nie znaleziono poprawnego JSON-a.")
                callback()
                return
            }
            val jsonText2 = extractJson(geminiPromptResult[2])
            if (jsonText2 == null) {
                Log.e("formObjectsFromPrompt", "Nie znaleziono poprawnego JSON-a.")
                callback()
                return
            }

            val fakturaDTO = coercingJson.decodeFromString<FakturkaDTO>(jsonText0)
            val odbiorca = Json.decodeFromString<OdbiorcaxSprzedawcaDTO>(jsonText1).odbiorca
            val sprzedawca = Json.decodeFromString<OdbiorcaxSprzedawcaDTO>(jsonText1).sprzedawca
            val produktyDTO = coercingJson.decodeFromString<ProduktyDTO>(jsonText2)

            val dataWystawienia = if (fakturaDTO.dataWystawienia == "null" && fakturaDTO.dataSprzedazy != "null") fakturaDTO.dataSprzedazy else fakturaDTO.dataWystawienia
            val dataSprzedazy = if (fakturaDTO.dataSprzedazy == "null" && fakturaDTO.dataWystawienia != "null") fakturaDTO.dataWystawienia else fakturaDTO.dataSprzedazy

            // 👉 SPRZEDAWCA
            val sprzedawcaObj = Sprzedawca(
                id = "0L",
                nazwa = sprzedawca.nazwa,
                nip = sprzedawca.nip,
                adres = sprzedawca.adres,
                kodPocztowy = sprzedawca.kodPocztowy,
                miejscowosc = sprzedawca.miejscowosc,
                kraj = sprzedawca.kraj,
                opis = sprzedawca.opis,
                email = sprzedawca.email,
                telefon = sprzedawca.telefon
            )


            // 👉 ODBIORCA
            val odbiorcaObj = Odbiorca(
                id = "0L",
                nazwa = odbiorca.nazwa,
                nip = odbiorca.nip,
                adres = odbiorca.adres,
                kodPocztowy = odbiorca.kodPocztowy,
                miejscowosc = odbiorca.miejscowosc,
                kraj = odbiorca.kraj,
                opis = odbiorca.opis,
                email = odbiorca.email,
                telefon = odbiorca.telefon
            )

            var razemNetto = 0.0
            var razemBrutto = 0.0

            // 👉 PRODUKTY
            val produkty = produktyDTO.produkty.mapIndexed { index, dto ->
                val produkt = Produkt(
                    id = "0L",
                    nazwaProduktu = dto.nazwaProduktu,
                    jednostkaMiary = dto.jednostkaMiary,
                    cenaNetto = dto.cenaNetto,
                    stawkaVat = dto.stawkaVat
                )
                val produktFaktura = ProduktFaktura(
                    id = (-1L - index).toString(), // tymczasowy ID do odróżnienia
                    fakturaId = "0L",   // ustawisz po zapisaniu faktury
                    produktId = "0L",   // ustawisz po zapisaniu produktu
                    ilosc = dto.ilosc,
                    rabat = dto.rabat,
                    wartoscNetto = dto.wartoscNetto,
                    wartoscBrutto = dto.wartoscBrutto
                )
                razemNetto += produktFaktura.wartoscNetto.replace(",", ".").toDoubleOrNull() ?: 0.0
                razemBrutto += produktFaktura.wartoscBrutto.replace(",", ".").toDoubleOrNull() ?: 0.0
                ProduktFakturaZProduktem(produktFaktura = produktFaktura, produkt = produkt)
            }

            // 👉 FAKTURA
            val faktura = Faktura(
                uzytkownikId = FirebaseAuth.getInstance().currentUser?.uid.toString(), // zakładamy, że masz domyślnego użytkownika
                odbiorcaId = "0L",   // tymczasowo — ustawiasz gdzieś później z bazy
                sprzedawcaId = "0L", // tymczasowo
                typFaktury = fakturaDTO.typFaktury,
                numerFaktury = fakturaDTO.numerFaktury,
                dataWystawienia = convertStringToDate(dataWystawienia),
                dataSprzedazy = convertStringToDate(dataSprzedazy),
                miejsceWystawienia = fakturaDTO.miejsceWystawienia,
                razemNetto = convertDoubleToString(razemNetto),
                razemVAT = convertDoubleToString(razemBrutto-razemNetto),
                razemBrutto = convertDoubleToString(razemBrutto),
                doZaplaty = convertDoubleToString(razemBrutto),
                waluta = fakturaDTO.waluta,
                formaPlatnosci = fakturaDTO.formaPlatnosci
            )

            setFaktura(faktura) {
                Log.i("Dolan", "setFaktura $faktura")
                setSprzedawca(sprzedawcaObj) {
                    Log.i("Dolan", "setSprzedawca $sprzedawca")
                    setOdbiorca(odbiorcaObj) {
                        Log.i("Dolan", "setOdbiorca $odbiorca")
                        setProdukty(produkty) {
                            Log.i("Dolan", "setProdukty $produkty")
                            "done ✅"
                            callback()
                        }
                    }
                }
            }




        } catch (e: Exception) {
            Log.e("formObjectsFromPrompt", "Błąd: ${e.message}")
            "błąd ❌"
            callback()
        }
    }
}


