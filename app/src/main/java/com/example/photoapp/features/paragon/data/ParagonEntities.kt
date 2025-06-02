package com.example.photoapp.features.paragon.data

import androidx.room.*
import com.example.photoapp.core.database.data.entities.Kategoria
import com.example.photoapp.core.database.data.entities.Uzytkownik
import java.util.*

@Entity(
    foreignKeys = [
        ForeignKey(entity = Uzytkownik::class, parentColumns = ["id"], childColumns = ["uzytkownikId"])
    ]
)
data class Paragon(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "uzytkownikId") val uzytkownikId: Long,
    @ColumnInfo(name = "dataZakupu") val dataZakupu: Date?,
    @ColumnInfo(name = "nazwaSklepu") val nazwaSklepu: String,
    @ColumnInfo(name = "kwotaCalkowita") val kwotaCalkowita: Double
)

@Entity(
    foreignKeys = [
        ForeignKey(entity = Paragon::class, parentColumns = ["id"], childColumns = ["paragonId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Kategoria::class, parentColumns = ["id"], childColumns = ["kategoriaId"], onDelete = ForeignKey.SET_NULL, deferred = true)
    ]
)
data class ProduktParagon(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "paragonId") val paragonId: Long,
    @ColumnInfo(name = "kategoriaId", defaultValue = "NULL") val kategoriaId: Long?,
    @ColumnInfo(name = "nazwaProduktu") val nazwaProduktu: String,
    @ColumnInfo(name = "cenaSuma") val cenaSuma: Double,
    @ColumnInfo(name = "ilosc") val ilosc: Int
)
