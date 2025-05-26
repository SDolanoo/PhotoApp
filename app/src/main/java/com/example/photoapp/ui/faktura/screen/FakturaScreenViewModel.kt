package com.example.photoapp.ui.faktura.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoapp.database.DatabaseViewModel
import com.example.photoapp.database.data.Faktura
import com.example.photoapp.utils.normalizedDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class FakturaScreenViewModel @Inject constructor(
    private val databaseViewModel: DatabaseViewModel
) : ViewModel() {

    private val _groupedFaktury = MutableStateFlow<Map<Date?, List<Faktura>>>(emptyMap())
    val groupedFaktury: StateFlow<Map<Date?, List<Faktura>>> = _groupedFaktury

    fun loadFaktury(showFiltered: Boolean, filteredList: List<Faktura>) {
        viewModelScope.launch {
            val source = if (showFiltered) filteredList else databaseViewModel.allLiveFaktura.value.orEmpty()
            _groupedFaktury.value = source
                .sortedByDescending { it.dataWystawienia }
                .groupBy { it.dataWystawienia?.normalizedDate() }
        }
    }
}
