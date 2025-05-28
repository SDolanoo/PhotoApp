package com.example.photoapp.features.paragon.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoapp.features.paragon.data.Paragon
import com.example.photoapp.features.paragon.data.ParagonRepository
import com.example.photoapp.features.paragon.data.ProduktParagon
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ParagonDetailsScreenViewModel @Inject constructor(
    private val repository: ParagonRepository
) : ViewModel() {

    private val _actualParagon = MutableStateFlow<Paragon?>(null)
    val actualParagon: StateFlow<Paragon?> = _actualParagon.asStateFlow()

    private val _editedParagon = MutableStateFlow<Paragon?>(null)
    val editedParagon: StateFlow<Paragon?> = _editedParagon.asStateFlow()

    private val _actualProdukty = MutableStateFlow<List<ProduktParagon>>(emptyList())
    val actualProdukty: StateFlow<List<ProduktParagon>> = _actualProdukty.asStateFlow()

    private val _editedProdukty = MutableStateFlow<List<ProduktParagon>>(emptyList())
    val editedProdukty: StateFlow<List<ProduktParagon>> = _editedProdukty.asStateFlow()

    fun setParagon(paragon: Paragon) {
        _actualParagon.value = paragon
    }

    fun loadProducts(paragon: Paragon) {
        viewModelScope.launch(Dispatchers.IO) {
            val products = repository.getProductForParagon(paragon.id)
            _actualProdukty.value = products
            _editedProdukty.value = products
            _editedParagon.value = paragon
        }
    }

    fun loadOnlyProducts(paragon: Paragon) {
        viewModelScope.launch(Dispatchers.IO) {
            val products = repository.getProductForParagon(paragon.id)
            _actualProdukty.value = products
            _editedProdukty.value = products
        }
    }

    fun getParagonByID(id: Int, callback: (Paragon) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val paragon = repository.getParagonById(id)
            callback(paragon)
        }
    }

    fun updateEditedParagonTemp(paragon: Paragon, callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _editedParagon.value = paragon
            callback()
        }
    }

    fun updateEditedProductTemp(index: Int, produkt: ProduktParagon, callback: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            _editedProdukty.value = _editedProdukty.value.toMutableList().also {
                it[index] = produkt
            }
            callback()
        }
    }

    fun deleteProduct(product: ProduktParagon, callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteProduktParagon(product)
            callback()
        }
    }

    fun addOneProduct(paragonId: Int, name: String, qty: String, callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val produkt = ProduktParagon(
                paragonId = paragonId,
                nazwaProduktu = name,
                ilosc = qty.toIntOrNull() ?: 1,
                cenaSuma = 0.0 // Adjust pricing logic as needed
            )
            repository.insertProduktParagon(produkt)
            callback()
        }
    }

    fun updateToDBProductsAndParagon(callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateParagon(_editedParagon.value!!)
            setParagon(_editedParagon.value!!)
            _editedProdukty.value.forEach {
                repository.updateProduktParagon(it)
            }
            callback()
        }
    }

    fun editingSuccess() {
        viewModelScope.launch(Dispatchers.IO) {
            updateToDBProductsAndParagon {
                loadProducts(_editedParagon.value!!)
            }
        }
    }

    fun editingFailed() {
        viewModelScope.launch(Dispatchers.IO) {
            loadProducts(_actualParagon.value!!)
        }
    }
}
