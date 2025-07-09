package com.example.photoapp.features.faktura.composables.product

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.photoapp.features.faktura.composables.common.CustomTextField
import com.example.photoapp.features.faktura.composables.common.CustomTextFieldWithButton

@Composable
fun InvoiceForm(
    modifier: Modifier,
    fields: List<Pair<String, MutableState<String>>>
) {

    var example: MutableState<String> = remember { mutableStateOf("") }
    val ROW_HEIGHT = 64.dp

    Column(
        modifier = modifier.padding(4.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(ROW_HEIGHT),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            CustomTextField(
                title = "Typ",
                field = fields[0].second,
                modifier = Modifier.weight(1f)
                    .fillMaxHeight()
            )

            CustomTextField(
                title = "Numer",
                field = fields[1].second,
                modifier = Modifier.weight(1f)
                    .fillMaxHeight()
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(ROW_HEIGHT),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            CustomTextField(
                title = "Data wystawienia",
                field = fields[2].second,
                modifier = Modifier.weight(1f)
                    .fillMaxHeight()
            )

            CustomTextField(
                title = "Data sprzedaży",
                field = fields[3].second,
                modifier = Modifier.weight(1f)
                    .fillMaxHeight()
            )
        }

        CustomTextField(
            title = "Miejsce wystawienia",
            field = fields[4].second,
            modifier = Modifier.fillMaxWidth()
        )
    }

}