package com.example.photoapp.features.faktura.data.faktura

import androidx.room.*
import com.example.photoapp.core.database.data.entities.Uzytkownik
import com.example.photoapp.features.faktura.data.odbiorca.Odbiorca
import com.example.photoapp.features.faktura.data.sprzedawca.Sprzedawca
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
    @ColumnInfo(name = "typFaktury") val typFaktury: String,
    @ColumnInfo(name = "numerFaktury") val numerFaktury: String,
    @ColumnInfo(name = "dataWystawienia") val dataWystawienia: Date?,
    @ColumnInfo(name = "dataSprzedazy") val dataSprzedazy: Date?,
    @ColumnInfo(name = "miejsceWystawienia") val miejsceWystawienia: String,
    @ColumnInfo(name = "razemNetto") val razemNetto: String,
    @ColumnInfo(name = "razemVAT") val razemVAT: String,
    @ColumnInfo(name = "razemBrutto") val razemBrutto: String,
    @ColumnInfo(name = "doZaplaty") val doZaplaty: String,
    @ColumnInfo(name = "waluta") val waluta: String,
    @ColumnInfo(name = "formaPlatnosci") val formaPlatnosci: String,
    @ColumnInfo(name = "produktyId") val produktyId: List<Long>
) {
    companion object {
        fun default() = Faktura(
            id = 1L,
            uzytkownikId = 1L,
            odbiorcaId = 1L,
            sprzedawcaId = 1L,
            typFaktury = "Faktura",
            numerFaktury = "FV-TEST-001",
            dataWystawienia = Date(),
            dataSprzedazy = Date(),
            razemNetto = "100.00",
            razemVAT = "23",
            razemBrutto = "123.00",
            doZaplaty = "123.00",
            waluta = "PLN",
            formaPlatnosci = "Przelew",
            miejsceWystawienia = "",
            produktyId = emptyList()
        )
    }
}

@Entity
data class ProduktFaktura(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "nazwaProduktu") val nazwaProduktu: String,
    @ColumnInfo(name = "ilosc") val ilosc: String,
    @ColumnInfo(name = "jednostkaMiary") val jednostkaMiary: String,
    @ColumnInfo(name = "cenaNetto") val cenaNetto: String,
    @ColumnInfo(name = "stawkaVat") val stawkaVat: String,
    @ColumnInfo(name = "wartoscNetto") val wartoscNetto: String,
    @ColumnInfo(name = "wartoscBrutto") val wartoscBrutto: String,
    @ColumnInfo(name = "rabat") val rabat: String,
    @ColumnInfo(name = "pkwiu") val pkwiu: String,
)
