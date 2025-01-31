package com.example.photoapp.ui.RaportFiskalny.Details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoapp.database.data.DatabaseRepository
import com.example.photoapp.database.data.ProduktRaportFiskalny
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class RaportFiskalnyViewModel @Inject constructor(
    private val repository: DatabaseRepository
) : ViewModel() {

    private val _produkty = MutableStateFlow<List<ProduktRaportFiskalny>>(emptyList())
    val produkty: StateFlow<List<ProduktRaportFiskalny>> = _produkty.asStateFlow()

    fun loadProducts(raportFiskalnyId: Int) {
        viewModelScope.launch {
            _produkty.value = repository.getProductForRaportFiskalny(raportFiskalnyId)
        }
    }

    fun formatDate(date: Long?): String {
        return date?.let {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it)
        } ?: "N/A"
    }
}
