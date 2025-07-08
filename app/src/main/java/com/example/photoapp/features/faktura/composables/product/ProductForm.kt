package com.example.photoapp.features.faktura.composables.product

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.photoapp.features.faktura.composables.common.CustomOutlinedButton
import com.example.photoapp.features.faktura.composables.common.CustomTextField
import com.example.photoapp.features.faktura.composables.common.CustomTextFieldWithButton
import com.example.photoapp.R

@Composable
fun ProductForm(modifier: Modifier) {

    var example: MutableState<String> = remember { mutableStateOf("") }
    val ROW_HEIGHT = 64.dp

    var state by remember { mutableStateOf("less") } // or more

    Column(
        modifier = modifier.padding(4.dp)
    ) {

        CustomTextFieldWithButton(
            title = "Nazwa",
            field = example
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(ROW_HEIGHT),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            CustomTextField(
                title = "Ilość",
                field = example,
                modifier = Modifier.weight(1f)
                    .fillMaxHeight()
            )

            CustomTextField(
                title = "Jednostka",
                field = example,
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
                title = "Cena netto",
                field = example,
                modifier = Modifier.weight(1f)
                    .fillMaxHeight()
            )

            CustomTextField(
                title = "Vat %",
                field = example,
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
                title = "Wartość netto",
                field = example,
                modifier = Modifier.weight(1f)
                    .fillMaxHeight()
            )

            CustomTextField(
                title = "Wartość brutto",
                field = example,
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
                CustomTextField(
                    title = "Rabat %",
                    field = example,
                    modifier = Modifier.weight(1f)
                        .fillMaxHeight()
                )

                CustomTextField(
                    title = "PKWiU",
                    field = example,
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
            HorizontalDivider(thickness = 1.dp, modifier=Modifier.width(120.dp ).padding(horizontal = 4.dp).padding(top = 14.dp))

            CustomOutlinedButton(
                title = "usuń pozycję",
                onClick = { /* usuwam z fakturki*/ },
                icon = painterResource(R.drawable.baseline_delete_outline_24),
                textColor = Color.Red,
                outlineColor = Color.Red,
                height = 28,
                modifier = Modifier.weight(1f)
            )
        }

    }
}