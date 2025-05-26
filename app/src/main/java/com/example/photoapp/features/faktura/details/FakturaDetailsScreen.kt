package com.example.photoapp.features.faktura.details

import androidx.compose.foundation.layout.*
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.photoapp.features.faktura.data.Faktura
import com.example.photoapp.features.faktura.data.ProduktFaktura
import com.example.photoapp.genericComposables.GenericEditableDetailsScreen
import com.example.photoapp.utils.convertMillisToDate
import com.example.photoapp.utils.formatDate

@Composable
fun FakturaDetailsScreen(
    faktura: Faktura?,
    leaveDetailsScreen: () -> Unit,
    navigateToCameraAndSetRF: () -> Unit,
    viewModel: FakturaDetailsScreenViewModel = hiltViewModel()
) {
    val actualFaktura by viewModel.actualFaktura.collectAsState()
    val actualProdukty by viewModel.actualProdukty.collectAsState()
    val editedProdukty by viewModel.editedProdukty.collectAsState()

    var refreshKey by remember { mutableStateOf(0) }

    LaunchedEffect(faktura) {
        faktura?.let {
            viewModel.setFaktura(it)
            viewModel.loadProducts(it)
        }
    }

    key(refreshKey) {
        GenericEditableDetailsScreen(
            title = "Faktura",
            leaveDetailsScreen = leaveDetailsScreen,
            navigateToCameraAndSetRF = navigateToCameraAndSetRF,
            actualItems = actualProdukty,
            editingItems = editedProdukty,
            editCanceled = {
                actualFaktura?.let { viewModel.loadProducts(it) }
                refreshKey++
            },
            editAccepted = {
                viewModel.updateToDBProducts {
                    actualFaktura?.let { viewModel.loadProducts(it) }
                    refreshKey++
                }
            },
            onAddItem = { name, qty ->
                actualFaktura?.let {
                    viewModel.addOneProduct(it.id, name, qty) {
                        viewModel.loadProducts(it)
                    }
                }
            },
            onEditItem = { index, produkt ->
                viewModel.updateEditedProductTemp(index, produkt)
            },
            onDeleteItem = { produkt ->
                viewModel.deleteProduct(produkt) {
                    actualFaktura?.let { viewModel.loadProducts(it) }
                }
            },
            enableDatePicker = true,
            initialDate = formatDate(faktura?.dataWystawienia?.time),
            onDateSelected = { millis ->
                viewModel.updateEditedFakturaTemp(convertMillisToDate(millis))
            },
            renderEditableItem = { produkt, onEdit ->
                FakturaProductEditor(produkt = produkt, onEdit = onEdit)
            },
            renderReadonlyItem = { produkt ->
                FakturaProductReadonly(produkt)
            }
        )
    }
}

@Composable
fun FakturaProductEditor(produkt: ProduktFaktura, onEdit: (ProduktFaktura) -> Unit) {
    var name by remember { mutableStateOf(produkt.nazwaProduktu) }
    var qty by remember { mutableStateOf(produkt.ilosc) }

    Column(modifier = Modifier.padding(8.dp)) {
        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                onEdit(produkt.copy(nazwaProduktu = it))
            },
            label = { Text("Nazwa") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = qty.toString(),
            onValueChange = {
                qty = it
                onEdit(produkt.copy(ilosc = it))
            },
            label = { Text("Ilość") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun FakturaProductReadonly(produkt: ProduktFaktura) {
    Column(modifier = Modifier.padding(8.dp)) {
        FakturaDetailsRow("Nazwa", produkt.nazwaProduktu)
        FakturaDetailsRow("Ilość", produkt.ilosc)
        FakturaDetailsRow("Netto", produkt.wartoscNetto)
        FakturaDetailsRow("VAT", produkt.stawkaVat)
        FakturaDetailsRow("Podatek VAT", produkt.podatekVat)
        FakturaDetailsRow("Brutto", produkt.brutto)
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