package com.example.photoapp.features.faktura.composables.readOnly

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.photoapp.features.faktura.composables.common.CustomOutlinedButton
import com.example.photoapp.R
import com.example.photoapp.features.faktura.composables.common.CustomText

@Composable
fun ProductReadOnly(
    modifier: Modifier,
    fields: List<String>
) {
    val ROW_HEIGHT = 64.dp

    var state by remember { mutableStateOf("less") } // or more

    Column(
        modifier = modifier.padding(4.dp)
    ) {

        CustomText(
            title = "Nazwa",
            field = fields[0]
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(ROW_HEIGHT),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            CustomText(
                title = "Ilość",
                field = fields[1],
                modifier = Modifier.weight(1f)
                    .fillMaxHeight()
            )

            CustomText(
                title = "Jednostka",
                field = fields[2],
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
                title = "Cena netto",
                field = fields[3],
                modifier = Modifier.weight(1f)
                    .fillMaxHeight()
            )

            CustomText(
                title = "Vat %",
                field = fields[4],
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
                title = "Wartość netto",
                field = fields[5],
                modifier = Modifier.weight(1f)
                    .fillMaxHeight()
            )

            CustomText(
                title = "Wartość brutto",
                field = fields[6],
                modifier = Modifier.weight(1f)
                    .fillMaxHeight()
            )
        }

        if (state == "more") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ROW_HEIGHT),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                CustomText(
                    title = "Rabat %",
                    field = fields[7],
                    modifier = Modifier.weight(1f)
                        .fillMaxHeight()
                )

                CustomText(
                    title = "PKWiU",
                    field = fields[8],
                    modifier = Modifier.weight(1f)
                        .fillMaxHeight()
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            //horizontalArrangement = Arrangement.spacedBy(120.dp)
        ) {
            CustomOutlinedButton(
                title = if (state == "less") "więcej opcji" else "mniej opcji",
                onClick = { if (state == "less") state = "more" else state = "less" },
                icon = if (state == "less") painterResource(R.drawable.baseline_expand_more_24) else painterResource(R.drawable.baseline_expand_less_24),
                height = 28,
                modifier = Modifier.weight(1f)
            )
        }

    }
}