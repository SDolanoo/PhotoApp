package com.example.photoapp.core.components

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
import com.example.photoapp.core.utils.convertMillisToString


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
    onAddItem: (T) -> Unit,
    onEditItem: (Int, T) -> Unit,
    onDeleteItem: (T) -> Unit,
    renderEditableItem: @Composable (T, (T) -> Unit) -> Unit,
    renderReadonlyItem: @Composable (T) -> Unit,
    renderAddItemDialog: @Composable ((onAdd: (T) -> Unit, onDismiss: () -> Unit) -> Unit)? = null,
    renderEditItemDialog: @Composable ((itemToEdit: T, onEdit: (T) -> Unit, onDismiss: () -> Unit) -> Unit)? = null,
    enableDatePicker: Boolean = false,
    initialDate: String,
    onDateSelected: (Long) -> Unit,
    renderTopBarActions: @Composable (() -> Unit)? = null
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
    var showAddItemDialog by remember { mutableStateOf(false) }

    var showEditItemDialog by remember { mutableStateOf(false) }
    var currentlyEdited by remember { mutableStateOf(1)}

    var showDatePicker by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

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
                        IconButton(onClick = { showAddItemDialog = true }) {
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
                                label = "Data:",
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
                        IconButton(onClick = {
                            showEditItemDialog = true
                            currentlyEdited = index
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                        renderReadonlyItem(item)
                    }
                }
            } else {
                itemsIndexed(actualItems) { index, item ->
                    renderReadonlyItem(item)
                }
            }
        }

        if (showAddItemDialog && renderAddItemDialog != null) {
            renderAddItemDialog( // IDK WHY THIS CANNOT HAVE ARGUMENTS!!!
                { item -> //onAdd
                    onAddItem(item)
                    showAddItemDialog = false
                },
                { // onDismiss
                    showAddItemDialog = false
                }
            )
        }

        if (showEditItemDialog && renderEditItemDialog != null) {
            renderEditItemDialog( // IDK WHY THIS CANNOT HAVE ARGUMENTS!!!
                editingItems[currentlyEdited],
                { item -> //onEdit
                    onEditItem(currentlyEdited, item)
                    showEditItemDialog = false
                },
                { // onDismiss
                    showEditItemDialog = false
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

