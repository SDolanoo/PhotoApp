package com.example.photoapp.features.paragon.details

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
import com.example.photoapp.features.paragon.data.Paragon
import com.example.photoapp.features.paragon.data.ProduktParagon
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParagonDetailsScreen(
    navController: NavHostController,
    paragon: Paragon?,
    viewModel: ParagonDetailsScreenViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val products by viewModel.products.collectAsState()

    // Load products once when screen opens
    LaunchedEffect(paragon) {
        paragon?.let {
            viewModel.loadProductsForParagon(it.id)
        }
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
                        "Szczegóły Paragonu",
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
        paragon?.let {
            ParagonDetailsContent(
                innerPadding = innerPadding,
                paragon = it,
                produkty = products
            )
        }
    }
}


@Composable
fun ParagonDetailsContent(
    innerPadding: PaddingValues,
    paragon: Paragon,
    produkty: List<ProduktParagon>
) {
    val dateformat = paragon.dataZakupu?.let {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            .format(it)
    }

    LazyColumn(
        contentPadding = innerPadding,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxSize()
    ) {
        item {
            ParagonDetailsRow(label = "Data zakupu:", value = dateformat.orEmpty())
            ParagonDetailsRow(label = "Nazwa sklepu:", value = paragon.nazwaSklepu)
            ParagonDetailsRow(label = "Kwota całkowita:", value = paragon.kwotaCalkowita.toString())
        }

        item {
            Text(
                text = "Produkty:",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        items(produkty.size) { index ->
            val product = produkty[index]
            ParagonProductDetails(
                name = product.nazwaProduktu,
                quantity = product.ilosc.toString(),
                price = product.cenaSuma.toString()
            )
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
