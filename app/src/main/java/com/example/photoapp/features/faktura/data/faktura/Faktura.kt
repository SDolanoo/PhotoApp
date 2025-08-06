package com.example.photoapp.features.faktura.data.faktura

import java.util.*

data class Faktura(
    var id: String = "",
    var uzytkownikId: String = "",
    var odbiorcaId: String = "",
    var sprzedawcaId: String = "",
    var typFaktury: String = "",
    var numerFaktury: String = "",
    var dataWystawienia: Date? = null,
    var dataSprzedazy: Date? = null,
    var miejsceWystawienia: String = "",
    var razemNetto: String = "",
    var razemVAT: String = "",
    var razemBrutto: String = "",
    var doZaplaty: String = "",
    var waluta: String = "",
    var formaPlatnosci: String = ""
) {
    companion object {
        fun default() = Faktura(
            id = "",
            uzytkownikId = "",
            odbiorcaId = "",
            sprzedawcaId = "",
            typFaktury = "Faktura",
            numerFaktury = "FV-TEST-001",
            dataWystawienia = Date(),
            dataSprzedazy = Date(),
            razemNetto = "100.00",
            razemVAT = "23",
            razemBrutto = "123.00",
            doZaplaty = "123.00",
            waluta = "PLN",
            formaPlatnosci = "Przelew",
            miejsceWystawienia = ""
        )
    }
}
