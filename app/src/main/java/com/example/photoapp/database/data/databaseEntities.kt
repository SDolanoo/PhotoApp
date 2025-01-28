package com.example.photoapp.database.data

import androidx.room.*
import java.util.*




// TODO change id type to LONG later
@Entity
data class Uzytkownik(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "login") val login: String,
    @ColumnInfo(name = "password") val password: String,
    @ColumnInfo(name = "email") val email: String
)

@Entity
data class Odbiorca(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "nazwa") val nazwa: String,
    @ColumnInfo(name = "nip") val nip: String,
    @ColumnInfo(name = "adres") val adres: String
)

@Entity
data class Sprzedawca(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "nazwa") val nazwa: String,
    @ColumnInfo(name = "nip") val nip: String,
    @ColumnInfo(name = "adres") val adres: String
)

@Entity(
    foreignKeys = [
        ForeignKey(entity = Uzytkownik::class, parentColumns = ["id"], childColumns = ["uzytkownikId"])
    ]
)
data class Paragon(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "uzytkownikId") val uzytkownikId: Int,
    @ColumnInfo(name = "dataZakupu") val dataZakupu: Date?,
    @ColumnInfo(name = "nazwaSklepu") val nazwaSklepu: String,
    @ColumnInfo(name = "kwotaCalkowita") val kwotaCalkowita: Double
)

@Entity(
    foreignKeys = [
        ForeignKey(entity = Paragon::class, parentColumns = ["id"], childColumns = ["paragonId"]),
        ForeignKey(entity = Kategoria::class, parentColumns = ["id"], childColumns = ["kategoriaId"])
    ]
)
data class ProduktParagon(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "paragonId") val paragonId: Int,
    @ColumnInfo(name = "kategoriaId") val kategoriaId: Int?,
    @ColumnInfo(name = "nazwaProduktu") val nazwaProduktu: String,
    @ColumnInfo(name = "cenaSuma") val cenaSuma: Double,
    @ColumnInfo(name = "ilosc") val ilosc: Int
)

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

@Entity
data class Kategoria(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "nazwa") val nazwa: String
)





