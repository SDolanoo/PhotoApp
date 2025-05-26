package com.example.photoapp.features.faktura.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoapp.database.DatabaseViewModel
import com.example.photoapp.features.faktura.data.Faktura
import com.example.photoapp.features.faktura.data.FakturaRepository
import com.example.photoapp.utils.normalizedDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject
import kotlin.collections.orEmpty
import kotlin.collections.sortedByDescending

@HiltViewModel
class FakturaScreenViewModel @Inject constructor(
    private val repository: FakturaRepository
) : ViewModel() {

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
