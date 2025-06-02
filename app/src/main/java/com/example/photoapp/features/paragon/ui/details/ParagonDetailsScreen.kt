package com.example.photoapp.features.paragon.ui.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.photoapp.core.components.DefaultAddItemDialog
import com.example.photoapp.features.paragon.data.Paragon
import com.example.photoapp.features.paragon.data.ProduktParagon
import com.example.photoapp.core.components.DetailsRow
import com.example.photoapp.core.components.GenericEditableDetailsScreen
import com.example.photoapp.core.utils.convertMillisToDate
import com.example.photoapp.core.utils.formatDate

@Composable
fun ParagonDetailsScreen(
    paragon: Paragon?,
    leaveDetailsScreen: () -> Unit,
    navigateToCameraAndSetRF: () -> Unit,
    viewModel: ParagonDetailsScreenViewModel = hiltViewModel()
) {
    val actualProdukty by viewModel.actualProdukty.collectAsState()
    val editingProdukty by viewModel.editedProdukty.collectAsState()

    val actualParagon by viewModel.actualParagon.collectAsState()

    var refreshKey by remember { mutableStateOf(0) }

    LaunchedEffect(paragon) {
        if (paragon != null) {
            viewModel.getParagonByID(paragon.id) { p ->
                viewModel.setParagon(p)
                viewModel.loadProducts(p)
            }
        }
    }

    key(refreshKey) {
        GenericEditableDetailsScreen(
            title = "Szczegóły Paragonu",
            leaveDetailsScreen = leaveDetailsScreen,
            navigateToCameraAndSetRF = navigateToCameraAndSetRF,
            actualItems = actualProdukty,
            editingItems = editingProdukty,
            editCanceled = {
                viewModel.loadProducts(actualParagon!!)
                refreshKey++
            },
            editAccepted = {
                viewModel.updateToDBProductsAndParagon {
                    viewModel.loadProducts(actualParagon!!)
                    refreshKey++
                }

            },
            onAddItem = { produkt ->
                viewModel.addOneProduct(paragonId = paragon!!.id, nazwaProduktu = produkt.nazwaProduktu, ilosc = produkt.ilosc.toString()) {
                    viewModel.loadProducts(paragon)
                }
            },
            onEditItem = { index, produkt ->
                viewModel.updateEditedProductTemp(index, produkt) {}
            },
            onDeleteItem = { produkt ->
                viewModel.deleteProduct(produkt) {
                    viewModel.loadProducts(paragon!!)
                }
            },
            enableDatePicker = true,
            initialDate = formatDate(actualParagon?.dataZakupu?.time?: paragon?.dataZakupu?.time),
            onDateSelected = { millis ->
                val newDate = convertMillisToDate(millis)
                viewModel.updateEditedParagonTemp(actualParagon!!.copy(dataZakupu = newDate)) {}
            },
            renderEditableItem = { produkt, onEdit ->
                ParagonProductDetailsEditing(
                    produkt = produkt,
                    onEdit = onEdit
                )
            },
            renderReadonlyItem = { produkt ->
                ParagonProductDetailsReadonly(
                    nazwaProduktu = produkt.nazwaProduktu,
                    ilosc = produkt.ilosc.toString(),
                    cenaSuma = produkt.cenaSuma.toString()
                )
            },
            renderAddItemDialog = { onAdd, onDismiss ->
                val newNazwaProduktu = remember { mutableStateOf("")}
                val newIlosc = remember { mutableStateOf("")}
                val newCenaSuma = remember { mutableStateOf("")}
                val newKategoria = remember { mutableStateOf("")}

                DefaultAddItemDialog(
                    title = "Dodaj Produkt",
                    fields = listOf(
                        "Nazwa" to newNazwaProduktu,
                        "Ilość" to newIlosc,
                        "Cena" to newCenaSuma,
                        "Kategoria" to newKategoria
                    ),
                    kategorie = viewModel.getAllKategoria(),
                    onBuildItem = {
                        ProduktParagon(
                            paragonId = paragon!!.id,
                            nazwaProduktu = newNazwaProduktu.value,
                            ilosc = newIlosc.value.toInt(),
                            cenaSuma = newCenaSuma.value.toDouble(),
                            kategoriaId = newKategoria.value.toLong()
                        )
                    },
                    onAction = onAdd,
                    onDismiss = onDismiss
                )
            },
            renderEditItemDialog = { produkt, onEdit, onDismiss ->
                val newNazwaProduktu = remember { mutableStateOf(produkt.nazwaProduktu)}
                val newIlosc = remember { mutableStateOf(produkt.ilosc.toString())}
                val newCenaSuma = remember { mutableStateOf(produkt.cenaSuma.toString())}
                val initialKategoria = remember(produkt.kategoriaId) {
                    val kategoria = produkt.kategoriaId?.let { viewModel.getKategoriaById(it) }
                    mutableStateOf(kategoria?.id?.toString() ?: "")
                }

                DefaultAddItemDialog(
                    title = "Edytuj Produkt",
                    fields = listOf(
                        "Nazwa" to newNazwaProduktu,
                        "Ilość" to newIlosc,
                        "Cena" to newCenaSuma,
                        "Kategoria" to initialKategoria
                    ),
                    kategorie = viewModel.getAllKategoria(),
                    onBuildItem = {
                        ProduktParagon(
                            id = produkt.id,
                            paragonId = paragon!!.id,
                            nazwaProduktu = newNazwaProduktu.value,
                            ilosc = newIlosc.value.toInt(),
                            cenaSuma = newCenaSuma.value.toDouble(),
                            kategoriaId = viewModel.getKategoriaByName(initialKategoria.value)?.id
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
fun ParagonProductDetailsEditing(produkt: ProduktParagon, onEdit: (ProduktParagon) -> Unit) {
    var nazwaProduktu by remember { mutableStateOf(produkt.nazwaProduktu) }
    var ilosc by remember { mutableStateOf(produkt.ilosc.toString()) }

    Row(modifier = Modifier.padding(8.dp)) {
        OutlinedTextField(
            modifier = Modifier.weight(1f).padding(end = 8.dp),
            value = nazwaProduktu,
            onValueChange = {
                nazwaProduktu = it
                onEdit(produkt.copy(nazwaProduktu = it))
            },
            label = { Text("Nazwa") }
        )
        OutlinedTextField(
            modifier = Modifier.weight(1f),
            value = ilosc,
            onValueChange = {
                ilosc = it
                onEdit(
                    produkt.copy(ilosc = it.toIntOrNull() ?: 1)
                )
            },
            label = { Text("Ilość") }
        )
    }
}


@Composable
fun ParagonProductDetailsReadonly(nazwaProduktu: String, ilosc: String, cenaSuma: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        DetailsRow(label = "Produkt:", value = nazwaProduktu)
        DetailsRow(label = "Ilość:", value = ilosc)
        DetailsRow(label = "Cena:", value = cenaSuma)
    }
}
