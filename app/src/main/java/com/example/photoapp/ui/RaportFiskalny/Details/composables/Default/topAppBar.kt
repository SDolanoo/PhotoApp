package com.example.photoapp.ui.RaportFiskalny.Details.composables.Default

import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.photoapp.R
import com.example.photoapp.database.data.ProduktRaportFiskalny
import com.example.photoapp.navigation.PhotoAppDestinations
import com.example.photoapp.ui.ExcelPacker.ExportRoomViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RFDefaultTopAppBar(
    navController: NavHostController,
    isCircularIndicatorShowing: (Boolean) -> Unit,
    changeEditingState: (Boolean) -> Unit,
    produkty: List<ProduktRaportFiskalny>,
    exportRoomViewModel: ExportRoomViewModel = hiltViewModel()
){
    //[START] Excel Packer
    val context = LocalContext.current

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val requestStoragePermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.i("Dolan", "Permission Granted")
            coroutineScope.launch {
                isCircularIndicatorShowing(true)
                delay(3000)
                exportRoomViewModel.exportToExcel(
                    whatToExport = "raport fiskalny",
                    listToExport = produkty
                )
                isCircularIndicatorShowing(false)
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
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    var expanded by remember { mutableStateOf(false) }

    val density = LocalDensity.current
    var offsetX by remember {
        mutableStateOf(0.dp)
    }

    var parentWidth by remember {
        mutableStateOf(0)
    }

    Column(modifier = Modifier.fillMaxWidth().onPlaced {
        parentWidth = it.size.width
    }) {
        CenterAlignedTopAppBar(

            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
            title = {
                Text(
                    "Szczegóły",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                IconButton(onClick = { navController.navigate(PhotoAppDestinations.RAPORT_FISKALNY_SCREEN_ROUTE) }) {
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
                                    isCircularIndicatorShowing(true)
                                    delay(3000)
                                    exportRoomViewModel.exportToExcel(
                                        whatToExport = "raport fiskalny",
                                        listToExport = produkty
                                    )
                                }.invokeOnCompletion {
                                    isCircularIndicatorShowing(false)
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
                            isCircularIndicatorShowing(true)
                            delay(3000)
                            exportRoomViewModel.exportToExcel(
                                whatToExport = "raport fiskalny",
                                listToExport = produkty
                            )
                        }.invokeOnCompletion {
                            isCircularIndicatorShowing(false)
                        }
                    }
                    Log.i("Dolan", "NOTHING HAPPENED")
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.upload_file),
                        contentDescription = "Localized description"
                    )
                }
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More options")
                }
            },
            scrollBehavior = scrollBehavior,
        )
        DropdownMenu(
            modifier = Modifier.onPlaced {
                val popUpWidthPx =
                    parentWidth  - it.size.width

                offsetX = with(density) {
                    popUpWidthPx.toDp()
                }

            },
            offset = DpOffset(offsetX, 0.dp),
            expanded = expanded,
            onDismissRequest = { expanded = false}
        ) {
            DropdownMenuItem(
                text = { Text("Edytuj") },
                onClick = { changeEditingState(true) }
            )

            HorizontalDivider()

            DropdownMenuItem(
                text = { Text("Dodaj więcej") },
                onClick = { /* TODO */}
            )
        }
    }
}
