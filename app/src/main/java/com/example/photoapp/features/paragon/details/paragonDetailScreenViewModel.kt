package com.example.photoapp.features.paragon.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoapp.features.paragon.data.ParagonRepository
import com.example.photoapp.features.paragon.data.ProduktParagon
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ParagonDetailsScreenViewModel @Inject constructor(
    private val repository: ParagonRepository
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
