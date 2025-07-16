package com.example.photoapp.ui.FilterScreen

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.photoapp.features.faktura.data.faktura.FakturaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

// FilterController Class
@HiltViewModel
class FilterController @Inject constructor(
    fakturaRepository: FakturaRepository
) : ViewModel() {

    private val _currentFilter = mutableStateOf("paragon")
    val currentFilter = _currentFilter

    private val _dateRange = mutableStateOf<Pair<Date?, Date?>>(Pair(null, null))
    val dateRange = _dateRange

    private val _dateSelectedOption = mutableStateOf("")
    val dateSelectedOption = _dateSelectedOption

    private val _currentFakturyDateFilter = mutableStateOf("dataWystawienia")
    val currentFakturyDateFilter = _currentFakturyDateFilter

    private val _priceRange = mutableStateOf<Pair<Double?, Double?>>(Pair(null, null))
    val priceRange = _priceRange

    private val _priceSelectedOption = mutableStateOf("")
    val priceSelectedOption = _priceSelectedOption

    private val _currentFakturyPriceFilter = mutableStateOf("brutto")
    val currentFakturyPriceFilter = _currentFakturyPriceFilter

    fun changeFilter(filter: String) {
        _currentFilter.value = filter
    }

    fun setDateRange(range: Pair<Date?, Date?>) {
        _dateRange.value = range
    }

    fun setDateSelectedOption(option: String) {
        _dateSelectedOption.value = option
    }

    fun setCurrentFakturyDateFilter(filter: String) { // filter: dataWystawienia or dataSprzedazy
        _currentFakturyDateFilter.value = filter
    }

    fun setPriceRange(range: Pair<Double?, Double?>) {
        _priceRange.value = range
    }

    fun setPriceSelectedOption(option: String) {
        _priceSelectedOption.value = option
    }

    fun setCurrentFakturyPriceFilter(filter: String) { // filter: brutto or netto
        _currentFakturyPriceFilter.value = filter
    }

    fun clearAllValues() {
        _dateRange.value = Pair(null, null)
        _priceRange.value = Pair(null, null)
    }

    fun convertStringToDate(strings: Pair<String, String>): Pair<Date?, Date?> {
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        val (x, y) = strings
        val dateFrom = formatter.parse(x)
        val dateTo = formatter.parse(y)
        return Pair(dateFrom, dateTo)
    }

    fun convertMillisToString(millis: Long): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return formatter.format(Date(millis))
    }

    fun formatCurrentDateRange(): Pair<Any?, Any?> {
        val (x, y) = dateRange.value
        if (x != null && y != null) {
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val from = formatter.format(x)
            val to = formatter.format(y)
            return Pair(from, to)
        }
        return Pair(x, y)
    }

    fun getMessageForSnackbar(): String {
        // Logic for showing Snackbar for testing purposes
        return "Applying filters: CurrentFilter ${_currentFilter.value}, Date: ${formatCurrentDateRange()}, Price:  ${_priceRange.value}"
    }

    fun canShowFilters(): Boolean {
        if (dateRange == Pair(null, null) && priceRange == Pair(null, null)) {
            Log.i("Dolan", "canShowFilters = false")
            return false
        }
        Log.i("Dolan", "canShowFilters = true")
        return true
    }

//    fun applyFakturysFilters():Pair<Boolean, List<Faktura>> {
////        Log.i("Dolan", "applyFilters FUNCTION")
////        val (startDate, endDate) = dateRange.value
////        Log.i("Dolan", "startDate: $startDate, endDate: $endDate")
////        val (minPrice, maxPrice) = priceRange.value
////        Log.i("Dolan", "minPrice: $minPrice, maxPrice: $maxPrice")
////
////
////        val resultList = fakturaRepository.fetchFilteredFaktury(
////            startDate = startDate,
////            endDate = endDate,
////            minPrice = minPrice,
////            maxPrice = maxPrice,
////            filterDate = currentFakturyDateFilter.value,
////            filterPrice = currentFakturyPriceFilter.value
////        )
////
////        val doShowFilters = canShowFilters()
////        Log.i("Dolan", "doShowFilters: $doShowFilters, resultList: $resultList")
//        var doShowFilters = true
//        var resultList: List<Faktura> = listOf(Faktura(
//            uzytkownikId = "TODO()",
//            odbiorcaId = TODO(),
//            sprzedawcaId = TODO(),
//            numerFaktury = TODO(),
//            typFaktury = TODO(),
//            dataWystawienia = TODO(),
//            dataSprzedazy = TODO(),
//            terminPlatnosci = TODO(),
//            razemNetto = TODO(),
//            razemVAT = TODO(),
//            razemBrutto = TODO(),
//            doZaplaty = TODO(),
//            waluta = TODO(),
//            formaPlatnosci = TODO()
//        ))
//        return Pair(doShowFilters, resultList)
//    }
}