package com.example.photoapp.ui.FilterScreen

import android.app.ProgressDialog.show
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoapp.core.utils.convertStringToDate
import com.example.photoapp.features.faktura.data.faktura.Faktura
import com.example.photoapp.features.faktura.data.faktura.FakturaRepository
import com.example.photoapp.features.faktura.data.faktura.Produkt
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class FilterState(
    var isWystawienia: Boolean, // else Sprzedazy
    var fromDate: String = "",
    var toDate: String = "",
    val isFromDateValid: Boolean = true,
    val isToDateValid: Boolean = true,
    var isGross: Boolean = false,
    var minPrice: String = "",
    var maxPrice: String = "",
    var isMinPriceValid: Boolean = true,
    var isMaxPriceValid: Boolean = true,
    var buyer: String = "",
    var seller: String = "",
    var product: String = ""
) {
    companion object {
        fun default() = FilterState(
            isWystawienia = true,
            fromDate = "",
            toDate = "",
            isGross = true,
            minPrice = "",
            maxPrice = "",
            buyer = "",
            seller = "",
            product = "",
        )
    }
}

@HiltViewModel
class FilterController @Inject constructor(
    private val fakturaRepository: FakturaRepository
) : ViewModel() {

    // ✅ Cały stan w jednym miejscu
    private val _filterState = MutableStateFlow(FilterState.default())
    val filterState: StateFlow<FilterState> = _filterState.asStateFlow()

    // 🧩 Proste API do aktualizacji dowolnego pola
    fun updateFilter(update: FilterState.() -> Unit) {
        _filterState.value = _filterState.value.copy().apply(update)
    }

//    fun clearAllFilters() {
//        _filterState.value = FilterState()
//    }

    fun convertStringToDatePair(): Pair<Date?, Date?> {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return try {
            val from = _filterState.value.fromDate.takeIf { it.isNotBlank() }?.let { formatter.parse(it) }
            val to = _filterState.value.toDate.takeIf { it.isNotBlank() }?.let { formatter.parse(it) }
            Pair(from, to)
        } catch (e: Exception) {
            Pair(null, null)
        }
    }

    fun isAllValid(filterState: FilterState): Boolean {
        val state = filterState
        Log.i("Dolan", "$state")
        return state.isMinPriceValid &&
                state.isMaxPriceValid &&
                state.isFromDateValid &&
                state.isToDateValid
    }

    fun getFormattedDateRange(): Pair<String, String> {
        return Pair(_filterState.value.fromDate, _filterState.value.toDate)
    }

    fun getMessageForSnackbar(): String {
        val f = _filterState.value
        return "Filter: Date: ${f.fromDate} → ${f.toDate} (${if (f.isWystawienia) "Wystawienia" else "Sprzedazy"}), Price: ${f.minPrice}–${f.maxPrice} (${if (f.isGross) "Gross" else "Net"})"
    }

    // ✅ Główna funkcja filtrowania
    fun applyFakturysFilters(filterState: FilterState): List<Faktura> {

        val f = filterState
        val startDate = convertStringToDate(f.fromDate)
        val endDate = convertStringToDate(f.toDate)
        val minPrice = f.minPrice.toDoubleOrNull()
        val maxPrice = f.maxPrice.toDoubleOrNull()

        val filterDateField = if (f.isWystawienia) "dataWystawienia" else "dataSprzedazy"

        val priceType = if (f.isGross) "brutto" else "netto"

        val resultList = fakturaRepository.fetchFilteredFaktury(
            startDate = startDate,
            endDate = endDate,
            minPrice = minPrice,
            maxPrice = maxPrice,
            filterDate = filterDateField,
            filterPrice = priceType
        )
        Log.i("Dolan", "RESULT LIST = $resultList")
        return resultList
    }
}