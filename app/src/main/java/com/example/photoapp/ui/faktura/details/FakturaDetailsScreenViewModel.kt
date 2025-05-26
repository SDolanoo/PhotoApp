package com.example.photoapp.ui.faktura.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoapp.database.data.DatabaseRepository
import com.example.photoapp.database.data.Faktura
import com.example.photoapp.database.data.ProduktFaktura
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class FakturaDetailsScreenViewModel @Inject constructor(
    private val repository: DatabaseRepository
) : ViewModel() {

    private val _actualFaktura = MutableStateFlow<Faktura?>(null)
    val actualFaktura: StateFlow<Faktura?> = _actualFaktura

    private val _actualProdukty = MutableStateFlow<List<ProduktFaktura>>(emptyList())
    val actualProdukty: StateFlow<List<ProduktFaktura>> = _actualProdukty

    private val _editedProdukty = MutableStateFlow<List<ProduktFaktura>>(emptyList())
    val editedProdukty: StateFlow<List<ProduktFaktura>> = _editedProdukty

    fun setFaktura(faktura: Faktura) {
        _actualFaktura.value = faktura
    }

    fun loadProducts(faktura: Faktura) {
        viewModelScope.launch(Dispatchers.IO) {
            val products = repository.getProductForFaktura(faktura.id)
            _actualProdukty.value = products
            _editedProdukty.value = products
        }
    }

    fun updateEditedProductTemp(index: Int, produkt: ProduktFaktura) {
        _editedProdukty.value = _editedProdukty.value.toMutableList().also { it[index] = produkt }
    }

    fun deleteProduct(product: ProduktFaktura, callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteProduktFaktura(product)
            callback()
        }
    }

    fun addOneProduct(fakturaId: Int, name: String, qty: String, callback: () -> Unit) {
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
            repository.insertProduktFaktura(produkt)
            callback()
        }
    }

    fun updateToDBProducts(callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _editedProdukty.value.forEach {
                repository.updateProduktFaktura(it)
            }
            callback()
        }
    }

    fun updateEditedFakturaTemp(newDate: Date) {
        _actualFaktura.value = _actualFaktura.value?.copy(dataWystawienia = newDate)
    }
}
