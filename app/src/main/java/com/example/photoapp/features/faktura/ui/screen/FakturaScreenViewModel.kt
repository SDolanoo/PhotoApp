package com.example.photoapp.features.faktura.ui.screen

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoapp.features.faktura.data.Faktura
import com.example.photoapp.features.faktura.data.FakturaRepository
import com.example.photoapp.core.utils.normalizedDate
import com.example.photoapp.features.raportFiskalny.data.RaportFiskalny
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject
import kotlin.collections.forEach
import kotlin.collections.orEmpty
import kotlin.collections.sortedByDescending

@HiltViewModel
class FakturaScreenViewModel @Inject constructor(
    private val repository: FakturaRepository
) : ViewModel() {

    val allFakturyLive: LiveData<List<Faktura>> = repository.getAllLiveFaktury()

    private val _isDeleteMode = mutableStateOf(false)
    val isDeleteMode: State<Boolean> = _isDeleteMode

    private val _selectedItems = mutableStateListOf<Faktura>()
    val selectedItems: List<Faktura> get() = _selectedItems

    fun getGroupedFakturaList(fakturyList: List<Faktura>): Map<Date?, List<Faktura>> {
        return fakturyList
            .sortedByDescending { it.dataWystawienia }
            .groupBy { raport -> raport.dataWystawienia?.normalizedDate() }
    }

    fun getCountForProductsForRaport(raport: Faktura): Int {
        return runBlocking {
            withContext(Dispatchers.IO) {
                repository.getProduktyForFaktura(raport.id).size
            }
        }
    }

    fun toggleDeleteMode() {
        _isDeleteMode.value = !_isDeleteMode.value
        if (!_isDeleteMode.value) _selectedItems.clear() // Reset selection when exiting delete mode
    }

    fun toggleItemSelection(item: Faktura) {
        if (_selectedItems.contains(item)) {
            _selectedItems.remove(item)
        } else {
            _selectedItems.add(item)
        }
    }

    fun deleteSelectedItems() {
        viewModelScope.launch(Dispatchers.IO) {
            selectedItems.forEach { item ->
                Log.i("Dolan", "IM IN VIEMODEL GOING TO REPO")
                repository.deleteFaktura(item) // Assuming a delete function exists
            }
            _selectedItems.clear()
            _isDeleteMode.value = false // Exit delete mode after deleting
        }
    }


    private val _groupedFaktury = MutableStateFlow<Map<Date?, List<Faktura>>>(emptyMap())
    val groupedFaktury: StateFlow<Map<Date?, List<Faktura>>> = _groupedFaktury

    fun loadFaktury(showFiltered: Boolean, filteredList: List<Faktura>) {
        viewModelScope.launch {
            val source = if (showFiltered) filteredList else repository.getAllLiveFaktury().value.orEmpty()
            _groupedFaktury.value = source
                .sortedByDescending { it.dataWystawienia }
                .groupBy { it.dataWystawienia?.normalizedDate() }
        }
    }
}
