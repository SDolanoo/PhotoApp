package com.example.photoapp.features.faktura.composables.forms

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
import com.example.photoapp.features.faktura.composables.common.CustomOutlinedButton
import com.example.photoapp.features.faktura.composables.common.CustomTextField
import com.example.photoapp.features.faktura.composables.common.CustomTextFieldWithButton
import com.example.photoapp.R

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
                onEdit = { onEdit() }
            )

            CustomTextField(
                title = "Jednostka",
                field = fields[2].second,
                modifier = Modifier.weight(1f)
                    .fillMaxHeight(),
                onEdit = { onEdit() }
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
                onEdit = { onEdit() }
            )

            CustomTextField(
                title = "Vat %",
                field = fields[4].second,
                modifier = Modifier.weight(1f)
                    .fillMaxHeight(),
                onEdit = { onEdit() }
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
                onEdit = { onEdit() }
            )

            CustomTextField(
                title = "Wartość brutto",
                field = fields[6].second,
                modifier = Modifier.weight(1f)
                    .fillMaxHeight(),
                onEdit = { onEdit() }
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
                    onEdit = { onEdit() }
                )

                CustomTextField(
                    title = "PKWiU",
                    field = fields[8].second,
                    modifier = Modifier.weight(1f)
                        .fillMaxHeight(),
                    onEdit = { onEdit() }
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