package com.example.photoapp.ui.RaportFiskalny.Details.composables.Default

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.photoapp.database.data.ProduktRaportFiskalny
import com.example.photoapp.database.data.RaportFiskalny
import com.example.photoapp.ui.RaportFiskalny.Details.RaportFiskalnyViewModel
import com.example.photoapp.ui.RaportFiskalny.Details.composables.IsEditing.RaportFiskalnyProductDetailsEditing

//Default\DetailsContent.kt
@Composable
fun RFDefaultDetailsContent(
    innerPadding: PaddingValues,
    raportFiskalny: RaportFiskalny,
    produkty: List<ProduktRaportFiskalny>,
    viewModel: RaportFiskalnyViewModel = hiltViewModel()
) {
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
                RaportFiskalnyProductDetailsDefault(
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
fun RaportFiskalnyProductDetailsDefault(nrPLU: String, quantity: String) {
    Column(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Row{
            Text(text = "nrPLU:    ", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = nrPLU, fontSize = 16.sp)
        }
        Row{
            Text(text = "ilosc:    ", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = quantity, fontSize = 14.sp)
        }

    }
}