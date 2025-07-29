package com.example.photoapp.core.components.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction

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
    options: List<String>,
    label: String,
    field: MutableState<String>,
    modifier: Modifier = Modifier,
    selected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var isCustomInput by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val focusState = remember { mutableStateOf(false) }

    // Watch for focus loss to revert back to dropdown mode
    LaunchedEffect(focusState.value) {
        if (!focusState.value && isCustomInput) {
            isCustomInput = false
        }
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            if (!isCustomInput) {
                expanded = !expanded
            }
        },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = field.value,
            onValueChange = {
                field.value = it
                selected(it)
            },
            readOnly = !isCustomInput,
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .onFocusChanged {
                    focusState.value = it.isFocused
                }
                .focusRequester(focusRequester),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus() // triggers focusState change
                }
            )
        )

        // Show dropdown only if not in custom input mode
        if (!isCustomInput) {
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            expanded = false
                            if (selectionOption == "więcej..") {
                                field.value = ""
                                isCustomInput = true
                                focusRequester.requestFocus()
                            } else {
                                field.value = selectionOption
                                selected(selectionOption)
                            }
                        }
                    )
                }
            }
        }
    }
}
