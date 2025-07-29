package com.example.photoapp.features.odbiorca.composables.readOnly

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.photoapp.core.components.common.InfoRow
import com.example.photoapp.core.components.common.SectionCard
import com.example.photoapp.core.components.common.TwoColumnRow

@Composable
fun OdbiorcaReadOnly(
    modifier: Modifier,
    fields: List<String>
) {
    SectionCard(
        title = "Odbiorca",
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