package com.example.photoapp.database.data.entities

import androidx.room.*

@Entity
data class Kategoria(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "nazwa") val nazwa: String
)
