package com.example.photoapp.ui.RaportFiskalny.Details.composables.IsEditing

import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.photoapp.database.data.ProduktRaportFiskalny
import com.example.photoapp.database.data.RaportFiskalny
import com.example.photoapp.ui.RaportFiskalny.Details.RaportFiskalnyViewModel
import kotlinx.coroutines.delay

@SuppressLint("RememberReturnType")
@Composable
fun RFEditingDetailsContent(
    innerPadding: PaddingValues,
    raportFiskalny: RaportFiskalny,
    produkty: List<ProduktRaportFiskalny>,
    viewModel: RaportFiskalnyViewModel
) {

    Log.i("Dolan", "SHOWING PRODUKTY LIST ${produkty}")
    Log.i("Dolan", "SHOWING PRODUKTY LIST ${viewModel._editedProducts}")

    LazyColumn(
        contentPadding = innerPadding,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxSize()
    ) {
        item {
            RaportFiskalnyDetailsRow(
                label = "Z dnia:",
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
                            modifier = Modifier.padding(start = 10.dp, end = 5.dp).align(Alignment.CenterVertically),
                            onClick = {
                            viewModel.deleteProduct(product) {
                                viewModel.loadProducts(raportFiskalnyId = raportFiskalny.id)
                            }

                        }) {
                            Icon(Icons.Default.Clear, contentDescription = "Delete Product")
                        }
                        RaportFiskalnyProductDetails(
                            produkt = product,
                            onEdit = { updatedProdukt ->
//                                viewModel._editedProducts[index] = updatedProdukt
                                viewModel.updateEditedProduct(index, updatedProdukt) {
//                                    viewModel.loadProducts(raportFiskalnyId = raportFiskalny.id)
                                    Log.i("Dolan", "Updating Product")
                                }
                                Log.i("Dolan", "SHOWING PRODUKTY LIST ${produkty}")
                                Log.i("Dolan", "SHOWING PRODUKTY LIST ${viewModel._editedProducts}")
                            }
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
fun RaportFiskalnyProductDetails(produkt: ProduktRaportFiskalny, onEdit: (ProduktRaportFiskalny) -> Unit) {
    Column(
        modifier = Modifier.padding(start = 5.dp, end = 10.dp)
    ) {
//        Row(modifier = Modifier.padding(all = 5.dp)){
//            Text(text = "nrPLU:    ", fontWeight = FontWeight.Bold, fontSize = 16.sp)
//            Text(text = nrPLU, fontSize = 16.sp)
//        }
//        Row(modifier = Modifier.padding(all = 5.dp)){
//            Text(text = "ilosc:    ", fontWeight = FontWeight.Bold, fontSize = 16.sp)
//            Text(text = quantity, fontSize = 14.sp)
//        }
        var textPLU by remember { mutableStateOf(produkt.nrPLU) }
        var textQuantity by remember { mutableStateOf((produkt.ilosc).toString()) }
        Row(modifier = Modifier.padding(all = 5.dp)){
            Row {
                OutlinedTextField(
                    modifier = Modifier.padding(all = 5.dp).weight(1f),
                    value = textPLU,
                    onValueChange = {
                        textPLU = it
                        onEdit(
                            ProduktRaportFiskalny(
                                id = produkt.id,
                                raportFiskalnyId = produkt.raportFiskalnyId,
                                nrPLU = it,
                                ilosc = textQuantity
                            )
                        )
                    },
                    label = { Text("nrPLU") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
                OutlinedTextField(
                    modifier = Modifier.padding(all = 5.dp).weight(1f),
                    value = textQuantity,
                    onValueChange = {
                        textQuantity = it
                        onEdit(
                            ProduktRaportFiskalny(
                                id = produkt.id,
                                raportFiskalnyId = produkt.raportFiskalnyId,
                                nrPLU = textPLU,
                                ilosc = it
                            )
                        )
                    },
                    label = { Text("ilość") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
            }
        }
    }
}