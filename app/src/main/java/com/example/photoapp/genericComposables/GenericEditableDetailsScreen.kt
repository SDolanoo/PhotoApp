package com.example.photoapp.genericComposables

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.photoapp.ui.ExcelPacker.ExportRoomViewModel
import com.example.photoapp.utils.convertMillisToString


@Suppress("UNCHECKED_CAST")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> GenericEditableDetailsScreen(
    title: String,
    leaveDetailsScreen: () -> Unit,
    navigateToCameraAndSetRF: () -> Unit,
    actualItems: List<T>,
    editingItems: List<T>,
    editCanceled: () -> Unit,
    editAccepted: () -> Unit,
    onAddItem: (String, String) -> Unit,
    onEditItem: (Int, T) -> Unit,
    onDeleteItem: (T) -> Unit,
    renderEditableItem: @Composable (T, (T) -> Unit) -> Unit,
    renderReadonlyItem: @Composable (T) -> Unit,
    enableDatePicker: Boolean = false,
    initialDate: String,
    onDateSelected: (Long) -> Unit,
    renderTopBarActions: @Composable (() -> Unit)? = null,
    exportRoomViewModel: ExportRoomViewModel = hiltViewModel()
) {
    //[START] Excel Packer
    var isCircularIndicatorShowing by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val alphaAnimation by animateFloatAsState(
        targetValue = if (isCircularIndicatorShowing) 1f else 0f,
        animationSpec = tween(durationMillis = 300) // Animacja przejścia w 300 ms
    )

    CircularProgressIndicator(
        modifier = Modifier
            .graphicsLayer(alpha = alphaAnimation) // Animacja przezroczystości
    )
    // [END] Excel Packer

    var isEditing by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    var newPLU by remember { mutableStateOf("") }
    var newQty by remember { mutableStateOf("") }

    var customDate by remember { mutableStateOf(initialDate) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    if (isEditing) {
                        IconButton(onClick = {
                            isEditing = false
                            editCanceled()
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Back")
                        }
                    } else {
                        IconButton(onClick = {
                            leaveDetailsScreen()
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    if (isEditing) {
                        IconButton(onClick = { showAddDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Add")
                        }
                        IconButton(onClick = {
                            isEditing = false
                            editAccepted()
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
                        ExportToExcelButton(
                            data = actualItems as List<Any>,
                            exportViewModel = exportRoomViewModel,
                            fileLabel = "raport fiskalny",
                            snackbarHostState = snackbarHostState,
                            onLoadingStateChanged = { isCircularIndicatorShowing = it }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (!isEditing) {
                FloatingActionButton(onClick = {
                    navigateToCameraAndSetRF()
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            if (enableDatePicker) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (isEditing) {
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
                                            val upEvent =
                                                waitForUpOrCancellation(pass = PointerEventPass.Initial)
                                            if (upEvent != null) {
                                                showDatePicker = true
                                            }
                                        }
                                    }
                            )
                        } else {
                            DetailsRow(
                                label = "data_zakupu:",
                                value = initialDate
                            )
                        }
                    }
                }
            }
            if (isEditing) {
                itemsIndexed(editingItems) { index, item ->
                    Row {
                        IconButton(onClick = { onDeleteItem(item) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                        renderEditableItem(item) {
                            onEditItem(index, it)
                            Log.i("Dolan", "Zmiana ceny !!!!!!!!!!!!!!!!!!!!!!!!!")
                        }
                    }
                }
            } else {
                itemsIndexed(actualItems) { index, item ->
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
                        customDate = convertMillisToString(it)
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

@Composable
fun DetailsRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(text = value, fontSize = 16.sp)
    }
}

