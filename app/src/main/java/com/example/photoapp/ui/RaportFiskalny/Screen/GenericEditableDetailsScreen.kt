package com.example.photoapp.ui.RaportFiskalny.Screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.photoapp.R
import com.example.photoapp.database.data.RaportFiskalny
import com.example.photoapp.ui.RaportFiskalny.Details.RaportFiskalnyViewModel
import com.example.photoapp.ui.RaportFiskalny.Details.composables.IsEditing.DatePickerModal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> GenericEditableDetailsScreen(
    title: String,
    items: List<T>,
    onAddItem: (String, String) -> Unit,
    onEditItem: (Int, T) -> Unit,
    onDeleteItem: (T) -> Unit,
    onExport: () -> Unit,
    renderItem: @Composable (T, (T) -> Unit) -> Unit,
    renderReadonlyItem: @Composable (T) -> Unit,
    enableDatePicker: Boolean = false,
    initialDate: Long?,
    onDateSelected: (Long) -> Unit,
    renderTopBarActions: @Composable (() -> Unit)? = null,
    viewModel: RaportFiskalnyViewModel
) {
    var isEditing by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    var newPLU by remember { mutableStateOf("") }
    var newQty by remember { mutableStateOf("") }

    var customDate by remember { mutableStateOf(viewModel.formatDate(initialDate)) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = { isEditing = false }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (isEditing) {
                        IconButton(onClick = { showAddDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Add")
                        }
                        IconButton(onClick = {
                            isEditing = false
                        }) {
                            Icon(Icons.Default.Check, contentDescription = "Save")
                        }
                    } else {
                        renderTopBarActions?.invoke()
                        IconButton(onClick = {
                            isEditing = true
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = onExport) {
                            Icon(
                                painter = painterResource(id = R.drawable.upload_file),
                                contentDescription = "Localized description"
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            if (enableDatePicker && initialDate != null) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedTextField(
                            value = customDate,
                            onValueChange = {},
                            label = { Text("Data") },
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Pick Date"
                                )
                            },
                            modifier = Modifier
                                .pointerInput(Unit) {
                                    awaitEachGesture {
                                        // Modifier.clickable doesn't work for text fields, so we use Modifier.pointerInput
                                        // in the Initial pass to observe events before the text field consumes them
                                        // in the Main pass.
                                        awaitFirstDown(pass = PointerEventPass.Initial)
                                        val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                                        if (upEvent != null) {
                                            showDatePicker = true
                                        }
                                    }
                                }
                        )
                    }
                }
            }

            itemsIndexed(items) { index, item ->
                if (isEditing) {
                    Row {
                        IconButton(onClick = { onDeleteItem(item) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                        renderItem(item) {
                            onEditItem(index, it)
                        }
                    }
                } else {
                    renderReadonlyItem(item)
                }
            }
        }

        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Add Product") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = newPLU,
                            onValueChange = { newPLU = it },
                            label = { Text("PLU") }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newQty,
                            onValueChange = { newQty = it },
                            label = { Text("Quantity") }
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        onAddItem(newPLU, newQty)
                        showAddDialog = false
                    }) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    Button(onClick = { showAddDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (showDatePicker) {
            DatePickerModal(
                onDateSelected = { it ->
                    if (it != null) {
                        customDate = viewModel.convertMillisToString(it)
                        onDateSelected(it)
                        Log.i("Dolan", "UPDATED RAPORT $customDate")

                    }
                },
                onDismiss = { showDatePicker = false }
            )
        }

        if (isLoading) {
            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
        }
    }
}
