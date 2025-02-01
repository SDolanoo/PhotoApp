package com.example.photoapp.ui.RaportFiskalny.Details

import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.photoapp.R
import com.example.photoapp.database.DatabaseViewModel
import com.example.photoapp.database.data.ProduktRaportFiskalny
import com.example.photoapp.database.data.RaportFiskalny
import com.example.photoapp.navigation.PhotoAppDestinations
import com.example.photoapp.ui.ExcelPacker.ExportRoomViewModel
import com.example.photoapp.ui.RaportFiskalny.Details.composables.Default.RFDefaultDetailsContent
import com.example.photoapp.ui.RaportFiskalny.Details.composables.Default.RFDefaultTopAppBar
import com.example.photoapp.ui.RaportFiskalny.Details.composables.IsEditing.RFEditingDetailsContent
import com.example.photoapp.ui.RaportFiskalny.Details.composables.IsEditing.RFEditingTopAppBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RaportFiskalnyDetailsScreen(
    navController: NavHostController,
    raportFiskalny: RaportFiskalny?,
    viewModel: RaportFiskalnyViewModel = hiltViewModel()
) {

    var isCircularIndicatorShowing by remember { mutableStateOf(false) }

    val produkty by viewModel.produkty.collectAsState()

    LaunchedEffect(raportFiskalny) {
        raportFiskalny?.id?.let { viewModel.loadProducts(it) }
    }

    var isEditing by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            if (isEditing == false) {
                RFDefaultTopAppBar(
                    navController = navController,
                    isCircularIndicatorShowing={ trueOrFalse ->
                        isCircularIndicatorShowing = trueOrFalse},
                    changeEditingState = { trueOrFalse ->
                        isEditing = trueOrFalse},
                    produkty = produkty,

                )
            } else {
                RFEditingTopAppBar(
                    navController = navController,
                    changeEditingState = { trueOrFalse ->
                        isEditing = trueOrFalse},
                    produkty = produkty,
                    viewModel = viewModel
                )
            }
        }
    ) { innerPadding ->
        if (raportFiskalny != null) {
            if (isEditing == false) {
                RFDefaultDetailsContent(
                    innerPadding = innerPadding,
                    raportFiskalny = raportFiskalny,
                    produkty = produkty,
                    viewModel = viewModel
                )
            } else {
                RFEditingDetailsContent(
                    innerPadding = innerPadding,
                    raportFiskalny = raportFiskalny,
                    produkty = produkty,
                    viewModel = viewModel
                )
            }
        }

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
    }
}

