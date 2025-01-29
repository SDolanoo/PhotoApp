package com.example.photoapp.ui.paragonView

import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ThumbUp
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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.photoapp.R
import com.example.photoapp.database.DatabaseViewModel
import com.example.photoapp.database.data.Paragon
import com.example.photoapp.ui.ExcelPacker.ExportRoomViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParagonScreen(
    navController: NavHostController,
    navigateToCameraView: (String)-> Unit,
    navigateToParagonDetailsScreen: (Paragon) -> Unit,
    navigateToFiltersScreen: () -> Unit,
    showFilteredParagons: Boolean,
    paragonFilteredList: List<Paragon>,
    databaseViewModel: DatabaseViewModel = hiltViewModel(),
    exportRoomViewModel: ExportRoomViewModel = hiltViewModel()
//    currentlyShowing: String,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    var expanded by remember { mutableStateOf(false) }

    // hello
    val allParagons by databaseViewModel.allLiveParagony.observeAsState(emptyList())
    Log.i("Dolan", "Showing paragony: $allParagons")

    val paragonListToShow = if (showFilteredParagons) {
        paragonFilteredList
    } else {
        allParagons
    }
    // world

    //[START] Excel Packer
    val context = LocalContext.current
    var isCircularIndicatorShowing by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val requestStoragePermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.i("Dolan", "Permission Granted")
            coroutineScope.launch {
                isCircularIndicatorShowing = true
                delay(3000)
                exportRoomViewModel.exportToExcel(paragonListToShow)
                isCircularIndicatorShowing = false
            }
        } else {
            Log.i("Dolan", "NOT Granted")
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = "This feature is unavailable because it requires access to the phone's storage",
                    duration = SnackbarDuration.Long
                )
            }
        }
    }
    // [END] Excel Packer
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
                        "Widok Paragony",
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
                    IconButton(onClick = {
                        Log.i("Dolan", "ASKING FOR PERMISSIOBNS")
                        if (Build.VERSION.SDK_INT < 33) {
                            when (PackageManager.PERMISSION_GRANTED) {
                                ContextCompat.checkSelfPermission(
                                    context,
                                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                                ) -> {
                                    coroutineScope.launch {
                                        Log.i("Dolan", "Writing to Excel")
                                        isCircularIndicatorShowing = true
                                        delay(3000)
                                        exportRoomViewModel.exportToExcel(listToExport = paragonListToShow)
                                    }.invokeOnCompletion {
                                        isCircularIndicatorShowing = false
                                    }
                                }

                                else -> {
                                    requestStoragePermission.launch(
                                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                                    )
                                }
                            }
                        } else {
                            Log.i("Dolan", "Writing to Excel")
                            coroutineScope.launch {
                                isCircularIndicatorShowing = true
                                delay(3000)
                                exportRoomViewModel.exportToExcel(listToExport = paragonListToShow)
                            }.invokeOnCompletion {
                                isCircularIndicatorShowing = false
                            }
                        }
                        Log.i("Dolan", "NOTHING HAPPENED")
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.upload_file),
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
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }

        },
    ) { innerPadding ->

        ScrollContent(
            innerPadding,
            paragonListToShow = paragonListToShow,
            navigateToParagonDetailsScreen = navigateToParagonDetailsScreen,
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
                  paragonListToShow: List<Paragon>,
                  navigateToParagonDetailsScreen: (Paragon) -> Unit,
) {

    val groupedParagons = paragonListToShow
        .sortedByDescending { it.dataZakupu }
        .groupBy { it.dataZakupu }

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(1),
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = innerPadding,
        verticalItemSpacing = 2.dp,
    ) {
        groupedParagons.forEach { (date, paragonList) ->
            if (date != null) {
                val dateformat = date.let {
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .format(it)
                }
                item {
                    Text(
                        text = dateformat,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 15.dp, top = 5.dp)
                    )
                }
                items(paragonList.size) { index ->
                    val paragon = paragonList[index]
                    Column {
                        if (index > 0) {
                            HorizontalDivider(thickness = 1.dp)
                        }
                        ParagonItem(
                            paragon = paragon,
                            navigateToParagonDetailsScreen = navigateToParagonDetailsScreen
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
fun ParagonItem(paragon: Paragon, navigateToParagonDetailsScreen: (Paragon) -> Unit) {
    val dateformat = paragon.dataZakupu?.let {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            .format(it)
    }

    ListItem(
        modifier = Modifier.clickable { navigateToParagonDetailsScreen(paragon) },
        // jest clickable -> przenosi nas na inną strone, może navigation
        headlineContent = { Text("Data Zakupu: $dateformat") },
        supportingContent = { Text(paragon.nazwaSklepu) },
        trailingContent = { Text("${paragon.kwotaCalkowita}") }
    )
}