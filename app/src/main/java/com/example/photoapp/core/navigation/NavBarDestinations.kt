package com.example.photoapp.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class NavBarDestinations(
    val route: String,
    val icon: ImageVector,
    val label: String,
    val contentDescription: String
) {
    Faktury(PhotoAppDestinations.FAKTURA_SCREEN_ROUTE, Icons.Default.Home, "Faktury", "Ekran faktur"),
    Excel(PhotoAppDestinations.EXCEL_PACKER_ROUTE, Icons.Default.Settings, "Export", "Ekran exportu"),
    Edycja(PhotoAppDestinations.EDITING_SELECTOR, Icons.Default.Person, "Profil", "Ekran profilu");
}