package com.example.photoapp.features.faktura.data.sprzedawca

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Sprzedawca(
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
)