package com.example.photoapp.core.database.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProduktFakturaDTO(
    // Dane produktu
    @SerialName("nazwaProduktu") val nazwaProduktu: String,
    @SerialName("jednostkaMiary") val jednostkaMiary: String,
    @SerialName("cenaNetto") val cenaNetto: String,
    @SerialName("stawkaVat") val stawkaVat: String,

    // Dane pozycji
    @SerialName("ilosc") val ilosc: String,
    @SerialName("rabat") val rabat: String,
    @SerialName("wartoscNetto") val wartoscNetto: String,
    @SerialName("wartoscBrutto") val wartoscBrutto: String,

    // Dodatkowe, jeśli potrzebujesz (opcjonalnie)
    @SerialName("pkwiu") val pkwiu: String = ""
)

@Serializable
data class FakturaDTO(
    @SerialName("typFaktury") val typFaktury: String = "none",
    @SerialName("numerFaktury") val numerFaktury: String = "none",
    @SerialName("dataWystawienia") val dataWystawienia: String = "1999-01-01",
    @SerialName("dataSprzedazy") val dataSprzedazy: String = "1999-01-01",
    @SerialName("miejsceWystawienia") val miejsceWystawienia: String = "none",
    @SerialName("razemNetto") val razemNetto: String = "999",
    @SerialName("razemVAT") val razemVAT: String? = "999",
    @SerialName("razemBrutto") val razemBrutto: String = "999",
    @SerialName("doZaplaty") val doZaplaty: String = "999",
    @SerialName("waluta") val waluta: String = "PLN",
    @SerialName("formaPlatnosci") val formaPlatnosci: String = "Przelew",

    // Osoby
    val odbiorca: OdbiorcaDTO,
    val sprzedawca: SprzedawcaDTO,

    // Pozycje faktury (czyli lista produktów + info pozycji)
    val produkty: List<ProduktFakturaDTO>
)


@Serializable
data class ProduktDTO(
    @SerialName("nazwaProduktu") val nazwaProduktu: String = "none",
    @SerialName("jednostkaMiary") val jednostkaMiary: String = "none",
    @SerialName("cenaNetto") val cenaNetto: String = "0",
    @SerialName("stawkaVat") val stawkaVat: String = "0"
)


@Serializable
data class OdbiorcaDTO(
    @SerialName("nazwa") val nazwa: String = "",
    @SerialName("nip") val nip: String = "",
    @SerialName("adres") val adres: String = "",
    @SerialName("kodPocztowy") val kodPocztowy: String = "",
    @SerialName("miejscowosc") val miejscowosc: String = "",
    @SerialName("kraj") val kraj: String = "",
    @SerialName("opis") val opis: String = "",
    @SerialName("email") val email: String = "",
    @SerialName("telefon") val telefon: String = ""
)

@Serializable
data class SprzedawcaDTO(
    @SerialName("nazwa") val nazwa: String = "",
    @SerialName("nip") val nip: String = "",
    @SerialName("adres") val adres: String = "",
    @SerialName("kodPocztowy") val kodPocztowy: String = "",
    @SerialName("miejscowosc") val miejscowosc: String = "",
    @SerialName("kraj") val kraj: String = "",
    @SerialName("opis") val opis: String = "",
    @SerialName("email") val email: String = "",
    @SerialName("telefon") val telefon: String = ""
)

@Serializable
data class OdbiorcaxSprzedawcaDTO(
    @SerialName("odbiorca") val odbiorca: OdbiorcaDTO,
    @SerialName("sprzedawca") val sprzedawca: SprzedawcaDTO
)

@Serializable
data class ProduktyDTO(
    val produkty: List<ProduktFakturaDTO>
)
