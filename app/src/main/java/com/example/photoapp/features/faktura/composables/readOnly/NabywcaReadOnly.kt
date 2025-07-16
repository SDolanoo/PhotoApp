package com.example.photoapp.features.faktura.composables.readOnly

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.photoapp.R
import com.example.photoapp.features.faktura.composables.common.CustomOutlinedButton
import com.example.photoapp.features.faktura.composables.common.CustomText

@Composable
fun NabywcaReadOnly(
    modifier: Modifier,
    fields: List<String>
) {
    val ROW_HEIGHT = 64.dp

    var state by remember { mutableStateOf("less") } // or more

    Column(
        modifier = modifier.padding(4.dp)
    ) {

        CustomText(
            title = "Nazwa firmy",
            field = fields[0],
            modifier = Modifier.fillMaxWidth()
        )

        CustomText(
            title = "NIP",
            field = fields[1],
            modifier = Modifier.fillMaxWidth()
        )

        CustomText(
            title = "Ulica i nr mieszkania",
            field = fields[2],
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(ROW_HEIGHT),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            CustomText(
                title = "Kod pocztowy",
                field = fields[3],
                modifier = Modifier.weight(0.4f)
                    .fillMaxHeight()
            )

            CustomText(
                title = "Miejscowość",
                field = fields[4],
                modifier = Modifier.weight(0.6f)
                    .fillMaxHeight()
            )
        }

        if (state == "more") {
            CustomText(
                title = "Kraj",
                field = fields[5],
                modifier = Modifier.fillMaxWidth()
            )

            CustomText(
                title = "Opis",
                field = fields[6],
                modifier = Modifier.fillMaxWidth()
            )

            CustomText(
                title = "E-mail",
                field = fields[7],
                modifier = Modifier.fillMaxWidth()
            )

            CustomText(
                title = "Telefon",
                field = fields[8],
                modifier = Modifier.fillMaxWidth()
            )
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
        }

    }
}