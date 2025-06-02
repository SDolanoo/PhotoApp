package com.example.photoapp.core.database.data.entities

import androidx.room.*

@Entity
data class Uzytkownik(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "login") val login: String,
    @ColumnInfo(name = "password") val password: String,
    @ColumnInfo(name = "email") val email: String
)
