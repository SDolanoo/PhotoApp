package com.example.photoapp.ui.RaportFiskalny.Screen

import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import com.example.photoapp.R
import com.example.photoapp.ui.ExcelPacker.ExportRoomViewModel
import kotlinx.coroutines.launch

@Composable
fun ExportToExcelButton(
    modifier: Modifier = Modifier,
    data: List<Any>,
    exportViewModel: ExportRoomViewModel,
    fileLabel: String,
    snackbarHostState: SnackbarHostState,
    onLoadingStateChanged: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            coroutineScope.launch {
                onLoadingStateChanged(true)
                exportViewModel.exportToExcel(fileLabel, data)
                onLoadingStateChanged(false)
            }
        } else {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = "Permission denied. Can't export file.",
                    duration = SnackbarDuration.Long
                )
            }
        }
    }

    IconButton(onClick = {
        val exportJob: suspend () -> Unit = {
            onLoadingStateChanged(true)
            exportViewModel.exportToExcel(fileLabel, data)
            onLoadingStateChanged(false)
        }

        if (Build.VERSION.SDK_INT < 33) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                coroutineScope.launch { exportJob() }
            } else {
                requestPermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        } else {
            coroutineScope.launch { exportJob() }
        }
    }) {
        Icon(
            painter = painterResource(id = R.drawable.upload_file),
            contentDescription = "Export to Excel",
            modifier = modifier
        )
    }
}