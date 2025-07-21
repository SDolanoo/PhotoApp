package com.example.photoapp.features.faktura.composables.readOnly

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.photoapp.features.faktura.composables.common.InfoRow
import com.example.photoapp.features.faktura.composables.common.SectionCard
import com.example.photoapp.features.faktura.composables.common.TwoColumnRow

@Composable
fun InvoiceReadOnly(
    modifier: Modifier,
    fields: List<String>
) {
    Column(
        modifier = modifier.padding(4.dp)
    ) {
        SectionCard(
            title = "Invoice Details",
            icon = Icons.Default.AccountCircle
        ) {
            TwoColumnRow("Typ", fields[0], "Numer", fields[1])
            TwoColumnRow("Data wystawienia", fields[2], "Data sprzedaży", fields[3])
            InfoRow("Miejsce wystawienia", fields[4])
        }
    }
}