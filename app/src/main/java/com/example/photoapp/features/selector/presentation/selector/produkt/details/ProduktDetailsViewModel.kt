package com.example.photoapp.features.selector.presentation.selector.produkt.details

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoapp.features.faktura.data.faktura.FakturaRepository
import com.example.photoapp.features.faktura.data.faktura.Produkt
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProduktDetailsViewModel @Inject constructor(
    private val produktRepository: FakturaRepository
) : ViewModel() {

    private val _actualProdukt = MutableStateFlow<Produkt>(Produkt.default())
    val actualProdukt: StateFlow<Produkt> = _actualProdukt.asStateFlow()

    private val _editedProdukt = MutableStateFlow<Produkt>(Produkt.default())
    val editedProdukt: StateFlow<Produkt> = _editedProdukt.asStateFlow()

    fun loadProdukt(produkt: Produkt) {
        viewModelScope.launch(Dispatchers.IO) {
            val loadedProdukt = produktRepository.getProduktById(produkt.id)
            loadedProdukt.let {
                _actualProdukt.value = it
                _editedProdukt.value = it
                Log.i("ProductDetails", "Załadowano produkt: $it")
            }
        }
    }

    fun editingSuccess() {
        viewModelScope.launch(Dispatchers.IO) {
            updateAllEditedToDB {
                loadProdukt(it)
            }
        }
    }

    fun editingFailed() {
        viewModelScope.launch(Dispatchers.IO) {
            loadProdukt(_actualProdukt.value)
        }
    }

    fun updateEditedProduktTemp(produkt: Produkt, callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _editedProdukt.value = produkt
            callback()
        }
    }

    fun updateAllEditedToDB(callback: (Produkt) -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val produkt = editedProdukt.value
                produktRepository.updateProdukt(produkt)
                callback(produkt.copy(id = produkt.id))
            } catch (e: Exception) {
                Log.e("ProductDetails", "Błąd zapisu: ${e.message}")
            }
        }
    }

    fun getProdukt(produkt: Produkt, callback: (Produkt?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val found = produktRepository.getProduktById(produkt.id)
            callback(found)
        }
    }

    fun setProdukt(produkt: Produkt) {
        _actualProdukt.value = produkt
    }
}
