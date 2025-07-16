package com.example.photoapp.features.faktura.ui.screen

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.photoapp.core.navigation.PhotoAppDestinations
import com.example.photoapp.features.faktura.data.faktura.Faktura
import com.example.photoapp.ui.FilterScreen.FilterController
import com.example.photoapp.ui.FilterScreen.FilterResult
import com.example.photoapp.ui.FilterScreen.FilterScreenContent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FakturaScreen(
    navController: NavHostController,
    navigateToCameraView: (String) -> Unit,
    navigateToFakturaDetailsScreen: (Faktura) -> Unit,
    viewModel: FakturaScreenViewModel = hiltViewModel(),
    filterController: FilterController = hiltViewModel()
) {
    val isFilterExpanded = remember { mutableStateOf(false) }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    val allFakturyLive by viewModel.allFakturyLive.observeAsState(emptyList())

    val groupedFaktury = viewModel.getGroupedFakturaList(allFakturyLive)

    Log.i("Dolan", "Showing paragony: $allFakturyLive")

    val isDeleteMode by viewModel.isDeleteMode

    val selectedItems = viewModel.selectedItems

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if (isDeleteMode) "Usuń Raporty Fiskalne" else "Widok Raporty Fiskalne",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    if (isDeleteMode) {
                        IconButton(onClick = { viewModel.deleteSelectedItems() }) {
                            Icon(Icons.Default.Close, contentDescription = "Cancel Deletion")
                        }
                    } else {
                        IconButton(onClick = { navController.navigate(PhotoAppDestinations.HOME_ROUTE) }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }

                },
                actions = {
                    if (isDeleteMode) {
                        IconButton(onClick = { viewModel.deleteSelectedItems() }) {
                            Icon(Icons.Default.Done, contentDescription = "Confirm Deletion")
                        }
                    } else {
                        IconButton(onClick = { viewModel.toggleDeleteMode() }) {
                            Icon(Icons.Default.Delete, contentDescription = "Enable Delete Mode")
                        }
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            if (!isDeleteMode) {
                FloatingActionButton(onClick = {
                    navigateToCameraView("faktura")
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        },
    ) { innerPadding ->
        Column {
            if (isFilterExpanded.value) {
                FilterScreenContent(filterController = filterController)

                Row(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(onClick = {
                        filterController.clearAllValues()
                    }) {
                        Text("Clear")
                    }

                    Button(onClick = {
                        val (startDate, endDate) = filterController.dateRange.value
                        val (minPrice, maxPrice) = filterController.priceRange.value

                        val filterResult = FilterResult(
                            filterType = "paragon",
                            startDate = startDate,
                            endDate = endDate,
                            minPrice = minPrice,
                            maxPrice = maxPrice,
                            dateFilterOption = filterController.currentFakturyDateFilter.value,
                            priceFilterOption = filterController.currentFakturyPriceFilter.value
                        )

                        viewModel.applyFakturaFilters(filterResult)
                        isFilterExpanded.value = false
                    }) {
                        Text("Apply")
                    }
                }
            } else {
                FakturaScrollContent(
                    innerPadding,
                    groupedFaktury = groupedFaktury,
                    navigateToFakturaDetailsScreen = navigateToFakturaDetailsScreen,
                    isDeleteMode = isDeleteMode,
                    selectedItems = selectedItems,
                    onItemSelected = { viewModel.toggleItemSelection(it) }
                )
            }
        }
    }
}

@Composable
fun FakturaScrollContent(
    innerPadding: PaddingValues,
    groupedFaktury: Map<Date?, List<Faktura>>,
    navigateToFakturaDetailsScreen: (Faktura) -> Unit,
    isDeleteMode: Boolean,
    selectedItems: List<Faktura>,
    onItemSelected: (Faktura) -> Unit
) {
    val calendarIcon = Icons.Default.DateRange

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(1),
        modifier = Modifier.fillMaxSize(),
        contentPadding = innerPadding,
        verticalItemSpacing = 2.dp,
    ) {
        groupedFaktury.forEach { (date, fakturaList) ->
            if (date != null) {
                val formattedDate  = date.let {
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .format(it)
                }
                item {
                    Row(modifier = Modifier.padding(start = 5.dp, top = 5.dp)) {
                        Icon(
                            imageVector = calendarIcon,
                            contentDescription = "Calendar icon",
                            modifier = Modifier.padding(all = 5.dp).size(24.dp * 0.8f)
                        )
                        Text(
                            text = formattedDate,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 9.dp)
                        )
                    }
                }
                items(fakturaList.size) { index ->
                    val faktura = fakturaList[index]
                    Column {
                        if (index > 0) {
                            HorizontalDivider(thickness = 1.dp)
                        }
                        FakturaItem(
                            faktura = faktura,
                            navigateToFakturaDetailsScreen = navigateToFakturaDetailsScreen,
                            isDeleteMode = isDeleteMode,
                            isSelected = selectedItems.contains(faktura),
                            onItemSelected = onItemSelected
                        )
                        if (index == fakturaList.size - 1) {
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FakturaItem(
    faktura: Faktura,
    navigateToFakturaDetailsScreen: (Faktura) -> Unit,
    isDeleteMode: Boolean,
    isSelected: Boolean,
    onItemSelected: (Faktura) -> Unit
) {
    val formattedDate  = faktura.dataWystawienia?.let {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            .format(it)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { if (!isDeleteMode) navigateToFakturaDetailsScreen(faktura) }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isDeleteMode) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onItemSelected(faktura) }
            )
        }
        ListItem(
            modifier = Modifier
                .clickable { if (!isDeleteMode) navigateToFakturaDetailsScreen(faktura) },
            headlineContent = { Text("Data Wystawienia: $formattedDate") },
            supportingContent = { Text("Netto ${faktura.razemNetto}") },
            trailingContent = { Text(faktura.razemBrutto) }
        )
    }
}
