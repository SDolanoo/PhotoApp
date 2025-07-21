package com.example.photoapp.features.faktura.composables.readOnly

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.photoapp.features.faktura.composables.common.InfoRow
import com.example.photoapp.features.faktura.composables.common.SectionCard
import com.example.photoapp.features.faktura.composables.common.TwoColumnRow

@Composable
fun NabywcaReadOnly(
    modifier: Modifier,
    fields: List<String>
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