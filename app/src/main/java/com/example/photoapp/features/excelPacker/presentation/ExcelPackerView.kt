package com.example.photoapp.features.excelPacker.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.photoapp.R
import com.example.photoapp.core.components.MyNavigationBar
import com.example.photoapp.core.navigation.NavBarDestinations
import com.example.photoapp.core.navigation.PhotoAppDestinations
import com.example.photoapp.features.faktura.data.faktura.Faktura
import com.example.photoapp.features.faktura.presentation.screen.FakturaScreenViewModel
import com.example.photoapp.features.FilterScreen.FilterController
import com.example.photoapp.features.FilterScreen.FilterScreenContent
import com.example.photoapp.features.FilterScreen.FilterState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.forEach

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExcelPacker(
    navController: NavHostController,
    exportRoomViewModel: ExportRoomViewModel = hiltViewModel(),
    viewModel: FakturaScreenViewModel = hiltViewModel(),
    filterController: FilterController = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var isCircularIndicatorShowing by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    var isFilterExpanded by remember { mutableStateOf(false) }
    var filterState = remember { mutableStateOf(FilterState.default()) }

    val groupedFaktury by viewModel.groupedFaktury.collectAsState()

    val requestStoragePermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.i("Dolan", "Permission Granted")
            coroutineScope.launch {
                isCircularIndicatorShowing = true
                delay(3000)
                exportRoomViewModel.exportToExcel("faktura", viewModel.getCurrentlyShowingList())
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
                title = {
                    Text(
                        if (isFilterExpanded) "Filtry" else "Export do Excel",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    if (isFilterExpanded) {
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
                    if (!isFilterExpanded) {
                        IconButton(onClick = { isFilterExpanded = true }) {
                            Icon(painter = painterResource(R.drawable.baseline_filter_list_alt_24), contentDescription = "Exit Filters")
                        }
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            if (!isFilterExpanded) {
                FloatingActionButton(onClick = {
                    Log.i("Dolan", "ASKING FOR PERMISSIOBNS")
                    if (Build.VERSION.SDK_INT < 33) {
                        when (PackageManager.PERMISSION_GRANTED) {
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ) -> {
                                coroutineScope.launch {
                                    Log.i("Dolan", "Writing to Excel")
                                    isCircularIndicatorShowing = true
                                    delay(3000)
                                    exportRoomViewModel.exportToExcel("faktura", viewModel.getCurrentlyShowingList())
                                }.invokeOnCompletion {
                                    isCircularIndicatorShowing = false
                                }
                            }

                            else -> {
                                requestStoragePermission.launch(
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                )
                            }
                        }
                    } else {
                        Log.i("Dolan", "Writing to Excel")
                        coroutineScope.launch {
                            isCircularIndicatorShowing = true
                            delay(3000)
                            exportRoomViewModel.exportToExcel("faktura", viewModel.getCurrentlyShowingList())
                        }.invokeOnCompletion {
                            isCircularIndicatorShowing = false
                        }
                    }
                    Log.i("Dolan", "NOTHING HAPPENED")
                }) {
                    Icon(Icons.Default.Add, contentDescription = "ExportToExcel")
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
            if (!isFilterExpanded) {
                MyNavigationBar(
                    navController = navController,
                    destinations = NavBarDestinations.entries
                )
            }
        },
        snackbarHost = {SnackbarHost(snackbarHostState)},
    ) { innerPadding ->
        Column {
            if (isFilterExpanded) {
                FilterScreenContent(
                    state = filterState,
                    paddingValues = innerPadding
                )
            } else {
                NFakturaScrollContent(
                    innerPadding,
                    groupedFaktury = groupedFaktury,
                )
            }
        }
    }
}

@Composable
private fun NFakturaScrollContent(
    innerPadding: PaddingValues,
    groupedFaktury: Map<Date?, List<Faktura>>,
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
                        NFakturaItem(
                            faktura = faktura
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
private fun NFakturaItem(
    faktura: Faktura,
) {
    val formattedDate  = faktura.dataWystawienia?.let {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            .format(it)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {  }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ListItem(
            modifier = Modifier
                .clickable {  },
            headlineContent = { Text("Data Wystawienia: $formattedDate") },
            supportingContent = { Text("Netto ${faktura.razemNetto}") },
            trailingContent = { Text(faktura.razemBrutto) }
        )
    }
}
