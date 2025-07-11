package com.example.photoapp.features.faktura.ui

import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.photoapp.R
import com.example.photoapp.core.components.DatePickerModal
import com.example.photoapp.core.utils.convertDateToString
import com.example.photoapp.core.utils.convertMillisToString
import com.example.photoapp.features.faktura.composables.common.CustomOutlinedButton
import com.example.photoapp.features.faktura.composables.forms.InvoiceForm
import com.example.photoapp.features.faktura.composables.forms.NabywcaForm
import com.example.photoapp.features.faktura.composables.forms.ProductForm
import com.example.photoapp.features.faktura.composables.forms.SprzedawcaForm
import com.example.photoapp.features.faktura.data.faktura.Faktura
import com.example.photoapp.features.faktura.data.faktura.ProduktFaktura
import com.example.photoapp.features.faktura.data.odbiorca.Odbiorca
import com.example.photoapp.features.faktura.data.sprzedawca.Sprzedawca
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewFakturaPreview() {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    var isDeleteMode = false

    var isEditing by remember { mutableStateOf(false) }

    var showDatePicker by remember { mutableStateOf(false) }
    var datePickerTarget by remember { mutableStateOf<DatePickerTarget?>(null) }
    var customDate by remember { mutableStateOf(convertDateToString(Date())) }

    var Invoice by remember { mutableStateOf(FakeData.Invoice) }
    var Seller by remember { mutableStateOf(FakeData.sprzedawca) }
    var Buyer by remember { mutableStateOf(FakeData.odbiorca) }
    val Products = remember { FakeData.Products.toMutableStateList() }

    var newProducts by remember { mutableIntStateOf(0) }

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
                    if (isEditing) {
                        IconButton(onClick = {
                            isEditing = false
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Back")
                        }
                    } else {
                        IconButton(onClick = {
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }

                },
                actions = {
                    if (isEditing) {
                        IconButton(onClick = {
                            isEditing = false
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
                    ),
                    showDatePickerWystawienia = {
                        datePickerTarget = DatePickerTarget.WYSTAWIENIA
                        showDatePicker = true
                    },
                    showDatePickerSprzedazy = {
                        datePickerTarget = DatePickerTarget.SPRZEDAZY
                        showDatePicker = true
                    },
                    onEdit = {}
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
                    ),
                    onEdit = {}
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
                    ),
                    onEdit = {}
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

            items(
                items = Products,
                key = { it.id }
            ) { product ->

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

                    },
                    onEdit = {}
                )

                HorizontalDivider(
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                )
            }

            item { // add new product button
                Row(modifier = Modifier.padding(top=12.dp)) {
                    CustomOutlinedButton(
                        title = "Dodaj",
                        onClick = {
                            Products.add(
                                ProduktFaktura(
                                    fakturaId = Invoice.id,
                                    nazwaProduktu = "Nowy produkt",
                                    jednostkaMiary = "szt",
                                    ilosc = "1",
                                    cenaNetto = "0.00",
                                    wartoscNetto = "0.00",
                                    wartoscBrutto = "0.00",
                                    stawkaVat = "23",
                                    rabat = "TODO()",
                                    pkwiu = "TODO()"
                                )
                            )
                        },
                        icon = painterResource(R.drawable.baseline_add_24),
                        height = 48,
                        modifier = Modifier.weight(1f),
                        textColor = Color.Blue,
                        outlineColor = Color.Blue
                    )
                }
            }
        }

        if (showDatePicker) {
            DatePickerModal(
                onDateSelected = { it ->
                    if (it != null) {
                        if (datePickerTarget == DatePickerTarget.WYSTAWIENIA) {
                            customDate = convertMillisToString(it)
                        } else {
                            customDate = convertMillisToString(it)
                        }
                        Log.i("Dolan", "UPDATED RAPORT $customDate")

                    }
                },
                onDismiss = { showDatePicker = false }
            )
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
            rabat = "TODO(",
            pkwiu = "TODO()",
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
            rabat = "TODO(",
            pkwiu = "TODO()",
        )
    )

    val Invoice = Faktura(
        id = 1,
        uzytkownikId = 1,
        odbiorcaId = odbiorca.id,
        sprzedawcaId = sprzedawca.id,
        typFaktury = "Faktura",
        numerFaktury = "FV-TEST-001",
        dataWystawienia = now,
        dataSprzedazy = now,
        razemNetto = "100.00",
        razemVAT = "23",
        razemBrutto = "123.00",
        doZaplaty = "123.00",
        waluta = "PLN",
        formaPlatnosci = "Przelew",
        miejsceWystawienia = ""
    )
}

enum class DatePickerTarget {
    WYSTAWIENIA,
    SPRZEDAZY
}