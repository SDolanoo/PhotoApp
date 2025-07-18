package com.example.photoapp.ui.testingButtons

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoapp.features.faktura.data.odbiorca.OdbiorcaRepository
import com.example.photoapp.features.faktura.data.sprzedawca.SprzedawcaRepository
import com.example.photoapp.core.database.data.repos.UzytkownikRepository
import com.example.photoapp.features.faktura.data.faktura.Faktura
import com.example.photoapp.features.faktura.data.faktura.FakturaRepository
import com.example.photoapp.features.faktura.data.faktura.ProduktFaktura
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@Composable
fun TestingButtons(
    backToHome: () -> Unit,
    databaseViewModel: TestingButtonVM  = hiltViewModel()// <-- inject this ViewModel
) {
    Column(modifier = Modifier.padding(16.dp)) {

        Button(
            onClick = { backToHome() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 30.dp, bottom = 8.dp)
        ) {
            Text(text = "Cofnij stronę")
        }

        Button(
            onClick = { databaseViewModel.addTestUzytkownik() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Text(text = "Add Test Uzytkownik")
        }

        Button(
            onClick = { databaseViewModel.addTestOdbiorcaAndSprzedawca() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Text(text = "Add Test Odbiorca & Sprzedawca")
        }

        Button(
            onClick = { databaseViewModel.addTestInvoices() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Text(text = "Add Test Invoices")
        }

        Button(
            onClick = { databaseViewModel.addTestInvoiceProducts() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Text(text = "Add Test Invoices Products")
        }
    }
}

@HiltViewModel
class TestingButtonVM @Inject constructor(
    private val fakturaRepository: FakturaRepository,
    private val odbiorcaRepository: OdbiorcaRepository,
    private val sprzedawcaRepository: SprzedawcaRepository,
    private val uzytkownikRepository: UzytkownikRepository
) : ViewModel() {

    fun addTestUzytkownik() {
        viewModelScope.launch(Dispatchers.IO) {
            uzytkownikRepository.insert(login = "stasio", "stasio", "stasio@gmail.com")
        }
    }

    fun addTestInvoices() {
        viewModelScope.launch(Dispatchers.IO) {
            val odbiorca = odbiorcaRepository.addOrGetOdbiorca("Jan Kowalski", "1234567890", "Warszawa, Polska")
            val sprzedawca = sprzedawcaRepository.addOrGetSprzedawca("Firma XYZ", "0987654321", "Kraków, Polska")

            repeat(3) { index ->
                val now = Date()
                val faktura = Faktura(
                    uzytkownikId = 1,
                    odbiorcaId = odbiorca.id,
                    sprzedawcaId = sprzedawca.id,
                    numerFaktury = "FV-TEST-00$index",
                    typFaktury = "Faktura",
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
                fakturaRepository.insertFaktura(faktura)
            }
        }
    }

    fun addTestInvoiceProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            val allFaktury = fakturaRepository.getAllFaktury()

            allFaktury.forEach { faktura ->
                repeat(2) { i ->
                    val produkt = ProduktFaktura(
                        fakturaId = faktura.id,
                        nazwaProduktu = "Produkt ${i + 1}",
                        jednostkaMiary = "szt",
                        ilosc = "${i + 1}",
                        cenaNetto = "50.00",
                        wartoscNetto = "11.50",
                        wartoscBrutto = "61.50",
                        stawkaVat = "23",
                        rabat = "TODO()",
                        pkwiu = "TODO()",
                    )
                    fakturaRepository.insertProdukt(produkt)
                }
            }
        }
    }

    fun addTestOdbiorcaAndSprzedawca() {
        viewModelScope.launch(Dispatchers.IO) {
            val commonName = "Janek Firma Testowa"
            val commonNip = "1234567890"
            val commonAdres = "Testowa 1, 00-001 Warszawa"

            // Use repositories to ensure uniqueness (or insert directly)
            odbiorcaRepository.addOrGetOdbiorca(
                nazwa = "Odbiorca $commonName",
                nip = commonNip,
                adres = commonAdres
            )

            sprzedawcaRepository.addOrGetSprzedawca(
                nazwa = "Sprzedawca $commonName",
                nip = commonNip,
                adres = commonAdres
            )
        }
    }

}
