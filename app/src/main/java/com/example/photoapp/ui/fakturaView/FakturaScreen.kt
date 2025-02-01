package com.example.photoapp.ui.fakturaView

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.example.photoapp.database.DatabaseViewModel
import com.example.photoapp.database.data.Faktura
import com.example.photoapp.utils.normalizedDate
import java.text.SimpleDateFormat
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FakturaScreen(
    navController: NavHostController,
    databaseViewModel: DatabaseViewModel = hiltViewModel(),
    navigateToCameraView: (String)-> Unit,
    navigateToFakturaDetailsScreen: (Faktura) -> Unit,
    navigateToFiltersScreen: () -> Unit,
//    currentlyShowing: String,
    showFilteredFaktury: Boolean,
    fakturaFilteredList: List<Faktura>,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),

        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        "Widok Faktury",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Localized description"
                        )
                    }
                    IconButton(onClick = { navigateToFiltersScreen() }) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Localized description"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                expanded = !expanded
//                navigateToCameraView()
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }

        },
    ) { innerPadding ->

        ScrollContent(
            innerPadding,
            databaseViewModel = databaseViewModel,
            navigateToFakturaDetailsScreen = navigateToFakturaDetailsScreen,
            showFilteredFaktury = showFilteredFaktury,
            fakturaFilteredList = fakturaFilteredList,
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Add Paragon") },
                onClick = { navigateToCameraView("paragon") }
            )

            HorizontalDivider()

            DropdownMenuItem(
                text = { Text("Add Faktura") },
                onClick = { navigateToCameraView("faktura") }
            )
        }
    }
}
//}

@Composable
fun ScrollContent(innerPadding: PaddingValues,
                  databaseViewModel: DatabaseViewModel,
                  navigateToFakturaDetailsScreen: (Faktura) -> Unit,
                  showFilteredFaktury: Boolean,
                  fakturaFilteredList: List<Faktura>,
) {

    val allFakturys by databaseViewModel.allLiveFaktura.observeAsState(emptyList())
    val faktury = if (showFilteredFaktury) {
        fakturaFilteredList
    } else {
        allFakturys
    }

    val groupedFaktury = faktury
        .sortedByDescending { it.dataWystawienia }
        .groupBy { faktura -> faktura.dataWystawienia?.normalizedDate() }

    val calendarIcon = Icons.Default.DateRange

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(1),
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = innerPadding,
        verticalItemSpacing = 2.dp,
    ) {
        groupedFaktury.forEach { (date, fakturaList) ->
            if (date != null) {
                val dateformat = date.let {
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it)
                }
                item {
                    Row( modifier = Modifier.padding(start = 15.dp, top = 5.dp) ) {
                        Icon(
                            imageVector = calendarIcon,
                            contentDescription = "Calendar icon",
                            modifier = Modifier.padding(start = 1.dp, end = 5.dp)
                        )
                        Text(
                            text=dateformat,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 5.dp, end = 5.dp)
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
                            navigateToFakturaDetailsScreen = navigateToFakturaDetailsScreen
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
fun FakturaItem(faktura: Faktura, navigateToFakturaDetailsScreen: (Faktura) -> Unit) {
    val dateformat = faktura.dataWystawienia?.let {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            .format(it)
    }

    ListItem(
        modifier = Modifier.clickable { navigateToFakturaDetailsScreen(faktura) },
        // jest clickable -> przenosi nas na inną strone, może navigation
        headlineContent = { Text("Data Wystawienia: $dateformat") },
        supportingContent = { Text("Netto ${faktura.razemNetto}") },
        trailingContent = { Text(faktura.razemBrutto) }
    )
}