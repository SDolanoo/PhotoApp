package com.example.photoapp.core.database.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
    @SerialName("ilosc") val ilosc: String = "999",
    @SerialName("jednostkaMiary") val jednostkaMiary: String = "none",
    @SerialName("cenaNetto") val cenaNetto: String = "999",
    @SerialName("stawkaVat") val stawkaVat: String = "999",
    @SerialName("wartoscNetto") val wartoscNetto: String = "999",
    @SerialName("wartoscBrutto") val wartoscBrutto: String = "999",
    @SerialName("rabat") val rabat: String = "999",
    @SerialName("pkwiu") val pkwiu: String = "999",
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
    @SerialName("waluta") val waluta: String = "999",
    @SerialName("formaPlatnosci") val formaPlatnosci: String = "none",

    val odbiorca: OdbiorcaDTO,
    val sprzedawca: SprzedawcaDTO,
    val produkty: List<ProduktFakturaDTO>
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
data class ProduktRaportFiskalnyDTO(
    @SerialName("nrPLU") val nrPLU: String = "none",
    @SerialName("ilosc") val ilosc: String = "none"
)

@Serializable
data class RaportFiskalnyDTO(
    @SerialName("dataDodania") val dataDodania: String = "1999-01-01",
    val produkty: List<ProduktRaportFiskalnyDTO>
)

@Serializable
data class OnlyProduktyRaportFiskalnyDTO(
    val produkty: List<ProduktRaportFiskalnyDTO>
)