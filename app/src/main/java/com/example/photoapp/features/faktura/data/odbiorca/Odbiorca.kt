package com.example.photoapp.features.faktura.data.odbiorca

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.photoapp.features.faktura.data.sprzedawca.Sprzedawca

@Entity
data class Odbiorca(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "nazwa") val nazwa: String,
    @ColumnInfo(name = "nip") val nip: String,
    @ColumnInfo(name = "adres") val adres: String,
    @ColumnInfo(name = "kod_pocztowy") val kodPocztowy: String = "",
    @ColumnInfo(name = "miejscowosc") val miejscowosc: String = "",
    @ColumnInfo(name = "kraj") val kraj: String = "",
    @ColumnInfo(name = "opis") val opis: String = "",
    @ColumnInfo(name = "email") val email: String = "",
    @ColumnInfo(name = "telefon") val telefon: String = ""
) {
    companion object {
        fun empty() = Odbiorca(
            id = 0,
            nazwa = "", nip = "", adres = "",
            kodPocztowy = "", miejscowosc = "",
            kraj = "", opis = "", email = "", telefon = ""
        )
    }
}