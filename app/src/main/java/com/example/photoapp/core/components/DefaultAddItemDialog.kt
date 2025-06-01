package com.example.photoapp.core.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.photoapp.core.database.data.entities.Kategoria

/**
 * A generic, reusable AlertDialog for adding an item of type T via text-based fields.
 *
 * This dialog renders a vertical list of `OutlinedTextField`s based on the provided `fields`,
 * and uses `onBuildItem` to construct a T instance to pass into `onAdd`.
 *
 * Use this in combination with a `renderAddItemDialog` lambda in your GenericEditableDetailsScreen
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
 * @param title The title text for the dialog
 * @param fields A list of label-state pairs representing input fields
 * @param onBuildItem Lambda that constructs the item of type T using current input values
 * @param onAdd Callback invoked with the newly built item when "Add" is pressed
 * @param onDismiss Callback invoked when the dialog is dismissed without action
 */

@Composable
fun <T> DefaultAddItemDialog(
    title: String,
    fields: List<Pair<String, MutableState<String>>>,
    kategorie: List<Kategoria>? = null,
    onBuildItem: () -> T,
    onAction: (T) -> Unit,
    onDismiss: () -> Unit
) {
    val kategorieList: List<String> = kategorie!!.map { it.nazwa }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                fields.forEach { (label, state) ->
                    if (label == "Kategoria") {
                        ExposedDropdownMenu(
                            options = kategorieList,
                            selected = { state.value = it },
                        )
                    } else {
                        OutlinedTextField(
                            value = state.value.toString(),
                            onValueChange = { state.value = it },
                            label = { Text(label) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val item = onBuildItem()
                onAction(item)
            }) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
