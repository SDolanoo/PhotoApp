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
import com.example.photoapp.core.utils.calculateGrossValue
import com.example.photoapp.core.utils.calculateNetPrice
import com.example.photoapp.core.utils.calculateNetValue
import com.example.photoapp.core.utils.calculateNetValueQuantity
import com.google.android.play.integrity.internal.q

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

    val name = fields[0].second
    val quantity = fields[1].second
    val unit = fields[2].second
    val netPrice = fields[3].second
    val vat = fields[4].second
    val netValue = fields[5].second
    val grossValue = fields[6].second
    val discount = fields[7].second

    Column(
        modifier = modifier.padding(4.dp)
    ) {

        CustomTextFieldWithButton(
            title = "Nazwa",
            field = name,
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
                field = quantity,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                onEdit = {
                    netValue.value = calculateNetValueQuantity(quantity.value, netPrice.value)
                    grossValue.value = calculateGrossValue(netValue.value, vat.value) ?: netValue.value
                    onEdit()
                },
                keyboardType = KeyboardType.NUMERIC
            )

            CustomDropdownMenu(
                options = listOf("szt", "godz", "dni", "mc", "kg", "m2", "więcej.."),
                label = "Jednostka",
                field = unit,
                selected = { onEdit() },
                modifier = Modifier
                    .weight(1f)
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
                field = netPrice,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                onEdit = {
                    netValue.value = calculateNetValueQuantity(quantity.value, netPrice.value)
                    grossValue.value = calculateGrossValue(netValue.value, vat.value) ?: netValue.value
                    onEdit()
                },
                keyboardType = KeyboardType.NUMERIC
            )

            CustomDropdownMenu(
                options = listOf("23", "8", "7", "5", "0", "zw", "np", "więcej.."),
                label = "Vat %",
                field = vat,
                selected = {
                    netValue.value = calculateNetValueQuantity(quantity.value, netPrice.value)
                    grossValue.value = calculateGrossValue(netValue.value, vat.value) ?: netValue.value
                    onEdit()
                },
                modifier = Modifier
                    .weight(1f)
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
                field = netValue,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                onEdit = {
                    grossValue.value = calculateGrossValue(netValue.value, vat.value) ?: netValue.value
                    netPrice.value = calculateNetPrice(netValue.value, quantity.value)
                    onEdit()
                },
                keyboardType = KeyboardType.NUMERIC
            )

            CustomTextField(
                title = "Wartość brutto",
                field = grossValue,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                onEdit = {
                    netValue.value = calculateNetValue(grossValue.value, vat.value) ?: calculateNetValueQuantity(netPrice.value, quantity.value)
                    netPrice.value = calculateNetPrice(netValue.value, quantity.value)
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
                    field = discount,
                    modifier = Modifier
                        .weight(1f)
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
            HorizontalDivider(thickness = 1.dp, modifier=Modifier
                .width(120.dp)
                .padding(horizontal = 4.dp)
                .padding(top = 14.dp))

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

