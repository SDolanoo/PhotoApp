package com.example.photoapp.archive.features.raportFiskalny.ui.screen

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoapp.archive.features.raportFiskalny.data.RaportFiskalny
import com.example.photoapp.archive.features.raportFiskalny.data.RaportFiskalnyRepository
import com.example.photoapp.core.utils.normalizedDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject
import kotlin.collections.sortedByDescending

@HiltViewModel
class RaportFiskalnyScreenViewModel @Inject constructor(
    private val repository: RaportFiskalnyRepository
) : ViewModel() {

    val allRaportFiskalny: LiveData<List<RaportFiskalny>> = repository.getAllLiveRaporty()

    private val _isDeleteMode = mutableStateOf(false)
    val isDeleteMode: State<Boolean> = _isDeleteMode

    private val _selectedItems = mutableStateListOf<RaportFiskalny>()
    val selectedItems: List<RaportFiskalny> get() = _selectedItems

    fun getGroupedRaportFiskalnyList(raportFiskalnyList: List<RaportFiskalny>): Map<Date?, List<RaportFiskalny>> {
        return raportFiskalnyList
            .sortedByDescending { it.dataDodania }
            .groupBy { raport -> raport.dataDodania?.normalizedDate() }
    }

    fun getCountForProductsForRaport(raport: RaportFiskalny): Int {
        return runBlocking {
            withContext(Dispatchers.IO) {
                repository.getProduktyForRaportId(raport.id).size
            }
        }
    }

    fun toggleDeleteMode() {
        _isDeleteMode.value = !_isDeleteMode.value
        if (!_isDeleteMode.value) _selectedItems.clear() // Reset selection when exiting delete mode
    }

    fun toggleItemSelection(item: RaportFiskalny) {
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
                repository.deleteRaport(item) // Assuming a delete function exists
            }
            _selectedItems.clear()
            _isDeleteMode.value = false // Exit delete mode after deleting
        }
    }
}
