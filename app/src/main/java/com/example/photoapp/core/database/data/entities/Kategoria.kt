package com.example.photoapp.core.database.data.entities

import androidx.room.*

@Entity
data class Kategoria(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "nazwa") val nazwa: String
)
