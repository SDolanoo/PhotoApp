package com.example.photoapp.features.faktura.ui

import android.util.Log.v
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.photoapp.core.database.data.entities.Odbiorca
import com.example.photoapp.core.database.data.entities.Sprzedawca
import com.example.photoapp.core.navigation.PhotoAppDestinations
import com.example.photoapp.core.utils.convertDateToString
import com.example.photoapp.features.faktura.composables.product.InvoiceForm
import com.example.photoapp.features.faktura.composables.product.NabywcaForm
import com.example.photoapp.features.faktura.composables.product.ProductForm
import com.example.photoapp.features.faktura.composables.product.SprzedawcaForm
import com.example.photoapp.features.faktura.data.Faktura
import com.example.photoapp.features.faktura.data.ProduktFaktura
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.Multimaps.index
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewFakturaPreview() {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    var isDeleteMode = false

    var Invoice by remember { mutableStateOf(FakeData.Invoice) }
    var Seller by remember { mutableStateOf(FakeData.sprzedawca) }
    var Buyer by remember { mutableStateOf(FakeData.odbiorca) }
    val Products = remember { FakeData.Products.toMutableStateList() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if (isDeleteMode) "Usuń Faktury" else "Szczegóły Faktura",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    if (isDeleteMode) {
                        IconButton(onClick = {  }) {
                            Icon(Icons.Default.Close, contentDescription = "Cancel Deletion")
                        }
                    } else {
                        IconButton(onClick = {  }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }

                },
                actions = {
                    if (isDeleteMode) {
                        IconButton(onClick = {  }) {
                            Icon(Icons.Default.Done, contentDescription = "Confirm Deletion")
                        }
                    } else {
                        IconButton(onClick = {  }) {
                            Icon(Icons.Default.Delete, contentDescription = "Enable Delete Mode")
                        }
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        LazyColumn (modifier = Modifier.padding(innerPadding).padding(horizontal = 8.dp)) {
            item {
                Text(
                    text = "Faktura",
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                HorizontalDivider(
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                )
            }

            item {
                val newTyp = remember { mutableStateOf("Faktura")}
                val newNumer = remember { mutableStateOf(Invoice.numerFaktury)}
                val newDataWystawienia = remember { mutableStateOf(convertDateToString(Invoice.dataWystawienia!!))}
                val newDataSprzedazy = remember { mutableStateOf(convertDateToString(Invoice.dataSprzedazy!!))}
                val newMiejsceWystawienia = remember { mutableStateOf("")}
                InvoiceForm(
                    modifier = Modifier,
                    fields = listOf(
                        "Typ" to newTyp,
                        "Numer" to newNumer,
                        "Data wystawienia" to newDataWystawienia,
                        "Data sprzedaży" to newDataSprzedazy,
                        "Miejsce wystawienia" to newMiejsceWystawienia
                    )
                )
            }

            item {
                HorizontalDivider(
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                )
            }

            item {
                Text(
                    text = "Sprzedawca",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                val newNazwa = remember { mutableStateOf(Seller.nazwa)}
                val newNIP = remember { mutableStateOf(Seller.nip)}
                val newAdres = remember { mutableStateOf(Seller.adres)}
                val newKodPocztowy = remember { mutableStateOf("")}
                val newMiejscowosc = remember { mutableStateOf("")}
                val newKraj = remember { mutableStateOf("")}
                val newOpis = remember { mutableStateOf("")}
                val newEmail = remember { mutableStateOf("")}
                val newTelefon = remember { mutableStateOf("")}
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
                    )
                )
            }

            item {
                HorizontalDivider(
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                )
            }

            item {
                Text(
                    text = "Nabywca",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                val newNazwa = remember { mutableStateOf(Buyer.nazwa)}
                val newNIP = remember { mutableStateOf(Buyer.nip)}
                val newAdres = remember { mutableStateOf(Buyer.adres)}
                val newKodPocztowy = remember { mutableStateOf("")}
                val newMiejscowosc = remember { mutableStateOf("")}
                val newKraj = remember { mutableStateOf("")}
                val newOpis = remember { mutableStateOf("")}
                val newEmail = remember { mutableStateOf("")}
                val newTelefon = remember { mutableStateOf("")}
                NabywcaForm(
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
                    )
                )
            }

            item {
                HorizontalDivider(
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                )
            }

            item {
                Text(
                    text = "Produkty",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }

            items(Products) { product ->

                val nazwaProduktu = remember { mutableStateOf(product.nazwaProduktu)}
                val ilosc = remember { mutableStateOf(product.ilosc)}
                val jednostkaMiary = remember { mutableStateOf(product.jednostkaMiary)}
                val cenaNetto = remember { mutableStateOf(product.cenaNetto)}
                val stawkaVat = remember { mutableStateOf(product.stawkaVat)}
                val wartoscNetto = remember { mutableStateOf(product.wartoscNetto)}
                val wartoscBrutto = remember { mutableStateOf(product.wartoscBrutto)}
                val rabat = remember { mutableStateOf("")}
                val pkwiu = remember { mutableStateOf("")}

                ProductForm(
                    modifier = Modifier,
                    fields = listOf(
                        "Nazwa" to nazwaProduktu,
                        "Ilość" to ilosc,
                        "Jednostka" to jednostkaMiary,
                        "Cena netto" to cenaNetto,
                        "Vat %" to stawkaVat,
                        "Wartość netto" to wartoscNetto,
                        "Wartość brutto" to wartoscBrutto,
                        "Rabat %" to rabat,
                        "PKWiU" to pkwiu,
                    ),
                    onDelete = {
                        Products.remove(product)
                    }
                )

                HorizontalDivider(
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                )


            }




//            item {
//                ProductForm(modifier = Modifier)
//            }
        }
    }
}

object FakeData {

    val now = Date()

    val odbiorca = Odbiorca(
        id = 1,
        nazwa = "Jan Kowalski",
        nip = "1234567890",
        adres = "Warszawa, Polska"
    )

    val sprzedawca = Sprzedawca(
        id = 1,
        nazwa = "Firma XYZ",
        nip = "0987654321",
        adres = "Kraków, Polska"
    )


    val Products = listOf(
        ProduktFaktura(
            fakturaId = 1,
            nazwaProduktu = "Produkt 1",
            jednostkaMiary = "szt",
            ilosc = "1",
            cenaNetto = "50.00",
            wartoscNetto = "11.50",
            wartoscBrutto = "61.50",
            stawkaVat = "23",
        ),
        ProduktFaktura(
            fakturaId = 1,
            nazwaProduktu = "Produkt 2",
            jednostkaMiary = "szt",
            ilosc = "2",
            cenaNetto = "50.00",
            wartoscNetto = "11.50",
            wartoscBrutto = "61.50",
            stawkaVat = "23",
        )
    )

    val Invoice = Faktura(
        id = 1,
        uzytkownikId = 1,
        odbiorcaId = odbiorca.id,
        sprzedawcaId = sprzedawca.id,
        numerFaktury = "FV-TEST-001",
        status = "Wystawiona",
        dataWystawienia = now,
        dataSprzedazy = now,
        terminPlatnosci = now,
        razemNetto = "100.00",
        razemVAT = "23",
        razemBrutto = "123.00",
        doZaplaty = "123.00",
        waluta = "PLN",
        formaPlatnosci = "Przelew"
    )
}