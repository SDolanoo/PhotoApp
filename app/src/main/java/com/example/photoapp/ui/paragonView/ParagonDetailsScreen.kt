package com.example.photoapp.ui.paragonView

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.photoapp.database.DatabaseViewModel
import com.example.photoapp.database.data.Paragon
import com.example.photoapp.database.data.ProduktParagon
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParagonDetailsScreen(
    navController: NavHostController,
    paragon: Paragon?,
    databaseViewModel: DatabaseViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
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
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        }
    ) { innerPadding ->
        if (paragon != null) {
            ParagonDetailsContent(innerPadding = innerPadding, paragon = paragon, databaseViewModel=databaseViewModel)
        }
    }
}

@Composable
fun ParagonDetailsContent(innerPadding: PaddingValues, paragon: Paragon, databaseViewModel: DatabaseViewModel) {
    val dateformat = paragon.dataZakupu?.let {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            .format(it)
    }
    val produkty: List<ProduktParagon> = databaseViewModel.getProductForParagon(paragon.id)
    LazyColumn(
        contentPadding = innerPadding,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxSize()
    ) {
        item {
            ParagonDetailsRow(label = "data_zakupu:", value = dateformat.toString())
            ParagonDetailsRow(label = "nazwa_sklepu:", value = paragon.nazwaSklepu)
            ParagonDetailsRow(label = "kwota_calkowita:", value = paragon.kwotaCalkowita.toString())
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
                ParagonProductDetails(
                    name = product.nazwaProduktu,
                    quantity = product.ilosc.toString(),
                    price = product.cenaSuma.toString(),
                )
            }
        }
    }
}

@Composable
fun ParagonDetailsRow(label: String, value: String) {
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
fun ParagonProductDetails(name: String, quantity: String, price: String) {
    Column(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Text(text = name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(text = "$quantity x $price", fontSize = 14.sp)
    }
}
