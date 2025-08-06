package com.example.photoapp.features.produkt.composables.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.photoapp.core.components.common.CustomDropdownMenu
import com.example.photoapp.core.components.common.CustomTextField
import com.example.photoapp.core.components.common.KeyboardType

@Composable
fun OneProduktForm(
    modifier: Modifier,
    fields: List<Pair<String, MutableState<String>>>,
    onEdit: () -> Unit,
    onButtonClick: () -> Unit,
) {
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
                title = "Nazwa",
                field = fields[0].second,
                modifier = Modifier.weight(0.5f)
                    .fillMaxHeight(),
                onEdit = { onEdit() },
                keyboardType = KeyboardType.NUMERIC
            )

            CustomDropdownMenu(
                options = listOf("szt", "godz", "dni", "mc", "kg", "m2", "więcej.."),
                label = "Jednostka",
                field = fields[1].second,
                selected = { onEdit() },
                modifier = Modifier.weight(0.5f)
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
                title = "Cena netto",
                field = fields[2].second,
                modifier = Modifier.weight(0.5f)
                    .fillMaxHeight(),
                onEdit = { onEdit() },
                keyboardType = KeyboardType.NUMERIC
            )

            CustomDropdownMenu(
                options = listOf("23", "8", "7", "5", "0", "zw", "np", "więcej.."),
                label = "Vat %",
                field = fields[3].second,
                selected = {
                    onEdit()
                },
                modifier = Modifier.weight(0.5f)
                    .fillMaxHeight()
            )
        }
    }
}
