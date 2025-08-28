package com.example.photoapp.features.selector.presentation.selector.odbiorca.details

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.photoapp.features.odbiorca.composables.form.OdbiorcaForm
import com.example.photoapp.features.odbiorca.composables.readOnly.OdbiorcaReadOnly
import com.example.photoapp.features.odbiorca.data.Odbiorca
import com.example.photoapp.features.odbiorca.validation.OdbiorcaValidationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OdbiorcaDetailsScreen(
    odbiorca: Odbiorca,
    leaveDetailsScreen: () -> Unit,
    viewModel: OdbiorcaDetailsViewModel = hiltViewModel(),
    validationVM: OdbiorcaValidationViewModel = hiltViewModel()
) {
    val actualOdbiorca  by viewModel.actualOdbiorca.collectAsState()
    val editedOdbiorca  by viewModel.editedOdbiorca.collectAsState()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    var isEditing by remember { mutableStateOf(false) }

    val validationResult by validationVM.validationResult.collectAsState()

    LaunchedEffect(odbiorca) {
        viewModel.getOdbiorca(odbiorca) { o ->
            viewModel.setOdbiorca(o)
            Log.i("Dolan", "loading products")
            viewModel.loadProducts(o)
            Log.i("Dolan", "loading products succesfuly")
        }
    }

    BackHandler {
        if (isEditing) {
            isEditing = false
            viewModel.editingFailed()
        } else {
            leaveDetailsScreen()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer),
                title = {
                    Text(
                        if (isEditing) "Edytuj Odbiorce" else "Szczegóły Odbiorca",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    if (isEditing) {
                        IconButton(onClick = {
                            isEditing = false
                            viewModel.editingFailed()
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Back")
                        }
                    } else {
                        IconButton(onClick = {
                            leaveDetailsScreen()
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }

                },
                actions = {
                    if (isEditing) {
                        IconButton(onClick = {
                            validationVM.validate(
                                buyerName = editedOdbiorca.nazwa,
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
        LazyColumn (modifier = Modifier
            .padding(innerPadding)
            .padding(horizontal = 8.dp)) {

            item {

                if (isEditing) {
                    Text(
                        text = "Odbiorca",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }


                validationResult.fieldErrors["BUYER_NAME"]?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }

                if (isEditing) {
                    key(editedOdbiorca.id) {
                        val newNazwa = remember { mutableStateOf(editedOdbiorca.nazwa) }
                        val newNIP = remember { mutableStateOf(editedOdbiorca.nip) }
                        val newAdres = remember { mutableStateOf(editedOdbiorca.adres) }
                        val newKodPocztowy = remember { mutableStateOf(editedOdbiorca.kodPocztowy) }
                        val newMiejscowosc = remember { mutableStateOf(editedOdbiorca.miejscowosc) }
                        val newKraj = remember { mutableStateOf(editedOdbiorca.kraj) }
                        val newOpis = remember { mutableStateOf(editedOdbiorca.opis) }
                        val newEmail = remember { mutableStateOf(editedOdbiorca.email) }
                        val newTelefon = remember { mutableStateOf(editedOdbiorca.telefon) }
                        OdbiorcaForm(
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
                                viewModel.updateEditedOdbiorcaTemp(
                                    Odbiorca(
                                        id = editedOdbiorca.id,
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
                            onButtonClick = {

                            }
                        )
                    }
                } else {
                    OdbiorcaReadOnly(
                        modifier = Modifier,
                        fields = listOf(
                            actualOdbiorca.nazwa,
                            actualOdbiorca.nip,
                            actualOdbiorca.adres,
                            actualOdbiorca.kodPocztowy,
                            actualOdbiorca.miejscowosc,
                            actualOdbiorca.kraj,
                            actualOdbiorca.opis,
                            actualOdbiorca.email,
                            actualOdbiorca.telefon,
                        )
                    )
                }
            } // Odbiorca
        }
    }
}