package com.example.photoapp.features.paragon.data

import androidx.room.*
import com.example.photoapp.database.data.entities.Kategoria
import com.example.photoapp.database.data.entities.Uzytkownik
import java.util.*

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
