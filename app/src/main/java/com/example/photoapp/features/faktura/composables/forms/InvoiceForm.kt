package com.example.photoapp.features.faktura.composables.forms

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
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.photoapp.core.components.common.CustomTextField

@Composable
fun InvoiceForm(
    modifier: Modifier,
    fields: List<Pair<String, MutableState<String>>>,
    onEdit: () -> Unit,
    showDatePickerWystawienia: () -> Unit,
    showDatePickerSprzedazy: () -> Unit
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
                title = "Typ",
                field = fields[0].second,
                modifier = Modifier.weight(1f)
                    .fillMaxHeight(),
                onEdit = { onEdit() }
            )

            CustomTextField(
                title = "Numer",
                field = fields[1].second,
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
                title = "Data wystawienia",
                field = fields[2].second,
                modifier = Modifier.weight(1f)
                    .fillMaxHeight()
                    .pointerInput(Unit) {
                        awaitEachGesture {
                            // Modifier.clickable doesn't work for text fields, so we use Modifier.pointerInput
                            // in the Initial pass to observe events before the text field consumes them
                            // in the Main pass.
                            awaitFirstDown(pass = PointerEventPass.Initial)
                            val upEvent =
                                waitForUpOrCancellation(pass = PointerEventPass.Initial)
                            if (upEvent != null) {
                                showDatePickerWystawienia()
                            }
                        }
                    },
                onEdit = { onEdit() }
            )

            CustomTextField(
                title = "Data sprzedaży",
                field = fields[3].second,
                modifier = Modifier.weight(1f)
                    .fillMaxHeight()
                    .pointerInput(Unit) {
                        awaitEachGesture {
                            // Modifier.clickable doesn't work for text fields, so we use Modifier.pointerInput
                            // in the Initial pass to observe events before the text field consumes them
                            // in the Main pass.
                            awaitFirstDown(pass = PointerEventPass.Initial)
                            val upEvent =
                                waitForUpOrCancellation(pass = PointerEventPass.Initial)
                            if (upEvent != null) {
                                showDatePickerSprzedazy()
                            }
                        }
                    },
                onEdit = { onEdit() }
            )
        }

        CustomTextField(
            title = "Miejsce wystawienia",
            field = fields[4].second,
            modifier = Modifier.fillMaxWidth(),
            onEdit = { onEdit() }
        )
    }

}