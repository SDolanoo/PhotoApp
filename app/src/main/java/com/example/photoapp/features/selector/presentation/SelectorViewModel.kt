package com.example.photoapp.features.selector.presentation

import androidx.lifecycle.ViewModel
import com.example.photoapp.features.faktura.data.faktura.FakturaRepository
import com.example.photoapp.features.faktura.data.faktura.Produkt
import com.example.photoapp.features.odbiorca.data.Odbiorca
import com.example.photoapp.features.odbiorca.data.OdbiorcaRepository
import com.example.photoapp.features.sprzedawca.data.Sprzedawca
import com.example.photoapp.features.sprzedawca.data.SprzedawcaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
        _allProdukty.value = fakturaRepository.getAllProdukty()
        _allOdbiorcy.value = odbiorcaRepository.getAllOdbiorcy()
        _allSprzedawcy.value = sprzedawcaRepository.getAll()
    }
}