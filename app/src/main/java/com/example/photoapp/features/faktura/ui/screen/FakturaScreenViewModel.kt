package com.example.photoapp.features.faktura.ui.screen

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoapp.archive.features.paragon.data.Paragon
import com.example.photoapp.features.faktura.data.Faktura
import com.example.photoapp.features.faktura.data.FakturaRepository
import com.example.photoapp.core.utils.normalizedDate
import com.example.photoapp.ui.FilterScreen.FilterResult
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

    private val _filteredFaktury = MutableStateFlow<List<Faktura>>(emptyList())
    val filteredFaktury: StateFlow<List<Faktura>> = _filteredFaktury

    private val _isDeleteMode = mutableStateOf(false)
    val isDeleteMode: State<Boolean> = _isDeleteMode

    private val _selectedItems = mutableStateListOf<Faktura>()
    val selectedItems: List<Faktura> get() = _selectedItems

    private val _groupedFaktury = MutableStateFlow<Map<Date?, List<Faktura>>>(emptyMap())
    val groupedFaktury: StateFlow<Map<Date?, List<Faktura>>> = _groupedFaktury

    fun getGroupedFakturaList(fakturyList: List<Faktura>): Map<Date?, List<Faktura>> {
        return fakturyList
            .sortedByDescending { it.dataWystawienia }
            .groupBy { raport -> raport.dataWystawienia?.normalizedDate() }
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

    fun applyFakturaFilters(filterResult: FilterResult) {
        val (startDate, endDate) = filterResult.startDate to filterResult.endDate
        val (minPrice, maxPrice) = filterResult.minPrice to filterResult.maxPrice

        val filtered = allFakturyLive.value.filter { faktura ->
            val matchesDate = when {
                startDate == null && endDate == null -> true
                startDate != null && endDate != null ->
                    faktura.dataWystawienia?.after(startDate) == true &&
                            faktura.dataWystawienia?.before(endDate) == true
                else -> true
            }

            val matchesPrice = when {
                minPrice == null && maxPrice == null -> true
                minPrice != null && maxPrice != null ->
                    faktura.razemBrutto.toDouble() in minPrice..maxPrice
                minPrice != null -> faktura.razemBrutto.toDouble() >= minPrice
                maxPrice != null -> faktura.razemBrutto.toDouble() <= maxPrice
                else -> true
            }

            matchesDate && matchesPrice
        }

        // Update filtered UI state
        _filteredFaktury.value = filtered
    }
}
