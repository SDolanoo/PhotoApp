package com.example.photoapp.features.produkt.composables.readOnly

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.photoapp.core.components.common.SectionCard
import com.example.photoapp.core.components.common.TwoColumnRow

@Composable
fun OneProduktReadOnly(
    modifier: Modifier,
    fields: List<String>
) {
    Column(
        modifier = modifier.padding(4.dp)
    ) {
        SectionCard(
            title = "Produkt",
            icon = Icons.Filled.Person
        ) {
            TwoColumnRow("Nazwa", fields[0], "Jednostka", fields[1])
            TwoColumnRow("Cena netto", fields[2], "Vat %", fields[3])
        }
    }
}