package com.example.photoapp.ui.RaportFiskalny.Details


import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.photoapp.database.data.RaportFiskalny
import com.example.photoapp.ui.RaportFiskalny.Details.composables.Default.RaportFiskalnyProductDetailsDefault
import com.example.photoapp.ui.RaportFiskalny.Details.composables.IsEditing.RaportFiskalnyProductDetailsEditing
import com.example.photoapp.ui.RaportFiskalny.Screen.GenericEditableDetailsScreen
import com.example.photoapp.utils.convertMillisToDate
import com.example.photoapp.utils.formatDate

@Composable
fun RaportFiskalnyDetailsScreen(
    navController: NavHostController,
    raportFiskalny: RaportFiskalny?,
    navigateToCameraAndSetRF: (String) -> Unit,
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
            onExport = {
                // Pass your Excel export logic here
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
