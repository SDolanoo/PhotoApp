package com.example.photoapp.features.faktura.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.photoapp.R
import com.example.photoapp.core.components.MyNavigationBar
import com.example.photoapp.core.navigation.NavBarDestinations
import com.example.photoapp.core.navigation.PhotoAppDestinations
import com.example.photoapp.features.faktura.data.faktura.Faktura
import com.example.photoapp.features.FilterScreen.FilterController
import com.example.photoapp.features.FilterScreen.FilterScreenContent
import com.example.photoapp.features.FilterScreen.FilterState
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
    var isFilterExpanded by remember { mutableStateOf(false) }
    var filterState = remember { mutableStateOf(FilterState.default()) }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    val groupedFaktury by viewModel.groupedFaktury.collectAsState()

    val isDeleteMode by viewModel.isDeleteMode

    val selectedItems = viewModel.selectedItems



    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if (isDeleteMode) "Usuń Faktury" else if (isFilterExpanded) "Filtry" else "Widok Faktury",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    if (isDeleteMode) {
                        IconButton(onClick = { viewModel.deleteSelectedItems() }) {
                            Icon(Icons.Default.Close, contentDescription = "Cancel Deletion")
                        }
                    } else if (isFilterExpanded) {
                        IconButton(onClick = { isFilterExpanded = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Exit Filters")
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
                    }

                    if (!isFilterExpanded && !isDeleteMode) {
                        IconButton(onClick = { isFilterExpanded = true }) {
                            Icon(painter = painterResource(R.drawable.baseline_filter_list_alt_24), contentDescription = "Exit Filters")
                        }
                        IconButton(onClick = {
                            viewModel.toggleDeleteMode()
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Enable Delete Mode")
                        }
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            if (!isDeleteMode && !isFilterExpanded) {
                FloatingActionButton(onClick = {
                    navigateToCameraView("faktura")
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        },
        bottomBar = {
            if (isFilterExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .navigationBarsPadding()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // 🔁 POWTÓRZ
                        TextButton(
                            onClick = {
                                viewModel.clearFilters()
                                isFilterExpanded = false
                            },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color.LightGray),
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = Color.Black,
                                containerColor = Color.Transparent
                            ),
                            modifier = Modifier
                                .height(56.dp)
                                .weight(1f)
                        ) {
                            Text("Wyczyść", fontSize = 16.sp)
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // ✅ ZATWIERDŹ
                        Button(
                            onClick = {
                                if (filterController.isAllValid(filterState.value)) {
                                    val ff: List<Faktura> = filterController.applyFakturysFilters(filterState.value)
                                    viewModel.applyFilters(ff)
                                    isFilterExpanded = false
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .height(56.dp)
                                .weight(1f)
                        ) {
                            Text("Zatwierdź", fontSize = 16.sp)
                        }
                    }
                }
            }
            if (!isDeleteMode && !isFilterExpanded) {
                MyNavigationBar(
                    navController = navController,
                    destinations = NavBarDestinations.entries
                )
            }
        }
    ) { innerPadding ->
        Column {
            if (isFilterExpanded) {
                FilterScreenContent(
                    state = filterState,
                    paddingValues = innerPadding
                )
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
                            modifier = Modifier
                                .padding(all = 5.dp)
                                .size(24.dp * 0.8f)
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
