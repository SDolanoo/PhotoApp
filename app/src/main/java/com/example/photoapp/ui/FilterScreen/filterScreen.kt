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
import com.example.photoapp.database.DatabaseViewModel
import com.example.photoapp.database.data.Faktura
import com.example.photoapp.database.data.Paragon
import com.example.photoapp.ui.FilterScreen.expandableSections.ExpandableSection


// Main FilterScreen Composable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScreen(
    onBackClick: () -> Unit,
    onParagonsFiltersApplied: (String, Boolean, List<Paragon>) -> Unit,
    onFakturyFiltersApplied: (String, Boolean, List<Faktura>) -> Unit,
    databaseViewModel: DatabaseViewModel = hiltViewModel(),
    ) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val filterController = remember { FilterController(databaseViewModel) }
    val currentFilter by filterController.currentFilter
//    val dateRange by filterController.dateRange.collectAsState()
//    val priceRange by filterController.priceRange.collectAsState()

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
                       },
        topBar = {
            TopAppBar(
                title = { Text("Filters") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF4267B2))
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = { filterController.clearAllValues() }) {
                    Text("Clear")
                }
                Button(onClick = {
                    if (filterController.currentFilter.value == "paragon") {
                        val (showFilteredParagons, filtersOfParagons) =
                            filterController.applyParagonsFilters()

                        onParagonsFiltersApplied(currentFilter, showFilteredParagons, filtersOfParagons)
                    } else if (filterController.currentFilter.value == "faktura") {
                        val (showFilteredFakturys, filtersOfFakturys) =
                            filterController.applyFakturysFilters()

                        onFakturyFiltersApplied(currentFilter, showFilteredFakturys, filtersOfFakturys)
                    }
//                    below logic for showing snackbar while testing
//                    scope.launch {
//                        var appliedFilters: String = "Filters are not applied"
//                        val filtersAreCleared = filterController.areFiltersCleared()
//                        if (filtersAreCleared == false) {
//                            appliedFilters = filterController.getMessageForSnackbar()
//                        }
//                        val result = snackbarHostState.showSnackbar(
//                            message = appliedFilters,
//                        )
//                    }
                }) {
                    Text("Apply")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Segmented Buttons
            SegmentedButtonsFilterChange(
                selectedFilter = currentFilter,
                onFilterChange = { filterController.changeFilter(it) }
            )

            // Date Filter
            ExpandableSection(title = "Data") {
                DateFilter(
                    filterController = filterController,
                    onDateRangeSelected = { A, B -> filterController.setDateRange(Pair(A, B)) }
                )
            }

            ExpandableSection(title = "Cena") {
                // Price Filter
                PriceFilter(
                    filterController = filterController,
                    onPriceRangeSelected = { A, B -> filterController.setPriceRange(Pair(A, B)) },
                )
            }

            // Other filters (e.g., recipients, vendors, stores)
            Text("Other Filters (Recipients, Vendors, Stores) are placeholders.")
        }
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

