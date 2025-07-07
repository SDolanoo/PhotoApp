package com.example.photoapp.features.faktura.composables

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

/**
 * A generic, reusable AlertDialog for adding an item of type T via text-based fields.
 *
 * This dialog renders a vertical list of `OutlinedTextField`s based on the provided `fields`,
 * and uses `onBuildItem` to construct a T instance to pass into `onAdd`.
 *
 * Use this in combination with a `renderAddItemDialog` lambda in your `GenericEditableDetailsScreen`
 * to enable flexible item addition workflows.
 *
 * Example usage:
 * ```
 * renderAddItemDialog = { onAdd, onDismiss ->
 *     val name = remember { mutableStateOf("") }
 *     val quantity = remember { mutableStateOf("") }
 *
 *     DefaultAddItemDialog(
 *         title = "Add Product",
 *         fields = listOf("Name" to name, "Quantity" to quantity),
 *         onBuildItem = {
 *             Product(name = name.value, quantity = quantity.value.toInt())
 *         },
 *         onAdd = onAdd,
 *         onDismiss = onDismiss
 *     )
 * }
 * ```
 *
 * @param title The title text for the dialog.
 * @param fields A list of pairs where the first value is the label of the input field,
 *               and the second is a mutable state representing the field's current value.
 * @param onBuildItem A lambda that constructs and returns an instance of T based on current field values.
 * @param onAdd A callback invoked with the newly constructed T instance when "Add" is clicked.
 * @param onDismiss A callback invoked when the dialog is dismissed without confirming.
 */

@Composable
fun CustomAlertDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    onCloseRequest: () -> Unit = onDismiss // domyślnie jak Anuluj
) {
    AlertDialog(
        onDismissRequest = onCloseRequest,
        title = { Text(text = title) },
        text = { Text(text = message) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Anuluj")
            }
        }
    )
}
