package com.example.photoapp.features.faktura.ui.details

import android.R.attr.name
import androidx.compose.foundation.layout.*
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.photoapp.core.components.DefaultAddItemDialog
import com.example.photoapp.features.faktura.data.Faktura
import com.example.photoapp.features.faktura.data.ProduktFaktura
import com.example.photoapp.core.components.GenericEditableDetailsScreen
import com.example.photoapp.core.utils.convertMillisToDate
import com.example.photoapp.core.utils.formatDate
import com.example.photoapp.features.paragon.data.ProduktParagon

@Composable
fun FakturaDetailsScreen(
    faktura: Faktura?,
    leaveDetailsScreen: () -> Unit,
    navigateToCameraAndSetRF: () -> Unit,
    viewModel: FakturaDetailsViewModel = hiltViewModel()
) {
    val actualProdukty by viewModel.actualProdukty.collectAsState()
    val editingProdukty by viewModel.editedProdukty.collectAsState()

    val actualFaktura by viewModel.actualFaktura.collectAsState()

    var refreshKey by remember { mutableStateOf(0) }

    LaunchedEffect(faktura) {
        if (faktura != null) {
            viewModel.getFakturaByID(faktura.id) { f ->
                viewModel.setFaktura(f)
                viewModel.loadProducts(f)
            }
        }
    }

    key(refreshKey) {
        GenericEditableDetailsScreen(
            title = "Faktura",
            leaveDetailsScreen = leaveDetailsScreen,
            navigateToCameraAndSetRF = navigateToCameraAndSetRF,
            actualItems = actualProdukty,
            editingItems = editingProdukty,
            editCanceled = {
                viewModel.editingFailed()
                refreshKey++
            },
            editAccepted = {
                viewModel.editingSuccess()
                refreshKey++
            },
            onAddItem = { produkt ->
                viewModel.addOneProduct(fakturaId = faktura!!.id, nazwaProduktu = produkt.nazwaProduktu, ilosc = produkt.ilosc.toString()) {
                    viewModel.loadProducts(faktura)
                }

            },
            onEditItem = { index, produkt ->
                viewModel.updateEditedProductTemp(index, produkt) {}
            },
            onDeleteItem = { produkt ->
                viewModel.deleteProduct(produkt) {
                    viewModel.loadProducts(faktura!!)
                }
            },
            enableDatePicker = true,
            initialDate = formatDate(faktura?.dataWystawienia?.time),
            onDateSelected = { millis ->
                val newDate = convertMillisToDate(millis)
                viewModel.updateEditedFakturaTemp(faktura!!.copy(dataWystawienia = newDate)) {}

            },
            renderEditableItem = { produkt, onEdit ->
                FakturaProductReadonly(produkt = produkt)
            },
            renderReadonlyItem = { produkt ->
                FakturaProductReadonly(produkt)
            },
            renderAddItemDialog = { onAdd, onDismiss ->
                val newNazwaProduktu = remember { mutableStateOf("")}
                val newJednostkaMiary = remember { mutableStateOf("")}
                val newIlosc = remember { mutableStateOf("")}
                val newCenaNetto = remember { mutableStateOf("")}
                val newWartoscNetto = remember { mutableStateOf("")}
                val newWartoscBrutto = remember { mutableStateOf("")}
                val newStawkaVat = remember { mutableStateOf("")}

                DefaultAddItemDialog(
                    title = "Dodaj Produkt",
                    fields = listOf(
                        "Nazwa" to newNazwaProduktu,
                        "Jednostka Miary" to newJednostkaMiary,
                        "Ilosc" to newIlosc,
                        "Cena Netto" to newCenaNetto,
                        "Wartosc Netto" to newWartoscNetto,
                        "Wartosc Brutto" to newWartoscBrutto,
                        "Stawka VAT" to newStawkaVat
                    ),
                    onBuildItem = {
                        ProduktFaktura(
                            fakturaId = faktura!!.id,
                            nazwaProduktu = newNazwaProduktu.value,
                            jednostkaMiary = newJednostkaMiary.value,
                            ilosc = newIlosc.value,
                            cenaNetto = newCenaNetto.value,
                            wartoscNetto = newWartoscNetto.value,
                            wartoscBrutto = newWartoscBrutto.value,
                            stawkaVat = newStawkaVat.value,
                        )
                    },
                    onAction = onAdd,
                    onDismiss = onDismiss
                )
            },
            renderEditItemDialog = { produkt, onEdit, onDismiss ->
                val newNazwaProduktu = remember { mutableStateOf(produkt.nazwaProduktu)}
                val newJednostkaMiary = remember { mutableStateOf(produkt.jednostkaMiary!!)}
                val newIlosc = remember { mutableStateOf(produkt.ilosc)}
                val newCenaNetto = remember { mutableStateOf(produkt.cenaNetto)}
                val newWartoscNetto = remember { mutableStateOf(produkt.wartoscNetto)}
                val newWartoscBrutto = remember { mutableStateOf(produkt.wartoscBrutto)}
                val newStawkaVat = remember { mutableStateOf(produkt.stawkaVat)}

                DefaultAddItemDialog(
                    title = "Edytuj Produkt",
                    fields = listOf(
                        "Nazwa" to newNazwaProduktu,
                        "Jednostka Miary" to newJednostkaMiary,
                        "Ilosc" to newIlosc,
                        "Cena Netto" to newCenaNetto,
                        "Wartosc Netto" to newWartoscNetto,
                        "Wartosc Brutto" to newWartoscBrutto,
                        "Stawka VAT" to newStawkaVat
                    ),
                    onBuildItem = {
                        ProduktFaktura(
                            fakturaId = faktura!!.id,
                            nazwaProduktu = newNazwaProduktu.value,
                            jednostkaMiary = newJednostkaMiary.value,
                            ilosc = newIlosc.value,
                            cenaNetto = newCenaNetto.value,
                            wartoscNetto = newWartoscNetto.value,
                            wartoscBrutto = newWartoscBrutto.value,
                            stawkaVat = newStawkaVat.value,
                        )
                    },
                    onAction = onEdit,
                    onDismiss = onDismiss
                )
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
        FakturaDetailsRow("Jednostka miary", produkt.jednostkaMiary)
        FakturaDetailsRow("Ilość", produkt.ilosc)
        FakturaDetailsRow("Cena Netto", produkt.cenaNetto)
        FakturaDetailsRow("Wartosc Netto", produkt.wartoscNetto)
        FakturaDetailsRow("Wartosc Brutto", produkt.wartoscBrutto)
        FakturaDetailsRow("Stawka VAT", produkt.stawkaVat)
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