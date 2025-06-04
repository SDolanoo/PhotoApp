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
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "uzytkownikId") val uzytkownikId: Long,
    @ColumnInfo(name = "odbiorcaId") val odbiorcaId: Long,
    @ColumnInfo(name = "sprzedawcaId") val sprzedawcaId: Long,
    @ColumnInfo(name = "numerFaktury") val numerFaktury: String,
    @ColumnInfo(name = "status") val status: String,
    @ColumnInfo(name = "dataWystawienia") val dataWystawienia: Date?,
    @ColumnInfo(name = "dataSprzedazy") val dataSprzedazy: Date?,
    @ColumnInfo(name = "terminPlatnosci") val terminPlatnosci: Date?,
    @ColumnInfo(name = "razemNetto") val razemNetto: String,
    @ColumnInfo(name = "razemVAT") val razemVAT: String,
    @ColumnInfo(name = "razemBrutto") val razemBrutto: String,
    @ColumnInfo(name = "doZaplaty") val doZaplaty: String,
    @ColumnInfo(name = "waluta") val waluta: String,
    @ColumnInfo(name = "formaPlatnosci") val formaPlatnosci: String
)

@Entity(
    foreignKeys = [
        ForeignKey(entity = Faktura::class, parentColumns = ["id"], childColumns = ["fakturaId"])
    ]
)
data class ProduktFaktura(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "fakturaId") val fakturaId: Long,
    @ColumnInfo(name = "nazwaProduktu") val nazwaProduktu: String,
    @ColumnInfo(name = "jednostkaMiary") val jednostkaMiary: String,
    @ColumnInfo(name = "ilosc") val ilosc: String,
    @ColumnInfo(name = "cenaNetto") val cenaNetto: String,
    @ColumnInfo(name = "wartoscNetto") val wartoscNetto: String,
    @ColumnInfo(name = "wartoscBrutto") val wartoscBrutto: String,
    @ColumnInfo(name = "stawkaVat") val stawkaVat: String
)
