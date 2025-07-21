package com.example.photoapp.features.faktura.ui.details

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.photoapp.R
import com.example.photoapp.core.components.DatePickerModal
import com.example.photoapp.core.utils.convertDateToString
import com.example.photoapp.core.utils.convertMillisToString
import com.example.photoapp.core.utils.convertStringToDate
import com.example.photoapp.features.faktura.composables.common.CustomOutlinedButton
import com.example.photoapp.features.faktura.composables.common.SearchableDropdownOverlay
import com.example.photoapp.features.faktura.composables.forms.InvoiceForm
import com.example.photoapp.features.faktura.composables.forms.NabywcaForm
import com.example.photoapp.features.faktura.composables.forms.ProductForm
import com.example.photoapp.features.faktura.composables.forms.SprzedawcaForm
import com.example.photoapp.features.faktura.composables.readOnly.InvoiceReadOnly
import com.example.photoapp.features.faktura.composables.readOnly.NabywcaReadOnly
import com.example.photoapp.features.faktura.composables.readOnly.ProductReadOnly
import com.example.photoapp.features.faktura.composables.readOnly.SprzedawcaReadOnly
import com.example.photoapp.features.faktura.data.faktura.Faktura
import com.example.photoapp.features.faktura.data.faktura.ProduktFaktura
import com.example.photoapp.features.faktura.data.odbiorca.Odbiorca
import com.example.photoapp.features.faktura.data.sprzedawca.Sprzedawca
import com.example.photoapp.features.faktura.ui.DatePickerTarget
import com.example.photoapp.features.faktura.validation.ValidationViewModel
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FakturaDetailsScreen(
    faktura: Faktura,
    leaveDetailsScreen: () -> Unit,
    viewModel: FakturaDetailsViewModel = hiltViewModel(),
    validationVM: ValidationViewModel = hiltViewModel()
) {
    val actualFaktura by viewModel.actualFaktura.collectAsState()
    val editedFaktura by viewModel.editedFaktura.collectAsState()

    val actualProdukty by viewModel.actualProdukty.collectAsState()
    val editedProdukty  by viewModel.editedProdukty.collectAsState()

    val actualOdbiorca  by viewModel.actualOdbiorca.collectAsState()
    val editedOdbiorca  by viewModel.editedOdbiorca.collectAsState()

    val actualSprzedawca  by viewModel.actualSprzedawca.collectAsState()
    val editedSprzedawca  by viewModel.editedSprzedawca.collectAsState()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    var isEditing by remember { mutableStateOf(false) }

    var showDatePicker by remember { mutableStateOf(false) }
    var datePickerTarget by remember { mutableStateOf<DatePickerTarget?>(null) }
    var customDateWystawienia by remember { mutableStateOf(convertDateToString(Date())) }
    var customDateSprzedazy by remember { mutableStateOf(convertDateToString(Date())) }


    var showOdbiorcaDropdown by remember { mutableStateOf(false) }
    var showSprzedawcaDropdown by remember { mutableStateOf(false) }
    var showProductDropdown by remember { mutableStateOf(false) }
    var dropdownProductIndex by remember { mutableIntStateOf(0) }

    val validationResult by validationVM.validationResult.collectAsState()

    LaunchedEffect(faktura) {
        viewModel.getFakturaByID(faktura.id) { f ->
            viewModel.setFaktura(f)
            Log.i("Dolan", "loading products")
            viewModel.loadProducts(f)
            Log.i("Dolan", "loading products succesfuly")
        }
    }

    Scaffold(
    topBar = {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    if (isEditing) "Edytuj Fakture" else "Szczegóły Faktura",
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
                            sellerName = editedSprzedawca.nazwa,
                            buyerName = editedOdbiorca.nazwa,
                            products = editedProdukty
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
                if (isEditing) {
                    val newTyp = remember { mutableStateOf(editedFaktura.typFaktury) }
                    val newNumer = remember { mutableStateOf(editedFaktura.numerFaktury) }
                    val newDataWystawienia =
                        remember { mutableStateOf(convertDateToString(editedFaktura.dataWystawienia!!)) }
                    val newDataSprzedazy =
                        remember { mutableStateOf(convertDateToString(editedFaktura.dataSprzedazy!!)) }
                    val newMiejsceWystawienia = remember { mutableStateOf(editedFaktura.miejsceWystawienia) }
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
                        onEdit = {
                            viewModel.updateEditedFakturaTemp(
                                faktura = Faktura(
                                    id = faktura.id,
                                    uzytkownikId = editedFaktura.uzytkownikId,
                                    odbiorcaId = editedOdbiorca.id,
                                    sprzedawcaId = editedSprzedawca.id,
                                    typFaktury = newTyp.value,
                                    numerFaktury = newNumer.value,
                                    dataWystawienia = convertStringToDate(newDataWystawienia.value),
                                    dataSprzedazy = convertStringToDate(newDataSprzedazy.value),
                                    miejsceWystawienia = newMiejsceWystawienia.value,
                                    razemNetto = editedFaktura.razemNetto,
                                    razemVAT = editedFaktura.razemVAT,
                                    razemBrutto = editedFaktura.razemBrutto,
                                    doZaplaty = editedFaktura.doZaplaty,
                                    waluta = editedFaktura.waluta,
                                    formaPlatnosci = editedFaktura.formaPlatnosci,
                                    produktyId = emptyList()
                                ),
                                callback = {}
                            )
                        }
                    )
                    LaunchedEffect(editedFaktura.dataSprzedazy) {
                        newDataSprzedazy.value = convertDateToString(editedFaktura.dataSprzedazy!!)
                    }

                    LaunchedEffect(editedFaktura.dataWystawienia) {
                        newDataWystawienia.value = convertDateToString(editedFaktura.dataWystawienia!!)
                    }
                } else {
                    InvoiceReadOnly(
                        modifier = Modifier,
                        fields = listOf(
                            actualFaktura.typFaktury,
                            actualFaktura.numerFaktury,
                            convertDateToString(actualFaktura.dataWystawienia!!),
                            convertDateToString(actualFaktura.dataSprzedazy!!),
                            actualFaktura.miejsceWystawienia
                        )
                    )
                }

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

                validationResult.fieldErrors["SELLER_NAME"]?.let { error ->
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
                                viewModel.editEditedSprzedawca(
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
                                )
                            },
                            onButtonClick = {
                                showSprzedawcaDropdown = true
                            },
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
                            onEdit = {
                                viewModel.editEditedOdbiorca(
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
                                )
                            },
                            onButtonClick = {
                                showOdbiorcaDropdown = true
                            }
                        )
                    }
                } else {
                    NabywcaReadOnly(
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

            if (isEditing) {
                itemsIndexed(
                    items = editedProdukty,
                    key = { _, item -> item.id }
                ) { index, product ->
                    key (product.id) {

                        val errorName = validationResult.fieldErrors["PRODUCT_NAME_$index"]
                        val errorQuantity = validationResult.fieldErrors["PRODUCT_QUANTITY_$index"]
                        val errorBrutto = validationResult.fieldErrors["PRODUCT_BRUTTO_$index"]

                        Column(modifier = Modifier.padding(start = 16.dp, top = 4.dp)) {
                            errorName?.let { Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                            errorQuantity?.let { Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                            errorBrutto?.let { Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                        }

                        val nazwaProduktu = remember { mutableStateOf(product.nazwaProduktu) }
                        val ilosc = remember { mutableStateOf(product.ilosc) }
                        val jednostkaMiary = remember { mutableStateOf(product.jednostkaMiary) }
                        val cenaNetto = remember { mutableStateOf(product.cenaNetto) }
                        val stawkaVat = remember { mutableStateOf(product.stawkaVat) }
                        val wartoscNetto = remember { mutableStateOf(product.wartoscNetto) }
                        val wartoscBrutto = remember { mutableStateOf(product.wartoscBrutto) }
                        val rabat = remember { mutableStateOf(product.rabat) }
                        val pkwiu = remember { mutableStateOf(product.pkwiu) }

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
                                viewModel.deleteEditedProduct(product)
                            },
                            onEdit = {
                                viewModel.updateEditedProductTemp(
                                    index,
                                    ProduktFaktura(
                                        id = product.id,
                                        nazwaProduktu = nazwaProduktu.value,
                                        ilosc = ilosc.value,
                                        jednostkaMiary = jednostkaMiary.value,
                                        cenaNetto = cenaNetto.value,
                                        stawkaVat = stawkaVat.value,
                                        wartoscNetto = wartoscNetto.value,
                                        wartoscBrutto = wartoscBrutto.value,
                                        rabat = rabat.value,
                                        pkwiu = pkwiu.value
                                    ),
                                    callback = {}
                                )
                            },
                            onButtonClick = {
                                dropdownProductIndex = index
                                showProductDropdown = true
                            }
                        )
                    }

                    HorizontalDivider(
                        thickness = 1.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    )
                }

                item {
                    Row(modifier = Modifier.padding(top = 12.dp)) {
                        CustomOutlinedButton(
                            title = "Dodaj",
                            onClick = {
                                viewModel.addOneProductToEdited()
                            },
                            icon = painterResource(R.drawable.baseline_add_24),
                            height = 48,
                            modifier = Modifier.weight(1f),
                            textColor = Color.Blue,
                            outlineColor = Color.Blue
                        )
                    }
                }
            } else { // Products
                item {ProductReadOnly(modifier = Modifier, produkty = actualProdukty)}
            }
        }

        if (showDatePicker) {
            DatePickerModal(
                onDateSelected = { millis ->
                    if (millis != null) {
                        val newDate = convertMillisToString(millis)
                        if (datePickerTarget == DatePickerTarget.WYSTAWIENIA) {
                            viewModel.updateEditedFakturaTemp(editedFaktura.copy(dataWystawienia = convertStringToDate(newDate))) {  }
                        } else {
                            viewModel.updateEditedFakturaTemp(editedFaktura.copy(dataSprzedazy = convertStringToDate(newDate))) {  }
                        }
                        Log.i("Dolan", "UPDATED RAPORT $newDate")

                    }
                },
                onDismiss = { showDatePicker = false }
            )
        }

        if (showSprzedawcaDropdown) {
            SearchableDropdownOverlay(
                items = viewModel.getListOfSprzedacwa(),
                onItemSelected = { viewModel.replaceEditedSprzedawca(it) },
                onDismissRequest = { showSprzedawcaDropdown = false },
                itemToSearchableText = { it.nazwa },
                itemContent = { sprzedawca ->
                    Column {
                        Text(text = sprzedawca.nazwa, fontWeight = FontWeight.Bold)
                    }
                },
                modifier = Modifier.padding(innerPadding)
            )
        }

        if (showOdbiorcaDropdown) {
            SearchableDropdownOverlay(
                items = viewModel.getListOfOdbiorca(),
                onItemSelected = { viewModel.replaceEditedOdbiorca(it) },
                onDismissRequest = { showOdbiorcaDropdown = false },
                itemToSearchableText = { it.nazwa },
                itemContent = { odbiorca ->
                    Column {
                        Text(text = odbiorca.nazwa, fontWeight = FontWeight.Bold)
                    }
                },
                modifier = Modifier.padding(innerPadding)
            )
        }

        if (showProductDropdown) {
            SearchableDropdownOverlay(
                items = viewModel.getListOfProdukty(),
                onItemSelected = { viewModel.replaceEditedProdukt(dropdownProductIndex, it, callback = {}) },
                onDismissRequest = { showProductDropdown = false },
                itemToSearchableText = { it.nazwaProduktu },
                itemContent = { produkt ->
                    Column {
                        Row {
                            Text(text = produkt.nazwaProduktu, fontWeight = FontWeight.Bold)
                            val formattedPrice = produkt.wartoscBrutto.toDoubleOrNull()?.let {
                                "%.2f".format(it)
                            } ?: "Błąd"
                            Text(text = "Cena: $formattedPrice zł")
                        }
                    }
                },
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

enum class DatePickerTarget {
    WYSTAWIENIA,
    SPRZEDAZY
}
