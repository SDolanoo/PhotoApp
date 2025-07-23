package com.example.photoapp.ui.acceptFaktura

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.photoapp.core.utils.convertDateToString
import com.example.photoapp.features.faktura.composables.readOnly.InvoiceReadOnly
import com.example.photoapp.features.faktura.composables.readOnly.NabywcaReadOnly
import com.example.photoapp.features.faktura.composables.readOnly.ProductReadOnly
import com.example.photoapp.features.faktura.composables.readOnly.SprzedawcaReadOnly
import com.example.photoapp.features.faktura.data.faktura.Faktura
import com.example.photoapp.features.faktura.data.faktura.ProduktFaktura
import com.example.photoapp.features.faktura.data.odbiorca.Odbiorca
import com.example.photoapp.features.faktura.data.sprzedawca.Sprzedawca
import com.example.photoapp.features.faktura.ui.details.ProduktFakturaZProduktem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcceptFakturaScreen(
    faktura: Faktura,
    sprzedawca: Sprzedawca,
    odbiorca: Odbiorca,
    produkty: List<ProduktFakturaZProduktem>,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    viewModel: AcceptFakturaController = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Szczegóły Faktura",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        onCancel()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }

                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            viewModel.addToDatabase(
                                faktura = faktura,
                                sprzedawca = sprzedawca,
                                odbiorca = odbiorca,
                                produkty = produkty
                            )
                            onConfirm()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Dodaj")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = { onCancel() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Anuluj")
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn (modifier = Modifier
            .padding(innerPadding)
            .padding(horizontal = 8.dp)) {
            item {
                Text(
                    text = "Faktura",
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                HorizontalDivider(
                    thickness = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )
            }

            item {
                InvoiceReadOnly(
                    modifier = Modifier,
                    fields = listOf(
                        faktura.typFaktury,
                        faktura.numerFaktury,
                        convertDateToString(faktura.dataWystawienia!!),
                        convertDateToString(faktura.dataSprzedazy!!),
                        faktura.miejsceWystawienia
                    )
                )
            } // InvoiceForm

            item {
                HorizontalDivider(
                    thickness = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )
            }

            item {
                Text(
                    text = "Sprzedawca",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                SprzedawcaReadOnly(
                    modifier = Modifier,
                    fields = listOf(
                        sprzedawca.nazwa,
                        sprzedawca.nip,
                        sprzedawca.adres,
                        sprzedawca.kodPocztowy,
                        sprzedawca.miejscowosc,
                        sprzedawca.kraj,
                        sprzedawca.opis,
                        sprzedawca.email,
                        sprzedawca.telefon,
                    )
                )
            } // Sprzedawca

            item {
                HorizontalDivider(
                    thickness = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )
            }

            item {
                Text(
                    text = "Nabywca",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                NabywcaReadOnly(
                    modifier = Modifier,
                    fields = listOf(
                        odbiorca.nazwa,
                        odbiorca.nip,
                        odbiorca.adres,
                        odbiorca.kodPocztowy,
                        odbiorca.miejscowosc,
                        odbiorca.kraj,
                        odbiorca.opis,
                        odbiorca.email,
                        odbiorca.telefon,
                    )
                )
            } // Odbiorca

            item {
                HorizontalDivider(
                    thickness = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )
            }

            item {
                Text(
                    text = "Produkty",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            } // Products

            item {
                ProductReadOnly(modifier = Modifier, produkty = produkty)
            }
        }
    }
}
