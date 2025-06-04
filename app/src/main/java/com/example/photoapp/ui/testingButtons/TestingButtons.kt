package com.example.photoapp.ui.testingButtons

import android.util.Log
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
import com.example.photoapp.core.database.data.entities.Kategoria
import com.example.photoapp.core.database.data.entities.Uzytkownik
import com.example.photoapp.core.database.data.repos.KategoriaRepository
import com.example.photoapp.core.database.data.repos.OdbiorcaRepository
import com.example.photoapp.core.database.data.repos.SprzedawcaRepository
import com.example.photoapp.core.database.data.repos.UzytkownikRepository
import com.example.photoapp.features.faktura.data.Faktura
import com.example.photoapp.features.faktura.data.FakturaRepository
import com.example.photoapp.features.faktura.data.ProduktFaktura
import com.example.photoapp.features.paragon.data.Paragon
import com.example.photoapp.features.paragon.data.ParagonRepository
import com.example.photoapp.features.paragon.data.ProduktParagon
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
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
            onClick = { databaseViewModel.addTestKategorias() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Text(text = "Add Test Kategorias")
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

        Button(
            onClick = { databaseViewModel.addTestParagony() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Text(text = "Add Test Paragony")
        }

        Button(
            onClick = { databaseViewModel.addTestParagonProdukty() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Text(text = "Add Test Produkty Paragon")
        }


    }



}

@HiltViewModel
class TestingButtonVM @Inject constructor(
    private val paragonRepository: ParagonRepository,
    private val fakturaRepository: FakturaRepository,
    private val kategoriaRepository: KategoriaRepository,
    private val odbiorcaRepository: OdbiorcaRepository,
    private val sprzedawcaRepository: SprzedawcaRepository,
    private val uzytkownikRepository: UzytkownikRepository
) : ViewModel() {

    fun addTestUzytkownik() {
        viewModelScope.launch(Dispatchers.IO) {
            uzytkownikRepository.insert(login = "stasio", "stasio", "stasio@gmail.com")
        }
    }

    fun addTestKategorias() {
        viewModelScope.launch(Dispatchers.IO) {
            val categories = listOf(
                Kategoria(nazwa = "Żywność"),
                Kategoria(nazwa = "Meble"),
                Kategoria(nazwa = "Sprzęt")
            )
            categories.forEach {
                kategoriaRepository.insert(it)
            }
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
                fakturaRepository.insertFaktura(faktura)
            }
        }
    }

    fun addTestInvoiceProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            val allFaktury = fakturaRepository.getAllLiveFaktury().value ?: return@launch

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
                    )
                    fakturaRepository.insertProdukt(produkt)
                }
            }
        }
    }

    fun addTestParagony() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.i("Dolan", "adding test paragony")
            val now = Date()
            repeat(3) { index ->
                val paragon = Paragon(
                    uzytkownikId = 1,
                    dataZakupu = now,
                    nazwaSklepu = "Sklep $index",
                    kwotaCalkowita = 59.99 + index * 10
                )
                paragonRepository.insertParagon(paragon)
                Log.i("Dolan", "added one ${paragon}")
            }
        }
    }

    fun addTestParagonProdukty() {
        viewModelScope.launch(Dispatchers.IO) {
            val paragony = paragonRepository.getAllParagony()

            // Ensure at least one kategoria exists
            val kategorie = kategoriaRepository.getAllKategorii()
            val fallbackKategoriaId = kategorie.firstOrNull()?.id

            if (paragony.isEmpty()) return@launch

            paragony.forEachIndexed { index, paragon ->
                repeat(2) { i ->
                    val produkt = ProduktParagon(
                        paragonId = paragon.id,
                        kategoriaId = fallbackKategoriaId,
                        nazwaProduktu = "Produkt ${index + 1}-$i",
                        cenaSuma = 19.99 + i * 5,
                        ilosc = i + 1
                    )
                    paragonRepository.insertProdukt(produkt)
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
