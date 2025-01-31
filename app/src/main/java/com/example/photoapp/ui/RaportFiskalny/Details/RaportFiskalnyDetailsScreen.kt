package com.example.photoapp.ui.RaportFiskalny.Details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.material3.Scaffold
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.photoapp.database.DatabaseViewModel
import com.example.photoapp.database.data.ProduktRaportFiskalny
import com.example.photoapp.database.data.RaportFiskalny
import com.example.photoapp.navigation.PhotoAppDestinations
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RaportFiskalnyDetailsScreen(
    navController: NavHostController,
    raportFiskalny: RaportFiskalny?,
    viewModel: RaportFiskalnyViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    var expanded by remember { mutableStateOf(false) }

    val produkty by viewModel.produkty.collectAsState()

    LaunchedEffect(raportFiskalny) {
        raportFiskalny?.id?.let { viewModel.loadProducts(it) }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(

                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        "Centered Top App Bar",
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
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More options")
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        }
    ) { innerPadding ->
        if (raportFiskalny != null) {
            RaportFiskalnyDetailsContent(
                innerPadding = innerPadding,
                raportFiskalny = raportFiskalny,
                produkty=produkty,
                viewModel = viewModel)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false}
        ) {
            DropdownMenuItem(
                text = { Text("Edytuj")},
                onClick = { /* TODO */}
            )

            HorizontalDivider()

            DropdownMenuItem(
                text = { Text("Usuń pola")},
                onClick = { /* TODO */}
            )

            HorizontalDivider()

            DropdownMenuItem(
                text = { Text("Dodaj więcej")},
                onClick = { /* TODO */}
            )

            HorizontalDivider()

            DropdownMenuItem(
                text = { Text("Przenieś dane")},
                onClick = { /* TODO */}
            )
        }
    }
}

@Composable
fun RaportFiskalnyDetailsContent(
    innerPadding: PaddingValues,
    raportFiskalny: RaportFiskalny,
    produkty: List<ProduktRaportFiskalny>,
    viewModel: RaportFiskalnyViewModel) {
    LazyColumn(
        contentPadding = innerPadding,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxSize()
    ) {
        item {
            RaportFiskalnyDetailsRow(
                label = "data_zakupu:",
                value = viewModel.formatDate(raportFiskalny.dataDodania?.time)
            )
        }

        item {
            Text(
                text = "produkty:",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        produkty.forEach { product ->
            item {
                RaportFiskalnyProductDetails(
                    nrPLU = product.nrPLU,
                    quantity = product.ilosc.toString(),
                )
            }
        }
    }
}

@Composable
fun RaportFiskalnyDetailsRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(text = value, fontSize = 16.sp)
    }
}

@Composable
fun RaportFiskalnyProductDetails(nrPLU: String, quantity: String) {
    Column(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Row{
            Text(text = "nrPLU", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = nrPLU, fontSize = 16.sp)
        }
        Row{
            Text(text = "ilosc", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = quantity, fontSize = 14.sp)
        }

    }
}