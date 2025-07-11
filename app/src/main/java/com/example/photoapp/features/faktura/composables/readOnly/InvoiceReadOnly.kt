package com.example.photoapp.features.faktura.composables.readOnly

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.photoapp.features.faktura.composables.common.CustomText

@Composable
fun InvoiceReadOnly(
    modifier: Modifier,
    fields: List<String>
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
            CustomText(
                title = "Typ",
                field = fields[0],
                modifier = Modifier.weight(1f)
                    .fillMaxHeight()
            )

            CustomText(
                title = "Numer",
                field = fields[1],
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
            CustomText(
                title = "Data wystawienia",
                field = fields[2],
                modifier = Modifier.weight(1f)
                    .fillMaxHeight()
            )

            CustomText(
                title = "Data sprzedaży",
                field = fields[3],
                modifier = Modifier.weight(1f)
                    .fillMaxHeight()
            )
        }

        CustomText(
            title = "Miejsce wystawienia",
            field = fields[4],
            modifier = Modifier.fillMaxWidth()
        )
    }

}