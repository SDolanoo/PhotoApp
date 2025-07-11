package com.example.photoapp.features.faktura.ui.details

import android.provider.SyncStateContract.Helpers.update
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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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

    private val _editedFaktura = MutableStateFlow<Faktura?>(null)
    val editedFaktura: StateFlow<Faktura?> = _editedFaktura.asStateFlow()

    // ---- PRODUKTY ----
    private val _actualProdukty = MutableStateFlow<List<ProduktFaktura>>(emptyList())
    val actualProdukty: StateFlow<List<ProduktFaktura>> = _actualProdukty.asStateFlow()

    private val _editedProdukty = MutableStateFlow<List<ProduktFaktura>>(emptyList())
    val editedProdukty: StateFlow<List<ProduktFaktura>> = _editedProdukty.asStateFlow()

    // ---- SPRZEDAWCA ----
    private val _actualSprzedawca = MutableStateFlow<Sprzedawca?>(null)
    val actualSprzedawca: StateFlow<Sprzedawca?> = _actualSprzedawca.asStateFlow()

    private val _editedSprzedawca = MutableStateFlow<Sprzedawca?>(null)
    val editedSprzedawca: StateFlow<Sprzedawca?> = _editedSprzedawca.asStateFlow()

    // ---- ODBIORCA ----
    private val _actualOdbiorca = MutableStateFlow<Odbiorca?>(null)
    val actualOdbiorca: StateFlow<Odbiorca?> = _actualOdbiorca.asStateFlow()

    private val _editedOdbiorca = MutableStateFlow<Odbiorca?>(null)
    val editedOdbiorca: StateFlow<Odbiorca?> = _editedOdbiorca.asStateFlow()


    fun loadProducts(faktura: Faktura) {
        viewModelScope.launch(Dispatchers.IO) {
            val produkty = repository.getProduktyForFaktura(faktura.id)
            val sprzedawca = sprzedawcaRepository.getById(faktura.sprzedawcaId.toLong())
            val odbiorca = odbiorcaRepository.getById(faktura.odbiorcaId.toLong())
            // Ustawienie actual i edited jednocześnie
            _editedFaktura.value = faktura

            _actualProdukty.value = produkty
            _editedProdukty.value = produkty

            _actualSprzedawca.value = sprzedawca
            _editedSprzedawca.value = sprzedawca

            _actualOdbiorca.value = odbiorca
            _editedOdbiorca.value = odbiorca
        }
    }

    fun loadOnlyProducts(faktura: Faktura) {
        viewModelScope.launch(Dispatchers.IO) {
            val products = repository.getProduktyForFaktura(faktura.id)
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
                loadProducts(_editedFaktura.value!!)
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

    fun updateAllEditedToDB(callback: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val faktura = editedFaktura.value
                val produktyEdited = editedProdukty.value
                val produktyActual = actualProdukty.value
                val sprzedawca = editedSprzedawca.value
                val odbiorca = editedOdbiorca.value

                if (faktura == null || sprzedawca == null || odbiorca == null) {
                    Log.e("Dolan", "Brakuje danych do zapisania")
                    return@launch
                }

                // 🔁 Aktualizacja faktury
                repository.updateFaktura(faktura)
                setFaktura(faktura)

                // 🔁 UPSERT produktów
                produktyEdited.forEach { produkt ->
                    repository.updateProdukt(produkt)
                }

                // 🔥 DETEKCJA USUNIĘTYCH PRODUKTÓW
                val editedIds = produktyEdited.map { it.id }.toSet()
                val deletedProdukty = produktyActual.filter { it.id !in editedIds }
                deletedProdukty.forEach {
                    repository.deleteProdukt(it)
                }

                // 🔁 Aktualizacja sprzedawcy i odbiorcy
                sprzedawcaRepository.update(sprzedawca)
                odbiorcaRepository.update(odbiorca)

            } catch (e: Exception) {
                Log.e("Dolan", "Błąd podczas zapisu do DB: ${e.message}")
            } finally {
                callback()
            }
        }
    }



    fun addOneProductToEdited(nazwaProduktu: String, ilosc: String, callback: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            val fakturaId = _editedFaktura.value?.id ?: return@launch

            val newProduct = ProduktFaktura(
                fakturaId = fakturaId,
                nazwaProduktu = nazwaProduktu,
                jednostkaMiary = "szt.",
                ilosc = ilosc,
                cenaNetto = "0",
                wartoscNetto = "0",
                wartoscBrutto = "0",
                stawkaVat = "0%",
                rabat = "",
                pkwiu = ""
            )

            _editedProdukty.update { currentList ->
                currentList + newProduct
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
            _editedFaktura.value = _editedFaktura.value?.copy(sprzedawcaId = newSprzedawca.id)
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
            _editedFaktura.value = _editedFaktura.value?.copy(odbiorcaId = newOdbiorca.id)
            callback()
        }
    }

}
