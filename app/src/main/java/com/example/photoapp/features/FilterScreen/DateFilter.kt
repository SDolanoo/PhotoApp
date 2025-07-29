package com.example.photoapp.features.FilterScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.photoapp.core.utils.convertMillisToString
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DateRangeSection(state: FilterState, onUpdate: (FilterState) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    var selectedOption by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.LightGray),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        if (expanded) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Zakres Dat", style = MaterialTheme.typography.titleMedium)
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowUp,
                            contentDescription = if (expanded) "Zwiń" else "Rozwiń"
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Typ")
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Wystawienia", modifier = Modifier.align(Alignment.CenterVertically))
                        Switch(
                            checked = state.isWystawienia,
                            onCheckedChange = { onUpdate(state.copy(isWystawienia = it)) }
                        )
                        Text("Sprzedaży", modifier = Modifier.align(Alignment.CenterVertically))
                    }
                }


                Spacer(Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = state.fromDate,
                            onValueChange = {
                                val isValid = isValidDateFormat(it)
                                onUpdate(state.copy(fromDate = it, isFromDateValid = isValid))
                            },
                            label = { Text("Od") },
                            isError = !state.isFromDateValid,
                            trailingIcon = {
                                Icon(imageVector = Icons.Default.DateRange, contentDescription = "Pick Start Date")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .pointerInput(Unit) {
                                    awaitEachGesture {
                                        awaitFirstDown(pass = PointerEventPass.Initial)
                                        val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                                        if (upEvent != null) {
                                            selectedOption = "From"
                                            showDatePicker = true
                                        }
                                    }
                                }
                        )
                        if (!state.isFromDateValid) {
                            Text(
                                "Niepoprawny format. Użyj: rrrr-mm-dd",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = state.toDate,
                            onValueChange = {
                                val isValid = isValidDateFormat(it)
                                onUpdate(state.copy(toDate = it, isToDateValid = isValid))
                            },
                            label = { Text("Do") },
                            isError = !state.isToDateValid,
                            trailingIcon = {
                                Icon(imageVector = Icons.Default.DateRange, contentDescription = "Pick End Date")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .pointerInput(Unit) {
                                    awaitEachGesture {
                                        awaitFirstDown(pass = PointerEventPass.Initial)
                                        val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                                        if (upEvent != null) {
                                            selectedOption = "To"
                                            showDatePicker = true
                                        }
                                    }
                                }
                        )
                        if (!state.isToDateValid) {
                            Text(
                                "Niepoprawny format. Użyj: rrrr-mm-dd",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }

                }
            }
        } else {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Zakres Dat", style = MaterialTheme.typography.titleMedium)
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowDown,
                            contentDescription = if (expanded) "Zwiń" else "Rozwiń"
                        )
                    }
                }
            }
        }


        if (showDatePicker) {
            DatePickerModalInput(
                onDateSelected = {
                    val formatted = it?.let { convertMillisToString(it) } ?: return@DatePickerModalInput
                    if (selectedOption == "From") {
                        onUpdate(state.copy(fromDate = formatted, isFromDateValid = true))
                    } else {
                        onUpdate(state.copy(toDate = formatted, isToDateValid = true))
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

private fun isValidDateFormat(input: String): Boolean {
    return try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        LocalDate.parse(input, formatter)
        true
    } catch (e: Exception) {
        false
    }
}