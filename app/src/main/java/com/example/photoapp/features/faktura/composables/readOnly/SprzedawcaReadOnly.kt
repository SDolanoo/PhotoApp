package com.example.photoapp.features.faktura.composables.readOnly

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.photoapp.features.faktura.composables.common.InfoRow
import com.example.photoapp.features.faktura.composables.common.SectionCard
import com.example.photoapp.features.faktura.composables.common.TwoColumnRow

@Composable
fun SprzedawcaReadOnly(
    modifier: Modifier,
    fields: List<String>
) {
    Column(
        modifier = modifier.padding(4.dp)
    ) {
        SectionCard(
            title = "Nabywca",
            icon = Icons.Filled.Person
        ) {
            InfoRow("Nazwa firmy", fields[0])
            InfoRow("NIP", fields[1])
            InfoRow("Ulica i nr mieszkania", fields[2])
            TwoColumnRow("Kod pocztowy", fields[3], "Miejscowość", fields[4])
            InfoRow("Kraj", fields[5])
            TwoColumnRow("E-mail", fields[7], "Telefon", fields[8])
            InfoRow("Opis", fields[6])
        }
    }
}