package com.example.photoapp.ui.RaportFiskalny.Details.composables.IsEditing

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavHostController
import com.example.photoapp.database.data.ProduktRaportFiskalny
import com.example.photoapp.ui.RaportFiskalny.Details.RaportFiskalnyViewModel

//IsEditing\topAppBar.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RFEditingTopAppBar(
    navController: NavHostController,
    changeEditingState: (Boolean) -> Unit,
    produkty: List<ProduktRaportFiskalny>,
    onAddingProduct: () -> Unit,
    viewModel: RaportFiskalnyViewModel
    ) {

    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text(
                "Edytowanie",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = { changeEditingState(false) }) {
                Icon(Icons.Default.Clear, contentDescription = "Cancel Action")
            }
        },
        actions = {
            IconButton(onClick = { onAddingProduct() }) {
                Icon(Icons.Default.Add, contentDescription = "Add one new Field")
            }
            IconButton(onClick = {
                viewModel.updateAllProductsAndRaports {
                    changeEditingState(false)
                }
//                produkty.forEach { produkt ->
//                    viewModel.updateProduct(product = produkt) {
//                        Log.i("Dolan", "Updated product $produkt")
//                    }
//                }
//                changeEditingState(false)
            }) {
                Icon(Icons.Default.Check, contentDescription = "Confirm Action")
            }
        },)
}