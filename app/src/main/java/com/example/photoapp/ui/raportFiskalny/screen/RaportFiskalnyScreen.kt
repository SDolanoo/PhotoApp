package com.example.photoapp.ui.raportFiskalny.screen

import android.util.Log
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.photoapp.database.data.RaportFiskalny
import com.example.photoapp.navigation.PhotoAppDestinations
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RaportFiskalnyScreen(
    navController: NavHostController,
    navigateToCameraView: (String)-> Unit,
    navigateToRaportFiskalnyDetailsScreen: (RaportFiskalny) -> Unit,
    viewModel: RaportFiskalnyScreenViewModel = hiltViewModel()
    ) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    val allRaportFiskalny by viewModel.allRaportFiskalny.observeAsState(emptyList())

    val groupedRaportFiskalny = viewModel.getGroupedRaportFiskalnyList(allRaportFiskalny)

    Log.i("Dolan", "Showing paragony: $allRaportFiskalny")

    val isDeleteMode by viewModel.isDeleteMode

    val selectedItems = viewModel.selectedItems

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),

        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    if (isDeleteMode) {
                        Text(
                            "Usuń Raporty Fiskalne",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    } else {
                        Text(
                            "Widok Raporty Fiskalne",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
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
                    navigateToCameraView("raportFiskalny")
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        },
    ) { innerPadding ->

        ScrollContent(
            innerPadding,
            groupedRaportFiskalny = groupedRaportFiskalny,
            navigateToRaportFiskalnyDetailsScreen = navigateToRaportFiskalnyDetailsScreen,
            isDeleteMode = isDeleteMode,
            selectedItems = selectedItems,
            onItemSelected = { viewModel.toggleItemSelection(it) },
            viewModel = viewModel
        )
    }
}


@Composable
fun ScrollContent(innerPadding: PaddingValues,
                  groupedRaportFiskalny: Map<Date?, List<RaportFiskalny>>,
                  navigateToRaportFiskalnyDetailsScreen: (RaportFiskalny) -> Unit,
                  isDeleteMode: Boolean,
                  selectedItems: List<RaportFiskalny>,
                  onItemSelected: (RaportFiskalny) -> Unit,
                  viewModel: RaportFiskalnyScreenViewModel
) {
    val calendarIcon = Icons.Default.DateRange

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(1),
        modifier = Modifier.fillMaxSize(),
        contentPadding = innerPadding,
        verticalItemSpacing = 2.dp,
    ) {
        groupedRaportFiskalny.forEach { (date, raportFiskalnyList) ->
            if (date != null) {
                val formattedDate  = date.let {
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .format(it)
                }
                item {
                    Row( modifier = Modifier.padding(start = 5.dp, top = 5.dp) ) {
                        Icon(
                            imageVector = calendarIcon,
                            contentDescription = "Calendar icon",
                            modifier = Modifier
                                .padding(all = 5.dp)
                                .size(24.dp * 0.8f)
                        )
                        Text(
                            text = formattedDate ,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 9.dp)
                        )
                    }
                }
                items(raportFiskalnyList.size) { index ->
                    val raportFiskalny = raportFiskalnyList[index]
                    Column {
                        if (index > 0) {
                            HorizontalDivider(thickness = 1.dp)
                        }
                        RaportFiskalnyItem(
                            raportFiskalny = raportFiskalny,
                            navigateToRaportFiskalnyDetailsScreen = navigateToRaportFiskalnyDetailsScreen,
                            isDeleteMode = isDeleteMode,
                            isSelected = selectedItems.contains(raportFiskalny),
                            onItemSelected = onItemSelected,
                            viewModel = viewModel
                        )
                        if (index == raportFiskalnyList.size - 1) {
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun RaportFiskalnyItem(
    raportFiskalny: RaportFiskalny,
    navigateToRaportFiskalnyDetailsScreen: (RaportFiskalny) -> Unit,
    isDeleteMode: Boolean,
    isSelected: Boolean,
    onItemSelected: (RaportFiskalny) -> Unit,
    viewModel: RaportFiskalnyScreenViewModel
) {
    val formattedDate  = raportFiskalny.dataDodania?.let {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            .format(it)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { if (!isDeleteMode) navigateToRaportFiskalnyDetailsScreen(raportFiskalny) }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isDeleteMode) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onItemSelected(raportFiskalny) }
            )
        }
        ListItem(
            modifier = Modifier
                .clickable { if (!isDeleteMode) navigateToRaportFiskalnyDetailsScreen(raportFiskalny) },
            // jest clickable -> przenosi nas na inną strone, może navigation
            headlineContent = { Text("Data Zakupu: $formattedDate ") },
            trailingContent = { Text("${viewModel.getCountForProductsForRaport(raport = raportFiskalny)}")}
        )
    }
}