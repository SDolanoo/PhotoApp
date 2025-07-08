package com.example.photoapp.features.faktura.composables.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier

/**
 * A reusable single-line text input field with a label.
 *
 * This composable wraps a standard `OutlinedTextField` for simple, editable text input.
 * The field is fully controlled via an external `MutableState<String>`, making it easy
 * to integrate with forms, dialog inputs, or other UI states.
 *
 * This component is useful as a building block for forms and dynamic user inputs.
 *
 * Example usage:
 * ```
 * val username = remember { mutableStateOf("") }
 *
 * CustomTextField(
 *     title = "Nazwa użytkownika",
 *     field = username
 * )
 * ```
 *
 * @param title The label text to display inside the input field.
 * @param field A `MutableState` representing the value of the text input.
 */

@Composable
fun CustomTextField(
    title: String,
    field: MutableState<String>,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = field.value.toString(),
        onValueChange = { field.value = it },
        label = { Text(title) },
        modifier = modifier,
        singleLine = true
    )
}