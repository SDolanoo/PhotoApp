package com.example.photoapp.core.database.data.entities

import androidx.room.*

@Entity
data class Odbiorca(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "nazwa") val nazwa: String,
    @ColumnInfo(name = "nip") val nip: String,
    @ColumnInfo(name = "adres") val adres: String
)
