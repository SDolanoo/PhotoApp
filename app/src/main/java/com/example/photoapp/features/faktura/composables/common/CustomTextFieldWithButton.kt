package com.example.photoapp.features.faktura.composables.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

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
    title: String = "",
    field: MutableState<String>,
    onEdit: () -> Unit,
    onButtonClick: () -> Unit
) {
    // Recommended height for OutlinedTextField is 56.dp
    val textFieldHeight = 56.dp
    val buttonHeight = textFieldHeight - 5.dp

    Column {
        OutlinedTextField(
            value = field.value,
            onValueChange = {
                field.value = it
                onEdit()
                            },
            label = { Text(title) },
            modifier = Modifier
                .fillMaxWidth(),
            singleLine = true,
            trailingIcon = {
                Button(
                    onClick = { onButtonClick() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Blue,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp), // rectangular with slightly rounded corners
                    modifier = Modifier
                        .height(buttonHeight)
                        .defaultMinSize(minWidth = 5.dp) // avoids extra padding from default min sizes
                        .offset(x = -3.dp)
                ) {
                    Text(text = title)
                }
            },
        )
    }
}