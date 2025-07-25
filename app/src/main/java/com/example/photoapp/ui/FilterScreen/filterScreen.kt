package com.example.photoapp.ui.FilterScreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

// Main FilterScreen Composable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScreenContent(
    state: MutableState<FilterState>,
    paddingValues: PaddingValues,
    filterController: FilterController = hiltViewModel(),
    ) {

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Date Range Section
        DateRangeSection(state = state.value, onUpdate = { state.value = it })

        // Price Range Section
        PriceRangeSection(state = state.value, onUpdate = { state.value = it })

    }
}


@Composable
fun FilterTextRow(label: String, value: String, onChange: (String) -> Unit) {
    Column(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(label)
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            placeholder = { Text("Enter $label") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
