package com.example.photoapp.features.selector.presentation.selector.sprzedawca.details

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
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
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.photoapp.features.sprzedawca.composables.form.SprzedawcaForm
import com.example.photoapp.features.sprzedawca.composables.readOnly.SprzedawcaReadOnly
import com.example.photoapp.features.sprzedawca.data.Sprzedawca
import com.example.photoapp.features.sprzedawca.validation.SprzedawcaValidationViewModel
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SprzedawcaDetailsScreen(
    sprzedawca: Sprzedawca,
    leaveDetailsScreen: () -> Unit,
    viewModel: SprzedawcaDetailsViewModel = hiltViewModel(),
    validationVM: SprzedawcaValidationViewModel = hiltViewModel()
) {
    val actualSprzedawca by viewModel.actualSprzedawca.collectAsState()
    val editedSprzedawca by viewModel.editedSprzedawca.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    var isEditing by remember { mutableStateOf(false) }
    val validationResult by validationVM.validationResult.collectAsState()

    LaunchedEffect(sprzedawca) {
        viewModel.getSprzedawca(sprzedawca) { o ->
            o?.let {
                viewModel.setSprzedawca(it)
                viewModel.loadSprzedawca(it)
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if (isEditing) "Edytuj Sprzedawcę" else "Szczegóły Sprzedawcy",
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
                                sellerName = editedSprzedawca.nazwa,
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
                        IconButton(onClick = {
                            isEditing = true
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 8.dp)
        ) {
            item {
                Text(
                    text = "Sprzedawca",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                validationResult.fieldErrors["BUYER_NAME"]?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }

                if (isEditing) {
                    key(editedSprzedawca.id) {
                        val newNazwa = remember { mutableStateOf(editedSprzedawca.nazwa) }
                        val newNIP = remember { mutableStateOf(editedSprzedawca.nip) }
                        val newAdres = remember { mutableStateOf(editedSprzedawca.adres) }
                        val newKodPocztowy = remember { mutableStateOf(editedSprzedawca.kodPocztowy) }
                        val newMiejscowosc = remember { mutableStateOf(editedSprzedawca.miejscowosc) }
                        val newKraj = remember { mutableStateOf(editedSprzedawca.kraj) }
                        val newOpis = remember { mutableStateOf(editedSprzedawca.opis) }
                        val newEmail = remember { mutableStateOf(editedSprzedawca.email) }
                        val newTelefon = remember { mutableStateOf(editedSprzedawca.telefon) }

                        SprzedawcaForm(
                            modifier = Modifier,
                            fields = listOf(
                                "Nazwa firmy" to newNazwa,
                                "NIP" to newNIP,
                                "Adres" to newAdres,
                                "Kod pocztowy" to newKodPocztowy,
                                "Miejscowość" to newMiejscowosc,
                                "Kraj" to newKraj,
                                "Opis" to newOpis,
                                "E-mail" to newEmail,
                                "Telefon" to newTelefon,
                            ),
                            onEdit = {
                                viewModel.updateEditedSprzedawcaTemp(
                                    Sprzedawca(
                                        id = editedSprzedawca.id,
                                        nazwa = newNazwa.value,
                                        nip = newNIP.value,
                                        adres = newAdres.value,
                                        kodPocztowy = newKodPocztowy.value,
                                        miejscowosc = newMiejscowosc.value,
                                        kraj = newKraj.value,
                                        opis = newOpis.value,
                                        email = newEmail.value,
                                        telefon = newTelefon.value
                                    )
                                ) {}
                            },
                            onButtonClick = { }
                        )
                    }
                } else {
                    SprzedawcaReadOnly(
                        modifier = Modifier,
                        fields = listOf(
                            actualSprzedawca.nazwa,
                            actualSprzedawca.nip,
                            actualSprzedawca.adres,
                            actualSprzedawca.kodPocztowy,
                            actualSprzedawca.miejscowosc,
                            actualSprzedawca.kraj,
                            actualSprzedawca.opis,
                            actualSprzedawca.email,
                            actualSprzedawca.telefon,
                        )
                    )
                }
            }
        }
    }
}
