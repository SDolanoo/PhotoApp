package com.example.photoapp.features.selector.presentation.selector.produkt.details

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.photoapp.features.produkt.validation.ProduktValidationViewModel
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.photoapp.features.produkt.composables.form.OneProduktForm
import com.example.photoapp.features.produkt.composables.readOnly.OneProduktReadOnly
import com.example.photoapp.features.produkt.data.Produkt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProduktDetailsScreen(
    produkt: Produkt,
    leaveDetailsScreen: () -> Unit,
    viewModel: ProduktDetailsViewModel = hiltViewModel(),
    validationVM: ProduktValidationViewModel = hiltViewModel()
) {
    val actualProdukt by viewModel.actualProdukt.collectAsState()
    val editedProdukt by viewModel.editedProdukt.collectAsState()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    var isEditing by remember { mutableStateOf(false) }

    val validationResult by validationVM.validationResult.collectAsState()

    LaunchedEffect(produkt) {
        viewModel.getProdukt(produkt) { p ->
            p?.let {
                viewModel.setProdukt(it)
                viewModel.loadProdukt(it)
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if (isEditing) "Edytuj Produkt" else "Szczegóły Produktu",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isEditing) {
                            isEditing = false
                            viewModel.editingFailed()
                        } else {
                            leaveDetailsScreen()
                        }
                    }) {
                        Icon(
                            imageVector = if (isEditing) Icons.Default.Close else Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (isEditing) {
                        IconButton(onClick = {
                            validationVM.validate(
                                productName = editedProdukt.nazwaProduktu,
                                productPrice = editedProdukt.cenaNetto
                            ) { isValid ->
                                if (isValid) {
                                    isEditing = false
                                    viewModel.editingSuccess()
                                }
                            }
                        }) {
                            Icon(Icons.Default.Check, contentDescription = "Save")
                        }
                    } else {
                        IconButton(onClick = { isEditing = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 8.dp)
        ) {
            item {
                Text(
                    text = "Produkt",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                validationResult.fieldErrors["PRODUCT_NAME"]?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }

                validationResult.fieldErrors["PRODUCT_PRICE"]?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }

                if (isEditing) {
                    key(editedProdukt.id) {
                        val newNazwa = remember { mutableStateOf(editedProdukt.nazwaProduktu) }
                        val newJednostka = remember { mutableStateOf(editedProdukt.jednostkaMiary) }
                        val newCena = remember { mutableStateOf(editedProdukt.cenaNetto) }
                        val newVat = remember { mutableStateOf(editedProdukt.stawkaVat) }

                        OneProduktForm(
                            modifier = Modifier,
                            fields = listOf(
                                "Nazwa produktu" to newNazwa,
                                "Jednostka miary" to newJednostka,
                                "Cena netto" to newCena,
                                "Stawka VAT" to newVat,
                            ),
                            onEdit = {
                                viewModel.updateEditedProduktTemp(
                                    Produkt(
                                        id = editedProdukt.id,
                                        nazwaProduktu = newNazwa.value,
                                        jednostkaMiary = newJednostka.value,
                                        cenaNetto = newCena.value,
                                        stawkaVat = newVat.value,
                                    )
                                ) {}
                            },
                            onButtonClick = { /* optional */ }
                        )
                    }
                } else {
                    OneProduktReadOnly(
                        modifier = Modifier,
                        fields = listOf(
                            actualProdukt.nazwaProduktu,
                            actualProdukt.jednostkaMiary,
                            actualProdukt.cenaNetto,
                            actualProdukt.stawkaVat
                        )
                    )
                }
            }
        }
    }
}