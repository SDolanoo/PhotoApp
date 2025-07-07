package com.example.photoapp.features.faktura.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * A custom composable combining an `OutlinedTextField` with a trailing button.
 *
 * This component displays a single-line text input field alongside a button inside the
 * `trailingIcon` slot. The field value is controlled by an external `MutableState`, and
 * the button currently uses the same title as the field label.
 *
 * You can customize this component to perform actions like submitting the field value,
 * triggering validation, or launching additional UI flows.
 *
 * Example usage:
 * ```
 * val email = remember { mutableStateOf("") }
 *
 * CustomTextFieldWithButton(
 *     title = "Wyślij",
 *     field = email
 * )
 * ```
 *
 * @param title The label for the input field and the text for the trailing button.
 * @param field A `MutableState` holding the current value of the input field.
 */

@Composable
fun CustomTextFieldWithButton(
    title: String,
    field: MutableState<String>,
) {
    Column() {
        OutlinedTextField(
            value = field.value.toString(),
            onValueChange = { field.value = it },
            label = { Text(title) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            trailingIcon = {
                Button(
                    onClick = { /* Twoja akcja */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Blue,
                        contentColor = Color.White
                    ),
                ) {
                    Text(title)
                }
            },
        )
    }
}