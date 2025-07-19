package com.example.photoapp.features.faktura.ui.details

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoapp.features.faktura.data.faktura.Faktura
import com.example.photoapp.features.faktura.data.faktura.FakturaRepository
import com.example.photoapp.features.faktura.data.faktura.ProduktFaktura
import com.example.photoapp.features.faktura.data.odbiorca.Odbiorca
import com.example.photoapp.features.faktura.data.odbiorca.OdbiorcaRepository
import com.example.photoapp.features.faktura.data.sprzedawca.Sprzedawca
import com.example.photoapp.features.faktura.data.sprzedawca.SprzedawcaRepository
import com.example.photoapp.features.faktura.ui.FakeData.now
import com.example.photoapp.features.faktura.ui.FakeData.odbiorca
import com.example.photoapp.features.faktura.ui.FakeData.sprzedawca
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class FakturaDetailsViewModel @Inject constructor(
    private val repository: FakturaRepository,
    private val sprzedawcaRepository: SprzedawcaRepository,
    private val odbiorcaRepository: OdbiorcaRepository
) : ViewModel() {

    // ---- FAKTURA ----
    private val _actualFaktura = MutableStateFlow<Faktura?>(null)
    val actualFaktura: StateFlow<Faktura?> = _actualFaktura.asStateFlow()

    private val _editedFaktura = MutableStateFlow<Faktura>(Faktura(
        id = 1L,
        uzytkownikId = 1L,
        odbiorcaId = 1L,
        sprzedawcaId = 1L,
        typFaktury = "Faktura",
        numerFaktury = "FV-TEST-001",
        dataWystawienia = Date(),
        dataSprzedazy = Date(),
        razemNetto = "100.00",
        razemVAT = "23",
        razemBrutto = "123.00",
        doZaplaty = "123.00",
        waluta = "PLN",
        formaPlatnosci = "Przelew",
        miejsceWystawienia = "",
        produktyId = emptyList()
    ))
    val editedFaktura: StateFlow<Faktura> = _editedFaktura.asStateFlow()

    // ---- PRODUKTY ----
    private val _actualProdukty = MutableStateFlow<List<ProduktFaktura>>(emptyList())
    val actualProdukty: StateFlow<List<ProduktFaktura>> = _actualProdukty.asStateFlow()

    private val _editedProdukty = MutableStateFlow<List<ProduktFaktura>>(emptyList())
    val editedProdukty: StateFlow<List<ProduktFaktura>> = _editedProdukty.asStateFlow()

    // ---- SPRZEDAWCA ----
    private val _actualSprzedawca = MutableStateFlow<Sprzedawca>(Sprzedawca(
        nazwa = "", nip = "", adres = "",
        kodPocztowy = "",
        miejscowosc = "",
        kraj = "",
        opis = "",
        email = "",
        telefon = ""
    ))
    val actualSprzedawca: StateFlow<Sprzedawca> = _actualSprzedawca.asStateFlow()

    private val _editedSprzedawca = MutableStateFlow<Sprzedawca>(Sprzedawca(
        nazwa = "", nip = "", adres = "",
        kodPocztowy = "",
        miejscowosc = "",
        kraj = "",
        opis = "",
        email = "",
        telefon = ""
    ))
    val editedSprzedawca: StateFlow<Sprzedawca> = _editedSprzedawca.asStateFlow()

    // ---- ODBIORCA ----
    private val _actualOdbiorca = MutableStateFlow<Odbiorca>(Odbiorca(
        nazwa = "", nip = "", adres = "",
        kodPocztowy = "",
        miejscowosc = "",
        kraj = "",
        opis = "",
        email = "",
        telefon = ""
    ))
    val actualOdbiorca: StateFlow<Odbiorca> = _actualOdbiorca.asStateFlow()

    private val _editedOdbiorca = MutableStateFlow<Odbiorca>(Odbiorca(
        nazwa = "", nip = "", adres = "",
        kodPocztowy = "",
        miejscowosc = "",
        kraj = "",
        opis = "",
        email = "",
        telefon = ""
    ))
    val editedOdbiorca: StateFlow<Odbiorca> = _editedOdbiorca.asStateFlow()


    fun loadProducts(faktura: Faktura) {
        viewModelScope.launch(Dispatchers.IO) {
            val produkty = repository.getProduktyForFaktura(faktura)
            val sprzedawca = sprzedawcaRepository.getById(faktura.sprzedawcaId.toLong())
            val odbiorca = odbiorcaRepository.getById(faktura.odbiorcaId.toLong())
            // Ustawienie actual i edited jednocześnie
            _editedFaktura.value = faktura

            _actualProdukty.value = produkty
            _editedProdukty.value = produkty

            _actualSprzedawca.value = sprzedawca!!
            _editedSprzedawca.value = sprzedawca
            Log.i("Dolan", sprzedawca.toString())

            _actualOdbiorca.value = odbiorca!!
            _editedOdbiorca.value = odbiorca
            Log.i("Dolan", odbiorca.toString())
        }
    }

    fun loadOnlyProducts(faktura: Faktura) {
        viewModelScope.launch(Dispatchers.IO) {
            val products = repository.getProduktyForFaktura(faktura)
            _actualProdukty.value = products
            _editedProdukty.value = products
        }
    }

    fun getFakturaByID(id: Long, callback: (Faktura) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val faktura = repository.getFakturaByID(id)
            callback(faktura!!)
        }
    }

    fun editingSuccess() {
        viewModelScope.launch(Dispatchers.IO) {
            updateAllEditedToDB {
                loadProducts(it)
            }
        }
    }

    fun editingFailed() {
        viewModelScope.launch(Dispatchers.IO) {
            loadProducts(_actualFaktura.value!!)
        }
    }

    fun updateEditedFakturaTemp(faktura: Faktura, callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _editedFaktura.value = faktura
            callback()
        }
    }

    fun updateEditedProductTemp(index: Int, produkt: ProduktFaktura, callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _editedProdukty.value = _editedProdukty.value.toMutableList().also {
                it[index] = produkt
            }
            callback()
        }
    }

    fun deleteEditedProduct(produkt: ProduktFaktura, callback: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            _editedProdukty.update { currentList ->
                currentList.filterNot { it == produkt }
            }

            callback()
        }
    }

    fun updateAllEditedToDB(callback: (Faktura) -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val faktura = editedFaktura.value
                val produktyEdited = editedProdukty.value
                val sprzedawca = editedSprzedawca.value
                val odbiorca = editedOdbiorca.value

                val nowaListaId = mutableListOf<Long>()

                // 🧠 OBSŁUGA KAŻDEGO PRODUKTU INDYWIDUALNIE
                produktyEdited.forEachIndexed { index, produkt ->
                    val produktId = when (produkt.id) {
                        in Long.MIN_VALUE until 1L -> repository.insertProdukt(produkt) // Zwraca nowe ID
                        else -> {
                            repository.updateProdukt(produkt)
                            produkt.id
                        }
                    }
                    nowaListaId.add(produktId)
                    // Jeżeli był nowy, nadpisujemy id w pamięci
                    val product = produkt.copy(id = produktId)
                    updateEditedProductTemp(index, product, callback = {})
                }

                // 🔥 DETEKCJA USUNIĘTYCH PRODUKTÓW
                val editedIds = produktyEdited.map { it.id }.toSet()

                // 🔁 Aktualizacja sprzedawcy i odbiorcy
                sprzedawcaRepository.update(sprzedawca)
                odbiorcaRepository.update(odbiorca)

                val updatedFaktura = faktura.copy(produktyId = nowaListaId)
                repository.updateFaktura(updatedFaktura)
                callback(updatedFaktura)

            } catch (e: Exception) {
                Log.e("Dolan", "Błąd podczas zapisu do DB: ${e.message}")
            }
        }
    }

    private var nextTempProductId = -1L

    fun addOneProductToEdited(callback: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            val newProduct = ProduktFaktura(
                id = nextTempProductId--,
                nazwaProduktu = "Nazwa Produktu",
                jednostkaMiary = "szt.",
                ilosc = "1",
                cenaNetto = "0",
                wartoscNetto = "0",
                wartoscBrutto = "0",
                stawkaVat = "0%",
                rabat = "",
                pkwiu = ""
            )

            _editedProdukty.update { currentList ->
                val index = currentList.indexOfFirst { it.nazwaProduktu == newProduct.nazwaProduktu }

                if (index >= 0) {
                    val existing = currentList[index]
                    val newIlosc = (existing.ilosc.toIntOrNull() ?: 1) + 1

                    currentList.toMutableList().apply {
                        set(index, existing.copy(ilosc = newIlosc.toString()))
                    }
                } else {
                    currentList + newProduct
                }
            }

            callback()
        }
    }

    fun setFaktura(faktura: Faktura) {
        _actualFaktura.value = faktura
    }

    // ---- SPRZEDAWCA ----
    fun editEditedSprzedawca(sprzedawca: Sprzedawca) {
        viewModelScope.launch(Dispatchers.IO) {
            _editedSprzedawca.value = sprzedawca
        }
    }

    fun replaceEditedSprzedawca(newSprzedawca: Sprzedawca, callback: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            _editedSprzedawca.value = newSprzedawca
            _editedFaktura.value = _editedFaktura.value.copy(sprzedawcaId = newSprzedawca.id)
            callback()
        }
    }

    // ---- ODBIORCA ----
    fun editEditedOdbiorca(odbiorca: Odbiorca) {
        viewModelScope.launch(Dispatchers.IO) {
            _editedOdbiorca.value = odbiorca
        }
    }

    fun replaceEditedOdbiorca(newOdbiorca: Odbiorca, callback: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            _editedOdbiorca.value = newOdbiorca
            _editedFaktura.value = _editedFaktura.value.copy(odbiorcaId = newOdbiorca.id)
            callback()
        }
    }

    fun getListOfSprzedacwa(): List<Sprzedawca> {
        return runBlocking {
            withContext(Dispatchers.IO) {
                sprzedawcaRepository.getAll()
            }
        }
    }

    fun getListOfOdbiorca(): List<Odbiorca> {
        return runBlocking {
            withContext(Dispatchers.IO) {
                odbiorcaRepository.getAllOdbiorcy()
            }
        }
    }

    fun getListOfProdukty(): List<ProduktFaktura> {
        return runBlocking {
            withContext(Dispatchers.IO) {
                repository.getAllProdukty()
            }
        }
    }

    fun replaceEditedProdukt(index: Int, product: ProduktFaktura,  callback: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            _editedProdukty.update { currentList ->
                currentList.toMutableList().apply {
                    this[index] = product
                }
            }
            callback()
        }
    }

}
