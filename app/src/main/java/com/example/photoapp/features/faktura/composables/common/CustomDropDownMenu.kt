package com.example.photoapp.features.faktura.composables.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

/**
 * A reusable exposed dropdown menu component for selecting a string from a predefined list.
 *
 * This composable displays an `OutlinedTextField` with an attached dropdown menu using
 * Material 3’s `ExposedDropdownMenuBox`. It manages its own internal selection and expansion
 * state, and exposes the selected value through a callback.
 *
 * This is useful when you want to let users choose from a list of options while maintaining
 * consistent Material Design behavior and styling.
 *
 * Example usage:
 * ```
 * val colors = listOf("Czerwony", "Zielony", "Niebieski")
 * CustomDropdownMenu(
 *     options = colors,
 *     label = "Wybierz kolor",
 *     selected = { chosenColor ->
 *         println("Wybrano: $chosenColor")
 *     }
 * )
 * ```
 *
 * @param options The list of string values to display as selectable options.
 * @param label The label text to show inside the `OutlinedTextField`.
 * @param selected Callback invoked when the user selects an option from the dropdown.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDropdownMenu(
    options:List<String>,
    label: String,
    selected: (String) -> Unit
) {
    // State management
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf("") }

    // Material Design dropdown wrapper
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedOptionText,
            onValueChange = { },
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        // The dropdown menu
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        selectedOptionText = selectionOption
                        selected(selectionOption)
                        expanded = false
                    }
                )
            }
        }
    }
}
