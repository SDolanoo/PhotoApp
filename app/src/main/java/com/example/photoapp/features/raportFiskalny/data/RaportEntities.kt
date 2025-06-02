package com.example.photoapp.features.raportFiskalny.data

import androidx.room.*
import java.util.*

@Entity
data class RaportFiskalny(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "dataDodania") val dataDodania: Date?
)

@Entity(
    foreignKeys = [
        ForeignKey(entity = RaportFiskalny::class, parentColumns = ["id"], childColumns = ["raportFiskalnyId"])
    ]
)
data class ProduktRaportFiskalny(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "raportFiskalnyId") val raportFiskalnyId: Long = 0,
    @ColumnInfo(name = "nrPLU") val nrPLU: String,
    @ColumnInfo(name = "ilosc") val ilosc: String?
)
