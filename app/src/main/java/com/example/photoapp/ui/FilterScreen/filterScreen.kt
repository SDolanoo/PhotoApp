package com.example.photoapp.ui.FilterScreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel



// Main FilterScreen Composable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScreenContent(
    onApplyFilters: (FilterState) -> Unit,
    onClearAll: () -> Unit,
    filterController: FilterController = hiltViewModel(),
    ) {
    var state by remember { mutableStateOf(FilterState.default()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Filters") },
                navigationIcon = {
                    IconButton(onClick = { /* Handle back */ }) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Date Range Section
            DateRangeSection(state = state, onUpdate = { state = it })

            // Price Range Section
            PriceRangeSection(state = state, onUpdate = { state = it })

            // Buyer, Seller, Product
            FilterTextRow("Buyer", state.buyer) {
                state = state.copy(buyer = it)
            }
            FilterTextRow("Seller", state.seller) {
                state = state.copy(seller = it)
            }
            FilterTextRow("Product", state.product) {
                state = state.copy(product = it)
            }

            // Buttons
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(onClick = {
                    state = FilterState.default()
                    onClearAll()
                }) {
                    Text("Clear All")
                }
                Button(onClick = { onApplyFilters(state) }) {
                    Text("Apply Filters")
                }
            }
        }
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


@Composable
fun SegmentedButtonsFilterChange(selectedFilter: String, onFilterChange: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = { onFilterChange("paragon") },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedFilter == "paragon") Color.Blue else Color.Gray
            )
        ) {
            Text("Paragon")
        }
        Button(
            onClick = { onFilterChange("faktura") },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedFilter == "faktura") Color.Blue else Color.Gray
            )
        ) {
            Text("Faktura")
        }
    }
}

