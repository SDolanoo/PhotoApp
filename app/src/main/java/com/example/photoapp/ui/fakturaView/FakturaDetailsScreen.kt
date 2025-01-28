package com.example.photoapp.ui.fakturaView

import com.example.photoapp.database.data.Faktura

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
import com.example.photoapp.database.data.ProduktFaktura
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FakturaDetailsScreen(
    navController: NavHostController,
    faktura: Faktura?,
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
        if (faktura != null) {
            FakturaDetailsContent(innerPadding = innerPadding, faktura = faktura, databaseViewModel=databaseViewModel)
        }
    }
}

@Composable
fun FakturaDetailsContent(innerPadding: PaddingValues, faktura: Faktura, databaseViewModel: DatabaseViewModel) {
    val dataWystawienia = faktura.dataWystawienia?.let {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            .format(it)
    }
    val dataSprzedazy = faktura.dataSprzedazy?.let {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            .format(it)
    }
    val produkty: List<ProduktFaktura> = databaseViewModel.getProductForFaktura(faktura.id)
    LazyColumn(
        contentPadding = innerPadding,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxSize()
    ) {
        item {
            FakturaDetailsRow(label = "Data wystawienia:", value = dataWystawienia.toString())
            FakturaDetailsRow(label = "Data Sprzedazy:", value = dataSprzedazy.toString())
            FakturaDetailsRow(label = "Numer Faktury:", value = faktura.numerFaktury)
            FakturaDetailsRow(label = "Nr Rachunku Bankowego:", value = faktura.nrRachunkuBankowego.toString())
            FakturaDetailsRow(label = "Numer Faktury:", value = faktura.numerFaktury)
            FakturaDetailsRow(label = "Netto:", value = faktura.razemNetto)
            FakturaDetailsRow(label = "Stawka:", value = faktura.razemStawka)
            FakturaDetailsRow(label = "Podatek:", value = faktura.razemPodatek)
            FakturaDetailsRow(label = "Brutto:", value = faktura.razemBrutto)
            FakturaDetailsRow(label = "Waluta:", value = faktura.waluta)
            FakturaDetailsRow(label = "Forma Platnosci:", value = faktura.formaPlatnosci)
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
                FakturaProductDetails(
                    nazwaProduktu = product.nazwaProduktu,
                    jednostkaMiary = product.jednostkaMiary.toString(),
                    ilosc = product.ilosc.toString(),
                    wartoscNetto = product.wartoscNetto,
                    stawkaVat = product.stawkaVat,
                    podatekVat = product.podatekVat,
                    brutto = product.brutto,
                )
            }
        }

    }
}

@Composable
fun FakturaDetailsRow(label: String, value: String?) {
    val newValue = value ?: "null"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(text = newValue, fontSize = 16.sp)
    }
}

@Composable
fun FakturaProductDetails(nazwaProduktu: String,
                          jednostkaMiary: String,
                          ilosc: String,
                          wartoscNetto: String,
                          stawkaVat: String,
                          podatekVat: String,
                          brutto: String) {
    Column(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Text(text = "nazwaProduktu: $nazwaProduktu", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(text = "jednostkaMiary: $jednostkaMiary", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(text = "ilosc: $ilosc", fontSize = 14.sp)
        Text(text = "wartoscNetto: $wartoscNetto", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(text = "stawkaVat: $stawkaVat", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(text = "podatekVat: $podatekVat", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(text = "brutto: $brutto", fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}
