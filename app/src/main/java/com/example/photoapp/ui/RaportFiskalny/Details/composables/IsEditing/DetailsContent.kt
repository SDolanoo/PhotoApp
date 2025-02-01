package com.example.photoapp.ui.RaportFiskalny.Details.composables.IsEditing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.photoapp.database.data.ProduktRaportFiskalny
import com.example.photoapp.database.data.RaportFiskalny
import com.example.photoapp.ui.RaportFiskalny.Details.RaportFiskalnyViewModel
import kotlinx.coroutines.delay

@Composable
fun RFEditingDetailsContent(
    innerPadding: PaddingValues,
    raportFiskalny: RaportFiskalny,
    produkty: List<ProduktRaportFiskalny>,
    viewModel: RaportFiskalnyViewModel
) {
    LazyColumn(
        contentPadding = innerPadding,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxSize()
    ) {
        item {
            RaportFiskalnyDetailsRow(
                label = "Data dodania:",
                value = viewModel.formatDate(raportFiskalny.dataDodania?.time)
            )
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
                Column {
                    if (index > 0) {
                        HorizontalDivider(thickness = 1.dp)
                    }
                    Row(modifier = Modifier.fillMaxWidth()) {
                        IconButton(
                            modifier = Modifier.padding(start = 10.dp, end = 5.dp),
                            onClick = {
                            viewModel.deleteProduct(product) {
                                viewModel.loadProducts(raportFiskalnyId = raportFiskalny.id)
                            }

                        }) {
                            Icon(Icons.Default.Clear, contentDescription = "Delete Product")
                        }
                        RaportFiskalnyProductDetails(
                            nrPLU = product.nrPLU,
                            quantity = product.ilosc.toString(),
                        )
                    }
                    if (index == produkty.size - 1) {
                        Spacer(modifier = Modifier.height(5.dp))
                    }
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
        modifier = Modifier.padding(start = 5.dp, end = 10.dp)
    ) {
        Row(modifier = Modifier.padding(all = 5.dp)){
            Text(text = "nrPLU:    ", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = nrPLU, fontSize = 16.sp)
        }
        Row(modifier = Modifier.padding(all = 5.dp)){
            Text(text = "ilosc:    ", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = quantity, fontSize = 14.sp)
        }

    }
}