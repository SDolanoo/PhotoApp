package com.example.photoapp.features.faktura.composables.product

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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

    Column(
        modifier = modifier
    ) {

        CustomTextFieldWithButton(
            title = "Nazwa",
            field = example
        )

        Row {
            CustomTextField(
                title = "Ilość",
                field = example
            )

            CustomTextField(
                title = "Jednostka",
                field = example
            )
        }

        Row {
            CustomTextField(
                title = "Cena netto",
                field = example
            )

            CustomTextField(
                title = "Vat%",
                field = example
            )
        }

        Row {
            CustomTextField(
                title = "Wartość netto",
                field = example
            )

            CustomTextField(
                title = "Wartość brutto",
                field = example
            )
        }

        Row {
            CustomTextField(
                title = "Rabat %",
                field = example
            )

            CustomTextField(
                title = "PKWiU",
                field = example
            )
        }

        Row {
            CustomOutlinedButton(
                title = "więcej opcji",
                onClick = { /* rozsuwam pozycje */ },
                icon = painterResource(R.drawable.baseline_expand_less_24), // painterResource(R.drawable.baseline_expand_more_24)
                height = 26.dp
            )

            CustomOutlinedButton(
                title = "usuń pozycję",
                onClick = { /* usuwam z fakturki*/ },
                icon = painterResource(R.drawable.baseline_delete_outline_24),
                textColor = Color.Red,
                outlineColor = Color.Red,
                height = 26.dp
            )
        }
    }
}