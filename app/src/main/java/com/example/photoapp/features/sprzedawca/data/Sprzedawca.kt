package com.example.photoapp.features.sprzedawca.data

data class Sprzedawca(
    var id: String = "",
    var nazwa: String = "",
    var nip: String = "",
    var adres: String = "",
    var kodPocztowy: String = "",
    var miejscowosc: String = "",
    var kraj: String = "",
    var opis: String = "",
    var email: String = "",
    var telefon: String = ""
) {
    companion object {
        fun empty() = Sprzedawca()
    }
}
