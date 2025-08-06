package com.example.photoapp.features.selector.presentation.selector

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoapp.features.faktura.data.faktura.FakturaRepository
import com.example.photoapp.features.odbiorca.data.Odbiorca
import com.example.photoapp.features.odbiorca.data.OdbiorcaRepository
import com.example.photoapp.features.produkt.data.Produkt
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
class SelectorViewModel @Inject constructor(
    private val fakturaRepository: FakturaRepository,
    private val odbiorcaRepository: OdbiorcaRepository,
    private val sprzedawcaRepository: SprzedawcaRepository
): ViewModel() {
    private val _allProdukty = MutableStateFlow<List<Produkt>>(emptyList())
    val allProdukty: StateFlow<List<Produkt>> = _allProdukty.asStateFlow()

    private val _allOdbiorcy = MutableStateFlow<List<Odbiorca>>(emptyList())
    val allOdbiorcy: StateFlow<List<Odbiorca>> = _allOdbiorcy.asStateFlow()

    private val _allSprzedawcy = MutableStateFlow<List<Sprzedawca>>(emptyList())
    val allSprzedawcy: StateFlow<List<Sprzedawca>> = _allSprzedawcy.asStateFlow()

    fun updateLists() {
        getProdukty()
        getOdbiorcy()
        getSprzedawcy()
    }

    fun getProdukty() {
        viewModelScope.launch(Dispatchers.IO) {
            _allProdukty.value = fakturaRepository.getAllProdukty()
        }
    }

    fun getOdbiorcy() {
        viewModelScope.launch(Dispatchers.IO) {
            _allOdbiorcy.value = odbiorcaRepository.getAllOdbiorcy()
        }
    }

    fun getSprzedawcy() {
        viewModelScope.launch(Dispatchers.IO) {
            _allSprzedawcy.value = sprzedawcaRepository.getAll()
        }
    }
}