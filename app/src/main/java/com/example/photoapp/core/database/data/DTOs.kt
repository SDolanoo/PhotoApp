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
    @SerialName("jednostkaMiary") val jednostkaMiary: String = "none",
    @SerialName("ilosc") val ilosc: String = "999",
    @SerialName("cenaNetto") val cenaNetto: String = "999",
    @SerialName("wartoscNetto") val wartoscNetto: String = "999",
    @SerialName("wartoscBrutto") val wartoscBrutto: String = "999",
    @SerialName("stawkaVat") val stawkaVat: String = "999"
)

@Serializable
data class FakturaDTO(
    @SerialName("numerFaktury") val numerFaktury: String = "none",
    @SerialName("status") val status: String = "none",
    @SerialName("dataWystawienia") val dataWystawienia: String = "1999-01-01",
    @SerialName("dataSprzedazy") val dataSprzedazy: String = "1999-01-01",
    @SerialName("terminPlatnosci") val terminPlatnosci: String = "1999-01-01",
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