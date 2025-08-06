package com.example.photoapp.features.produkt.composables.form

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.dolan.photoapp.R
import com.example.photoapp.core.components.common.CustomOutlinedButton
import com.example.photoapp.core.components.common.CustomTextField
import com.example.photoapp.core.components.common.CustomTextFieldWithButton
import com.example.photoapp.core.components.common.CustomDropdownMenu
import com.example.photoapp.core.components.common.KeyboardType

@Composable
fun ProductForm(
    modifier: Modifier,
    fields: List<Pair<String, MutableState<String>>>,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onButtonClick: () -> Unit
) {

    val ROW_HEIGHT = 64.dp

    var state by remember { mutableStateOf("less") } // or more

    Column(
        modifier = modifier.padding(4.dp)
    ) {

        CustomTextFieldWithButton(
            title = "Nazwa",
            field = fields[0].second,
            onEdit = { onEdit() },
            onButtonClick = { onButtonClick() }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(ROW_HEIGHT),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            CustomTextField(
                title = "Ilość",
                field = fields[1].second,
                modifier = Modifier.weight(1f)
                    .fillMaxHeight(),
                onEdit = { onEdit() },
                keyboardType = KeyboardType.NUMERIC
            )

            CustomDropdownMenu(
                options = listOf("szt", "godz", "dni", "mc", "kg", "m2", "więcej.."),
                label = "Jednostka",
                field = fields[2].second,
                selected = { onEdit() },
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
                field = fields[3].second,
                modifier = Modifier.weight(1f)
                    .fillMaxHeight(),
                onEdit = {
                    val calculatedPrice = calculateBrutto(fields[3].second.value, fields[4].second.value)
                    fields[5].second.value = fields[3].second.value
                    fields[6].second.value = calculatedPrice
                    onEdit()
                },
                keyboardType = KeyboardType.NUMERIC
            )

            CustomDropdownMenu(
                options = listOf("23", "8", "7", "5", "0", "zw", "np", "więcej.."),
                label = "Vat %",
                field = fields[4].second,
                selected = {
                    val priceNetto = calculateNetto(fields[3].second.value, fields[4].second.value)
                    val priceBrutto = calculateBrutto(fields[6].second.value, fields[4].second.value)
                    fields[3].second.value = priceNetto
                    fields[5].second.value = priceNetto
                    fields[6].second.value = priceBrutto
                    onEdit()
                },
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
                field = fields[5].second,
                modifier = Modifier.weight(1f)
                    .fillMaxHeight(),
                onEdit = {
                    val calculatedPrice = calculateBrutto(fields[5].second.value, fields[4].second.value)
                    fields[3].second.value = fields[5].second.value
                    fields[6].second.value = calculatedPrice
                    onEdit()
                },
                keyboardType = KeyboardType.NUMERIC
            )

            CustomTextField(
                title = "Wartość brutto",
                field = fields[6].second,
                modifier = Modifier.weight(1f)
                    .fillMaxHeight(),
                onEdit = {
                    val calculatedPrice = calculateNetto(fields[6].second.value, fields[4].second.value)
                    fields[3].second.value = calculatedPrice
                    fields[5].second.value = calculatedPrice
                    onEdit()
                },
                keyboardType = KeyboardType.NUMERIC
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
                    field = fields[7].second,
                    modifier = Modifier.weight(1f)
                        .fillMaxHeight(),
                    onEdit = { onEdit() },
                    keyboardType = KeyboardType.NUMERIC
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
                onClick = { onDelete() },
                icon = painterResource(R.drawable.baseline_delete_outline_24),
                textColor = Color.Red,
                outlineColor = Color.Red,
                height = 28,
                modifier = Modifier.weight(1f)
            )
        }

    }
}

@SuppressLint("DefaultLocale")
private fun calculateNetto(price: String, vat: String): String {
    val vatRate = vat.toIntOrNull()
    val grossPrice = price.toDoubleOrNull()

    if (vatRate == null || grossPrice == null) return "0"

    val netPrice = grossPrice / (1 + vatRate / 100.0)
    return String.format("%.2f", netPrice).replace('.', ',')
}

@SuppressLint("DefaultLocale")
private fun calculateBrutto(price: String, vat: String): String {
    val vatRate = vat.toIntOrNull()
    val netPrice = price.toDoubleOrNull()

    if (vatRate == null || netPrice == null) return "0"

    val grossPrice = netPrice * (1 + vatRate / 100.0)
    return String.format("%.2f", grossPrice).replace('.', ',')
}