package com.example.photoapp.ui.paragon.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoapp.database.data.DatabaseRepository
import com.example.photoapp.database.data.ProduktParagon
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ParagonDetailsScreenViewModel @Inject constructor(
    private val repository: DatabaseRepository
) : ViewModel() {

    private val _products = MutableStateFlow<List<ProduktParagon>>(emptyList())
    val products: StateFlow<List<ProduktParagon>> = _products

    fun loadProductsForParagon(paragonId: Int) {
        viewModelScope.launch {
            val result = repository.getProductForParagon(paragonId)
            _products.value = result
        }
    }
}
