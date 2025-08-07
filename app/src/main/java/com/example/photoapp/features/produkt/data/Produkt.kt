package com.example.photoapp.features.produkt.data

import com.example.photoapp.core.utils.currentUserId

data class Produkt(
    var id: String = "",
    var uzytkownikId: String = currentUserId(),
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
