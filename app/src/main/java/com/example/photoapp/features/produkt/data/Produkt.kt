package com.example.photoapp.features.produkt.data

data class Produkt(
    var id: String = "",
    var nazwaProduktu: String = "",
    var jednostkaMiary: String = "",
    var cenaNetto: String = "",
    var stawkaVat: String = ""
){
    companion object {
        fun default() = Produkt(
            id = "0",
            nazwaProduktu = "Nowy Produkt",
            jednostkaMiary = "szt",
            cenaNetto = "100",
            stawkaVat = "23",
        )
    }
}
