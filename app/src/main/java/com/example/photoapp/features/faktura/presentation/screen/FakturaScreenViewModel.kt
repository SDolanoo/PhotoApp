package com.example.photoapp.features.faktura.presentation.screen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoapp.features.faktura.data.faktura.FakturaRepository
import com.example.photoapp.core.utils.normalizedDate
import com.example.photoapp.features.faktura.data.faktura.Faktura
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject
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

    private val _groupedFaktury = MutableStateFlow<Map<Date?, List<Faktura>>>(getGroupedFakturaList(repository.getAllFaktury()))
    val groupedFaktury: StateFlow<Map<Date?, List<Faktura>>> = _groupedFaktury

    fun getGroupedFakturaList(fakturyList: List<Faktura>): Map<Date?, List<Faktura>> {
        return fakturyList
            .sortedByDescending { it.dataWystawienia }
            .groupBy { raport -> raport.dataWystawienia?.normalizedDate() }
    }

    fun setGroupedFaktura(fakturyList: Map<Date?, List<Faktura>>) {
        _groupedFaktury.value = fakturyList
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
            selectedItems.forEach { repository.deleteFaktura(it) }
            _selectedItems.clear()
            _isDeleteMode.value = false // Exit delete mode after deleting
        }
    }

    fun applyFilters(filtered: List<Faktura>) {
        _groupedFaktury.value = getGroupedFakturaList(filtered)
    }

    fun clearFilters() {
        _groupedFaktury.value = getGroupedFakturaList(allFakturyLive.value!!)
    }

    fun getCurrentlyShowingList(): List<Faktura> {
        val fakturaLists: List<Faktura> = groupedFaktury.value.values.flatten()
        return fakturaLists
    }
}
