package com.example.photoapp.ui.FilterScreen

import android.annotation.SuppressLint
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
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
import java.util.Calendar
import java.util.Date

@SuppressLint("NewApi")
@Composable
fun DateFilter(
    filterController: FilterController,
    onDateRangeSelected: (Date?, Date?) -> Unit
) {
    val today = Date()

    var selectedOption by remember { mutableStateOf("") }
    var customDateFrom by remember { mutableStateOf<Long?>(null) }
    var customDateTo by remember { mutableStateOf<Long?>(null) }

    var showDatePicker by remember { mutableStateOf(false) }

    if (customDateFrom != null && customDateTo != null) {
        val x: String = filterController.convertMillisToString(customDateFrom!!)
        val y: String = filterController.convertMillisToString(customDateTo!!)
        val (from, to) = filterController.convertStringToDate(Pair(x, y))
        onDateRangeSelected(from, to)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (filterController.currentFilter.value == "faktura") {
            SegmentedButtonsDateFilter { value -> //dataWystawienia or dataSprzedazy
                filterController.setCurrentFakturyDateFilter(value)
            }
        }
        Text(text = "Data zakupu")

        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = selectedOption == "week",
                onClick = {
                    selectedOption = "week"
                    val calendar = Calendar.getInstance()
                    calendar.time = today
                    calendar.add(Calendar.DAY_OF_YEAR, -7)
                    calendar.set(Calendar.HOUR_OF_DAY, 0)
                    calendar.set(Calendar.MINUTE, 0)
                    calendar.set(Calendar.SECOND, 0)
                    onDateRangeSelected(calendar.time, today)
                    customDateFrom = null
                    customDateTo = null
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Ostatni tydzień")
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = selectedOption == "month",
                onClick = {
                    selectedOption = "month"
                    val calendar = Calendar.getInstance()
                    calendar.time = today
                    calendar.set(Calendar.DAY_OF_MONTH, 1)
                    calendar.set(Calendar.HOUR_OF_DAY, 0)
                    calendar.set(Calendar.MINUTE, 0)
                    calendar.set(Calendar.SECOND, 0)
                    onDateRangeSelected(calendar.time, today)
                    customDateFrom = null
                    customDateTo = null
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Ostatni miesiąc")
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = selectedOption == "year",
                onClick = {
                    selectedOption = "year"
                    val calendar = Calendar.getInstance()
                    calendar.time = today
                    calendar.set(Calendar.DAY_OF_YEAR, 1)
                    calendar.set(Calendar.HOUR_OF_DAY, 0)
                    calendar.set(Calendar.MINUTE, 0)
                    calendar.set(Calendar.SECOND, 0)
                    onDateRangeSelected(calendar.time, today)
                    customDateFrom = null
                    customDateTo = null
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Ostatni rok")
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = customDateFrom?.let  { filterController.convertMillisToString(it) } ?: "",
                onValueChange = { },
                label = { Text("From") },
                trailingIcon = {
                    Icon(imageVector = Icons.Default.DateRange, contentDescription = "Pick Start Date")
                },
                modifier = Modifier
                    .weight(1f)
                    .pointerInput(customDateTo) {
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

            Spacer(modifier = Modifier.width(16.dp))

            OutlinedTextField(
                value = customDateTo?.let  { filterController.convertMillisToString(it) } ?: "",
                onValueChange = { },
                label = { Text("To") },
                trailingIcon = {
                    IconButton(onClick = { /* Show Date Picker */ }) {
                        Icon(imageVector = Icons.Default.DateRange, contentDescription = "Pick End Date")
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .pointerInput(customDateTo) {
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
                        customDateFrom = it
                    } else {
                        customDateTo = it
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
fun SegmentedButtonsDateFilter(onChoice: (String) -> Unit){
    var selectedIndex by remember { mutableStateOf(0) }
    val options = listOf("Data wystawien", "Data sprzedazy")

    SingleChoiceSegmentedButtonRow {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size
                ),
                onClick = {
                    selectedIndex = index
                    val choice = options[index]
                    onChoice(choice)
                },
                selected = index == selectedIndex,
                label = { Text(label) }
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