package com.example.photoapp.features.selector.presentation.selector.odbiorca.details

import android.util.Log
import android.util.Log.v
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoapp.features.odbiorca.data.Odbiorca
import com.example.photoapp.features.odbiorca.data.OdbiorcaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OdbiorcaDetailsViewModel @Inject constructor(
    private val odbiorcaRepository: OdbiorcaRepository
) : ViewModel() {
    // ---- ODBIORCA ----
    private val _actualOdbiorca = MutableStateFlow<Odbiorca>(Odbiorca.empty())
    val actualOdbiorca: StateFlow<Odbiorca> = _actualOdbiorca.asStateFlow()

    private val _editedOdbiorca = MutableStateFlow<Odbiorca>(Odbiorca.empty())
    val editedOdbiorca: StateFlow<Odbiorca> = _editedOdbiorca.asStateFlow()

    fun loadProducts(odbiorca: Odbiorca) {
        viewModelScope.launch(Dispatchers.IO) {
            val odbiorca = odbiorcaRepository.getById(odbiorca.id)
            // Ustawienie actual i edited jednocześnie
            _actualOdbiorca.value = odbiorca!!
            _editedOdbiorca.value = odbiorca

            Log.i("Dolan", odbiorca.toString())
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
            loadProducts(_actualOdbiorca.value)
        }
    }

    fun updateEditedOdbiorcaTemp(odbiorca: Odbiorca, callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _editedOdbiorca.value = odbiorca
            callback()
        }
    }

    fun updateAllEditedToDB(callback: (Odbiorca) -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val odbiorca = editedOdbiorca.value

                // 🔁 Aktualizacja sprzedawcy i odbiorcy
                val odbiorcaId = odbiorcaRepository.upsertOdbiorcaSmart(odbiorca)
                callback(odbiorca.copy(id = odbiorcaId))

            } catch (e: Exception) {
                Log.e("Dolan", "Błąd podczas zapisu do DB: ${e.message}")
            }
        }
    }

    fun getOdbiorca(odbiorca: Odbiorca, callback: (Odbiorca) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val odbiorca = odbiorcaRepository.getById(odbiorca.id)
            callback(odbiorca!!)
        }
    }

    fun setOdbiorca(odbiorca: Odbiorca) {
        _actualOdbiorca.value = odbiorca
    }
}