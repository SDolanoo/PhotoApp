package com.example.photoapp.features.paragon.ui.screen

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoapp.features.paragon.data.Paragon
import com.example.photoapp.features.paragon.data.ParagonRepository
import com.example.photoapp.core.utils.normalizedDate
import com.example.photoapp.ui.FilterScreen.FilterResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject
import kotlin.collections.sortedByDescending



@HiltViewModel
class ParagonScreenViewModel @Inject constructor(
    private val repository: ParagonRepository
) : ViewModel() {

    val allParagony: LiveData<List<Paragon>> = repository.getAllLiveParagony()

    private val _filteredParagony = MutableStateFlow<List<Paragon>>(emptyList())
    val filteredParagony: StateFlow<List<Paragon>> = _filteredParagony

    private val _isDeleteMode = mutableStateOf(false)
    val isDeleteMode: State<Boolean> = _isDeleteMode

    private val _selectedItems = mutableStateListOf<Paragon>()
    val selectedItems: List<Paragon> get() = _selectedItems

    fun getGroupedParagonsList(paragonList: List<Paragon>): Map<Date?, List<Paragon>> {
        return paragonList
            .sortedByDescending { it.dataZakupu }
            .groupBy { paragon -> paragon.dataZakupu?.normalizedDate() }
    }

    fun toggleDeleteMode() {
        _isDeleteMode.value = !_isDeleteMode.value
        if (!_isDeleteMode.value) _selectedItems.clear()
    }

    fun toggleItemSelection(paragon: Paragon) {
        if (_selectedItems.contains(paragon)) {
            _selectedItems.remove(paragon)
        } else {
            _selectedItems.add(paragon)
        }
    }

    fun deleteSelectedItems() {
        viewModelScope.launch(Dispatchers.IO) {
            selectedItems.forEach { repository.deleteParagon(it) }
            _selectedItems.clear()
            _isDeleteMode.value = false
        }
    }

    fun applyParagonFilters(filterResult: FilterResult) {
        val (startDate, endDate) = filterResult.startDate to filterResult.endDate
        val (minPrice, maxPrice) = filterResult.minPrice to filterResult.maxPrice

        val filtered = allParagony.value.filter { paragon ->
            val matchesDate = when {
                startDate == null && endDate == null -> true
                startDate != null && endDate != null ->
                    paragon.dataZakupu?.after(startDate) == true &&
                            paragon.dataZakupu?.before(endDate) == true
                else -> true
            }

            val matchesPrice = when {
                minPrice == null && maxPrice == null -> true
                minPrice != null && maxPrice != null ->
                    paragon.kwotaCalkowita in minPrice..maxPrice
                minPrice != null -> paragon.kwotaCalkowita >= minPrice
                maxPrice != null -> paragon.kwotaCalkowita <= maxPrice
                else -> true
            }

            matchesDate && matchesPrice
        }

        // Update filtered UI state
        _filteredParagony.value = filtered
    }

}