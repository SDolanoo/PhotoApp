package com.example.photoapp.ui.ExcelPacker

import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.photoapp.features.faktura.data.faktura.Faktura
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.collections.sortedByDescending

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExcelPacker(
    navController: NavHostController,
    exportRoomViewModel: ExportRoomViewModel = hiltViewModel(),

    navigateToFakturaDetailsScreen: (Faktura) -> Unit,
    navigateToFiltersScreen: () -> Unit,
    showFilteredFaktura: Boolean,
    fakturaFilteredList: List<Faktura>,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    val coroutineScope = rememberCoroutineScope()

    // hello
    val allFaktura by exportRoomViewModel.allFaktura.observeAsState(emptyList())
    Log.i("Dolan", "Showing paragony: $allFaktura")

    val fakturaListToShow = if (showFilteredFaktura) {
        fakturaFilteredList
    } else {
        allFaktura
    }
    // world

    val context = LocalContext.current
    var isCircularIndicatorShowing by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    val requestStoragePermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.i("Dolan", "Permission Granted")
            coroutineScope.launch {
                isCircularIndicatorShowing = true
                delay(3000)
                exportRoomViewModel.exportToExcel("faktura", fakturaListToShow)
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
                        "ExcelPacker",
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
        snackbarHost = {SnackbarHost(snackbarHostState)},
        floatingActionButton = {
            FloatingActionButton(onClick = {
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
                                exportRoomViewModel.exportToExcel("faktura", fakturaListToShow)
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
                        exportRoomViewModel.exportToExcel("faktura", fakturaListToShow)
                    }.invokeOnCompletion {
                        isCircularIndicatorShowing = false
                    }
                }
                Log.i("Dolan", "NOTHING HAPPENED")
            }) {
                Icon(Icons.Default.Add, contentDescription = "ExportToExcel")
            }

        },
    ) { innerPadding ->

        if (isCircularIndicatorShowing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.5f),
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        ScrollContent(
            innerPadding,
            fakturaListToShow = fakturaListToShow,
            navigateToFakturaDetailsScreen = navigateToFakturaDetailsScreen,

        )
    }
}
//}

@Composable
fun ScrollContent(innerPadding: PaddingValues,
                  fakturaListToShow: List<Faktura>,
                  navigateToFakturaDetailsScreen: (Faktura) -> Unit,
) {
    val faktura = fakturaListToShow

    val groupedFaktura = faktura
        .sortedByDescending { it.dataWystawienia }
        .groupBy { it.dataWystawienia }

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(1),
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = innerPadding,
        verticalItemSpacing = 2.dp,
    ) {
        groupedFaktura.forEach { (date, fakturaList) ->
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
                        if (index ==fakturaList.size - 1) {
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
        headlineContent = { Text("Data Zakupu: $dateformat") },
        supportingContent = { Text(faktura.numerFaktury) },
        trailingContent = { Text(faktura.razemBrutto) }
    )
}