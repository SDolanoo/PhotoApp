package com.example.photoapp.features.paragon.ui.screen

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.photoapp.R
import com.example.photoapp.core.navigation.PhotoAppDestinations
import com.example.photoapp.features.paragon.data.Paragon
import com.example.photoapp.ui.ExcelPacker.ParagonItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParagonScreen(
    navController: NavHostController,
    navigateToCameraView: (String) -> Unit,
    navigateToParagonDetailsScreen: (Paragon) -> Unit,
    viewModel: ParagonScreenViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    val allParagony by viewModel.allParagony.observeAsState(emptyList())

    val groupedParagons = viewModel.getGroupedParagonsList(allParagony)

    val isDeleteMode by viewModel.isDeleteMode

    val selectedItems = viewModel.selectedItems

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if (isDeleteMode) "Usuń Paragony" else "Widok Paragony",
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
                    navigateToCameraView("paragon")
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        }
    ) { innerPadding ->
        ParagonScrollContent(
            innerPadding = innerPadding,
            groupedParagons = groupedParagons,
            navigateToParagonDetailsScreen = navigateToParagonDetailsScreen,
            isDeleteMode = isDeleteMode,
            selectedItems = selectedItems,
            onItemSelected = { viewModel.toggleItemSelection(it) }
        )
    }
}

@Composable
fun ParagonScrollContent(
    innerPadding: PaddingValues,
    groupedParagons: Map<Date?, List<Paragon>>,
    navigateToParagonDetailsScreen: (Paragon) -> Unit,
    isDeleteMode: Boolean,
    selectedItems: List<Paragon>,
    onItemSelected: (Paragon) -> Unit
) {
    val calendarIcon = Icons.Default.DateRange

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(1),
        modifier = Modifier.fillMaxSize(),
        contentPadding = innerPadding,
        verticalItemSpacing = 2.dp,
    ) {
        groupedParagons.forEach { (date, paragonList) ->
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
                                .padding(5.dp)
                                .size(19.dp)
                        )
                        Text(
                            text = formattedDate,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 9.dp)
                        )
                    }
                }
                items(paragonList.size) { index ->
                    val paragon = paragonList[index]
                    Column {
                        if (index > 0) HorizontalDivider(thickness = 1.dp)
                        ParagonItem(
                            paragon = paragon,
                            navigateToParagonDetailsScreen = navigateToParagonDetailsScreen,
                            isDeleteMode = isDeleteMode,
                            isSelected = selectedItems.contains(paragon),
                            onItemSelected = onItemSelected
                        )
                        if (index == paragonList.size - 1) {
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ParagonItem(
    paragon: Paragon,
    navigateToParagonDetailsScreen: (Paragon) -> Unit,
    isDeleteMode: Boolean,
    isSelected: Boolean,
    onItemSelected: (Paragon) -> Unit
) {
    val formattedDate  = paragon.dataZakupu?.let {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            .format(it)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { if (!isDeleteMode) navigateToParagonDetailsScreen(paragon) }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isDeleteMode) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onItemSelected(paragon) }
            )
        }
        ListItem(
            modifier = Modifier.clickable { if (!isDeleteMode) navigateToParagonDetailsScreen(paragon) },
            headlineContent = { Text("Data Zakupu: $formattedDate") },
            supportingContent = { Text(paragon.nazwaSklepu) },
            trailingContent = { Text("${paragon.kwotaCalkowita}") }
        )
    }

}
