package com.example.photoapp.ui.raportFiskalny.data

import androidx.room.*
import java.util.*

@Entity
data class RaportFiskalny(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "dataDodania") val dataDodania: Date?
)

@Entity(
    foreignKeys = [
        ForeignKey(entity = RaportFiskalny::class, parentColumns = ["id"], childColumns = ["raportFiskalnyId"])
    ]
)
data class ProduktRaportFiskalny(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "raportFiskalnyId") val raportFiskalnyId: Int = 0,
    @ColumnInfo(name = "nrPLU") val nrPLU: String,
    @ColumnInfo(name = "ilosc") val ilosc: String?
)
