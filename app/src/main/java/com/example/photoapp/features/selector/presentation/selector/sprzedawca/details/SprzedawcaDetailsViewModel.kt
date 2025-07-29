package com.example.photoapp.features.selector.presentation.selector.sprzedawca.details

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoapp.features.sprzedawca.data.Sprzedawca
import com.example.photoapp.features.sprzedawca.data.SprzedawcaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SprzedawcaDetailsViewModel @Inject constructor(
    private val sprzedawcaRepository: SprzedawcaRepository
) : ViewModel() {

    private val _actualSprzedawca = MutableStateFlow<Sprzedawca>(Sprzedawca.empty())
    val actualSprzedawca: StateFlow<Sprzedawca> = _actualSprzedawca.asStateFlow()

    private val _editedSprzedawca = MutableStateFlow<Sprzedawca>(Sprzedawca.empty())
    val editedSprzedawca: StateFlow<Sprzedawca> = _editedSprzedawca.asStateFlow()

    fun loadSprzedawca(sprzedawca: Sprzedawca) {
        viewModelScope.launch(Dispatchers.IO) {
            val loaded = sprzedawcaRepository.getById(sprzedawca.id)
            loaded?.let {
                _actualSprzedawca.value = it
                _editedSprzedawca.value = it
                Log.i("Dolan", it.toString())
            }
        }
    }

    fun editingSuccess() {
        viewModelScope.launch(Dispatchers.IO) {
            updateAllEditedToDB {
                loadSprzedawca(it)
            }
        }
    }

    fun editingFailed() {
        viewModelScope.launch(Dispatchers.IO) {
            loadSprzedawca(_actualSprzedawca.value)
        }
    }

    fun updateEditedSprzedawcaTemp(sprzedawca: Sprzedawca, callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _editedSprzedawca.value = sprzedawca
            callback()
        }
    }

    fun updateAllEditedToDB(callback: (Sprzedawca) -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val sprzedawca = editedSprzedawca.value
                val id = sprzedawcaRepository.upsertSprzedawcaSmart(sprzedawca)
                callback(sprzedawca.copy(id = id))
            } catch (e: Exception) {
                Log.e("Dolan", "Błąd podczas zapisu do DB: ${e.message}")
            }
        }
    }

    fun getSprzedawca(sprzedawca: Sprzedawca, callback: (Sprzedawca?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val found = sprzedawcaRepository.getById(sprzedawca.id)
            callback(found)
        }
    }

    fun setSprzedawca(sprzedawca: Sprzedawca) {
        _actualSprzedawca.value = sprzedawca
    }
}
