package com.example.photoapp.features.produkt.data

data class ProduktFaktura(
    var id: String = "",
    var produktId: String = "",
    var fakturaId: String = "",
    var ilosc: String = "",
    var rabat: String = "",
    var wartoscNetto: String = "",
    var wartoscBrutto: String = ""
){
    companion object {
        fun default() = ProduktFaktura(
            id = "0",
            fakturaId = "1",
            produktId = "1",
            ilosc = "1",
            rabat = "0",
            wartoscNetto = "100",
            wartoscBrutto = "123"
        )
    }
}
