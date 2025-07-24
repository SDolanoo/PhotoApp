package com.example.photoapp.ui.FilterScreen

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.photoapp.core.utils.convertMillisToString

@Composable
fun DateRangeSection(state: FilterState, onUpdate: (FilterState) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    var selectedOption by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }

    Column(Modifier.padding(vertical = 8.dp)) {
        Text("Date Range")

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Pricing Type")
            Row {
                Text("Net")
                Switch(
                    checked = state.isGross,
                    onCheckedChange = { onUpdate(state.copy(isGross = it)) }
                )
                Text("Gross")
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = state.fromDate,
                onValueChange = { },
                label = { Text("Od") },
                trailingIcon = {
                    Icon(imageVector = Icons.Default.DateRange, contentDescription = "Pick Start Date")
                },
                modifier = Modifier
                    .weight(1f)
                    .pointerInput(state.fromDate) {
                        awaitEachGesture {
                            // Modifier.clickable doesn't work for text fields, so we use Modifier.pointerInput
                            // in the Initial pass to observe events before the text field consumes them
                            // in the Main pass.
                            awaitFirstDown(pass = PointerEventPass.Initial)
                            val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                            if (upEvent != null) {
                                selectedOption = "From"
                                showDatePicker = true
                            }
                        }
                    }
            )
            OutlinedTextField(
                value = state.toDate,
                onValueChange = { },
                label = { Text("Do") },
                trailingIcon = {
                    IconButton(onClick = { /* Show Date Picker */ }) {
                        Icon(imageVector = Icons.Default.DateRange, contentDescription = "Pick End Date")
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .pointerInput(state.toDate) {
                        awaitEachGesture {
                            // Modifier.clickable doesn't work for text fields, so we use Modifier.pointerInput
                            // in the Initial pass to observe events before the text field consumes them
                            // in the Main pass.
                            awaitFirstDown(pass = PointerEventPass.Initial)
                            val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                            if (upEvent != null) {
                                selectedOption = "To"
                                showDatePicker = true
                            }
                        }
                    }
            )
        }

        if (showDatePicker) {
            DatePickerModalInput(
                onDateSelected = {
                    if (selectedOption == "From") {
                        onUpdate(state.copy(fromDate = it?. let { convertMillisToString(it) } ?: state.fromDate))
                    } else {
                        onUpdate(state.copy(fromDate = it?. let { convertMillisToString(it) } ?: state.toDate))
                    }
                    selectedOption = ""
                },
                onDismiss = { showDatePicker = false }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModalInput(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(initialDisplayMode = DisplayMode.Input)

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}