package com.example.photoapp.ui.raportFiskalny.Details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.photoapp.database.data.ProduktRaportFiskalny
import com.example.photoapp.database.data.RaportFiskalny
import com.example.photoapp.genericComposables.GenericEditableDetailsScreen
import com.example.photoapp.utils.convertMillisToDate
import com.example.photoapp.utils.formatDate

@Composable
fun RaportFiskalnyDetailsScreen(
    raportFiskalny: RaportFiskalny?,
    leaveDetailsScreen: () -> Unit,
    navigateToCameraAndSetRF: () -> Unit,
    viewModel: RaportFiskalnyViewModel = hiltViewModel()
) {
    val actualProdukty by viewModel.actualProdukty.collectAsState()
    val editingProdukty by viewModel.editedProdukty.collectAsState()

    val raport by viewModel.actualRaport.collectAsState()

    var refreshKey by remember { mutableStateOf(0) }

    LaunchedEffect(raportFiskalny) {
        if (raportFiskalny != null) {
            viewModel.getRaportByID(raportFiskalny.id) {
                viewModel.setRaport(it)
                viewModel.loadProducts(it)
            }
        }
    }

    key(refreshKey) {
        GenericEditableDetailsScreen(
            title = "Raport Fiskalny",
            leaveDetailsScreen = leaveDetailsScreen,
            navigateToCameraAndSetRF = navigateToCameraAndSetRF,
            actualItems = actualProdukty,
            editingItems = editingProdukty,
            editCanceled = { viewModel.loadProducts(raport!!)
                           refreshKey++},
            editAccepted = {
                viewModel.updateToDBProductsAndRaports{
                    viewModel.loadProducts(raport!!)
                    refreshKey++
                }
                           },
            onAddItem = { plu, qty ->
                viewModel.addOneProduct(raport!!.id, plu, qty) {
                    viewModel.loadProducts(raport!!)
                }
            },
            onEditItem = { index, item ->
                viewModel.updateEditedProductTemp(index, item) {}
            },
            onDeleteItem = { item ->
                viewModel.deleteProduct(item) {
                    viewModel.loadProducts(raport!!)
                }
            },
            enableDatePicker = true,
            initialDate = formatDate(raportFiskalny?.dataDodania?.time),
            onDateSelected = { it ->
                val newDate = convertMillisToDate(it)
                viewModel.updateEditedRaportTemp(raport!!.copy(dataDodania = newDate)) {}
            },
            renderEditableItem = { product, onEdit ->
                RaportFiskalnyProductDetailsEditing(
                    produkt = product,
                    onEdit = onEdit
                )
            },
            renderReadonlyItem = { product ->
                RaportFiskalnyProductDetailsDefault(
                    nrPLU = product.nrPLU,
                    quantity = product.ilosc.toString()
                )
            }
        )
    }
}

@Composable
fun RaportFiskalnyProductDetailsEditing(produkt: ProduktRaportFiskalny, onEdit: (ProduktRaportFiskalny) -> Unit) {
    Column(
        modifier = Modifier.padding(start = 5.dp, end = 10.dp)
    ) {
        var textPLU by remember { mutableStateOf(produkt.nrPLU) }
        var textQuantity by remember { mutableStateOf((produkt.ilosc).toString()) }
        Row(modifier = Modifier.padding(all = 5.dp)){
            Row {
                OutlinedTextField(
                    modifier = Modifier
                        .padding(all = 5.dp)
                        .weight(1f),
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
                    modifier = Modifier
                        .padding(all = 5.dp)
                        .weight(1f),
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
