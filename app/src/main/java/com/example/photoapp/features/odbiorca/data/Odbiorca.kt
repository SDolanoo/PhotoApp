package com.example.photoapp.features.odbiorca.data

import com.example.photoapp.core.utils.currentUserId


data class Odbiorca(
    var id: String = "",
    var uzytkownikId: String = currentUserId(),
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
        fun empty() = Odbiorca()
    }
}
