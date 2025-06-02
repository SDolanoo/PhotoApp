package com.example.photoapp.features.faktura.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoapp.features.faktura.data.Faktura
import com.example.photoapp.features.faktura.data.FakturaRepository
import com.example.photoapp.features.faktura.data.ProduktFaktura
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FakturaDetailsViewModel @Inject constructor(
    private val repository: FakturaRepository
) : ViewModel() {

    private val _actualFaktura = MutableStateFlow<Faktura?>(null)
    val actualFaktura: StateFlow<Faktura?> = _actualFaktura

    private val _actualProdukty = MutableStateFlow<List<ProduktFaktura>>(emptyList())
    val actualProdukty: StateFlow<List<ProduktFaktura>> = _actualProdukty.asStateFlow()

    private val _editedProdukty = MutableStateFlow<List<ProduktFaktura>>(emptyList())
    val editedProdukty: StateFlow<List<ProduktFaktura>> = _editedProdukty.asStateFlow()

    private val _editedFaktura = MutableStateFlow<Faktura?>(null)
    val editedFaktura: StateFlow<Faktura?> = _editedFaktura.asStateFlow()

    fun setFaktura(faktura: Faktura) {
        _actualFaktura.value = faktura
    }

    fun loadProducts(faktura: Faktura) {
        viewModelScope.launch(Dispatchers.IO) {
            val products = repository.getProduktyForFaktura(faktura.id)
            _actualProdukty.value = products
            _editedProdukty.value = products
            _editedFaktura.value = faktura
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

    fun updateEditedProductTemp(index: Int, produkt: ProduktFaktura, callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _editedProdukty.value = _editedProdukty.value.toMutableList().also {
                it[index] = produkt
            }
            callback()
        }
    }

    fun updateEditedFakturaTemp(faktura: Faktura, callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _editedFaktura.value = faktura
            callback()
        }
    }

    fun deleteProduct(product: ProduktFaktura, callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteProdukt(product)
            callback()
        }
    }

    fun addOneProduct(fakturaId: Long, name: String, qty: String, callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val produkt = ProduktFaktura(
                fakturaId = fakturaId,
                nazwaProduktu = name,
                jednostkaMiary = "szt.",
                ilosc = qty,
                wartoscNetto = "0",
                stawkaVat = "0%",
                podatekVat = "0",
                brutto = "0"
            )
            repository.insertProdukt(produkt)
            callback()
        }
    }

    fun updateToDBProductsAndFaktura(callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateFaktura(_editedFaktura.value!!)
            setFaktura(_editedFaktura.value!!)
            _editedProdukty.value.forEach {
                repository.updateProdukt(it)
            }
            callback()
        }
    }

    fun editingSuccess() {
        viewModelScope.launch(Dispatchers.IO) {
            updateToDBProductsAndFaktura {
                loadProducts(_editedFaktura.value!!)
            }
        }
    }

    fun editingFailed() {
        viewModelScope.launch(Dispatchers.IO) {
            loadProducts(_actualFaktura.value!!)
        }
    }
}
