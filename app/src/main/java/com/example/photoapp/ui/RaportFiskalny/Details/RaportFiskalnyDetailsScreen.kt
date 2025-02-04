package com.example.photoapp.ui.RaportFiskalny.Details


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.photoapp.database.data.RaportFiskalny
import com.example.photoapp.ui.RaportFiskalny.Details.composables.Default.RFDefaultDetailsContent
import com.example.photoapp.ui.RaportFiskalny.Details.composables.Default.RFDefaultTopAppBar
import com.example.photoapp.ui.RaportFiskalny.Details.composables.IsEditing.RFEditingDetailsContent
import com.example.photoapp.ui.RaportFiskalny.Details.composables.IsEditing.RFEditingTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RaportFiskalnyDetailsScreen(
    navController: NavHostController,
    raportFiskalny: RaportFiskalny?,
    navigateToCameraAndSetRF: (String) -> Unit,
    viewModel: RaportFiskalnyViewModel = hiltViewModel()
) {

    var isCircularIndicatorShowing by remember { mutableStateOf(false) }

    var isExpandedAdding by remember { mutableStateOf(false) }

    val produkty by viewModel.produkty.collectAsState()

    LaunchedEffect(raportFiskalny) {
        raportFiskalny?.id?.let { viewModel.loadProducts(it) }
    }

    var isEditing by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            if (isEditing == false) {
                RFDefaultTopAppBar(
                    navController = navController,
                    isCircularIndicatorShowing={ trueOrFalse ->
                        isCircularIndicatorShowing = trueOrFalse},
                    changeEditingState = { trueOrFalse ->
                        isEditing = trueOrFalse},
                    produkty = produkty,
                    navigateToCameraAndSetRF = navigateToCameraAndSetRF
                )
            } else {
                RFEditingTopAppBar(
                    navController = navController,
                    changeEditingState = { trueOrFalse ->
                        isEditing = trueOrFalse},
                    produkty = produkty,
                    onAddingProduct = { isExpandedAdding = true }
                )
            }
        }
    ) { innerPadding ->
        if (raportFiskalny != null) {
            if (isEditing == false) {
                RFDefaultDetailsContent(
                    innerPadding = innerPadding,
                    raportFiskalny = raportFiskalny,
                    produkty = produkty
                )
            } else {
                RFEditingDetailsContent(
                    innerPadding = innerPadding,
                    raportFiskalny = raportFiskalny,
                    produkty = produkty
                )
            }
        }

        if (isCircularIndicatorShowing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.5f),
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (isExpandedAdding) {
            var nrPLU by remember { mutableStateOf("") }
            var ilosc by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = {
                    isExpandedAdding = false
                },
                title = { Text(text = "Add New Product") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = nrPLU,
                            onValueChange = { nrPLU = it },
                            label = { Text("PLU Number") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = ilosc,
                            onValueChange = { ilosc = it },
                            label = { Text("Quantity") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                        )
                    }
                },
                confirmButton = {
                    Button (
                        onClick = {
                            viewModel.addOneProduct(
                                rfId = raportFiskalny!!.id,
                                nrPLU = nrPLU,
                                ilosc = ilosc,
                            ) {
                                isExpandedAdding = false
                                viewModel.loadProducts(raportFiskalnyId = raportFiskalny.id)
                            }
                        },
                    ){
                        Text("Dodaj")
                    }
                },
                dismissButton = {
                    Button(onClick = { isExpandedAdding = false }) {
                        Text("Usuń")
                    }
                }
            )
        }
    }
}

