package com.example.photoapp.core.navigation

import com.dolan.photoapp.R

enum class NavBarDestinations(
    val route: String,
    val icon: Int,
    val label: String,
    val contentDescription: String
) {
    Faktury(PhotoAppDestinations.FAKTURA_SCREEN_ROUTE, R.drawable.baseline_home_filled_24, "Faktury", "Ekran faktur"),
    Excel(PhotoAppDestinations.EXCEL_PACKER_ROUTE, R.drawable.upload_file, "Export", "Ekran exportu"),
    Edycja(PhotoAppDestinations.EDITING_SELECTOR, R.drawable.baseline_edit_note_24, "Profil", "Ekran profilu");
}