package com.example.photoapp.features.faktura.data

import androidx.room.*
import com.example.photoapp.core.database.data.entities.Odbiorca
import com.example.photoapp.core.database.data.entities.Sprzedawca
import com.example.photoapp.core.database.data.entities.Uzytkownik
import java.util.*

@Entity(
    foreignKeys = [
        ForeignKey(entity = Uzytkownik::class, parentColumns = ["id"], childColumns = ["uzytkownikId"]),
        ForeignKey(entity = Odbiorca::class, parentColumns = ["id"], childColumns = ["odbiorcaId"]),
        ForeignKey(entity = Sprzedawca::class, parentColumns = ["id"], childColumns = ["sprzedawcaId"])
    ]
)
data class Faktura(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "uzytkownikId") val uzytkownikId: Int,
    @ColumnInfo(name = "odbiorcaId") val odbiorcaId: Int,
    @ColumnInfo(name = "sprzedawcaId") val sprzedawcaId: Int,
    @ColumnInfo(name = "numerFaktury") val numerFaktury: String,
    @ColumnInfo(name = "nrRachunkuBankowego") val nrRachunkuBankowego: String?,
    @ColumnInfo(name = "dataWystawienia") val dataWystawienia: Date?,
    @ColumnInfo(name = "dataSprzedazy") val dataSprzedazy: Date?,
    @ColumnInfo(name = "razemNetto") val razemNetto: String,
    @ColumnInfo(name = "razemStawka") val razemStawka: String,
    @ColumnInfo(name = "razemPodatek") val razemPodatek: String,
    @ColumnInfo(name = "razemBrutto") val razemBrutto: String,
    @ColumnInfo(name = "waluta") val waluta: String,
    @ColumnInfo(name = "formaPlatnosci") val formaPlatnosci: String
)

@Entity(
    foreignKeys = [
        ForeignKey(entity = Faktura::class, parentColumns = ["id"], childColumns = ["fakturaId"])
    ]
)
data class ProduktFaktura(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "fakturaId") val fakturaId: Int,
    @ColumnInfo(name = "nazwaProduktu") val nazwaProduktu: String,
    @ColumnInfo(name = "jednostkaMiary") val jednostkaMiary: String?,
    @ColumnInfo(name = "ilosc") val ilosc: String?,
    @ColumnInfo(name = "wartoscNetto") val wartoscNetto: String,
    @ColumnInfo(name = "stawkaVat") val stawkaVat: String,
    @ColumnInfo(name = "podatekVat") val podatekVat: String,
    @ColumnInfo(name = "brutto") val brutto: String
)
