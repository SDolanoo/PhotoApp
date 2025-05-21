package com.example.photoapp.ui.RaportFiskalny.Details

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.photoapp.database.data.DatabaseRepository
import com.example.photoapp.database.data.ProduktRaportFiskalny
import com.example.photoapp.database.data.RaportFiskalny
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import okhttp3.Callback
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

//RaportFiskalnyDetailsViewModel
@HiltViewModel
class RaportFiskalnyViewModel @Inject constructor(
    private val repository: DatabaseRepository
) : ViewModel() {

    private val _raport = mutableStateOf<RaportFiskalny?>(null)
    val raport = _raport

    private val _produkty = MutableStateFlow<List<ProduktRaportFiskalny>>(emptyList())
    val produkty: StateFlow<List<ProduktRaportFiskalny>> = _produkty.asStateFlow()

    var _editedProducts = mutableStateListOf<ProduktRaportFiskalny>()
    var _editedRaport = mutableStateListOf<RaportFiskalny>()

    fun loadProducts(raport: RaportFiskalny) {
        viewModelScope.launch(Dispatchers.IO) {
            val fetchedProducts = repository.getProductForRaportFiskalny(raportFiskalnyId = raport.id)
            _produkty.value = fetchedProducts
            _editedProducts.clear()
            _editedProducts.addAll(fetchedProducts)
            _editedRaport.clear()
            _editedRaport.add(raport)
        }
    }

    fun loadOnlyProducts(raport: RaportFiskalny) {
        viewModelScope.launch(Dispatchers.IO) {
            val fetchedProducts =
                repository.getProductForRaportFiskalny(raportFiskalnyId = raport.id)
            _produkty.value = fetchedProducts
            _editedProducts.clear()
            _editedProducts.addAll(fetchedProducts)
        }
    }

    fun getRaportByID(id: Int, callback: (RaportFiskalny) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val raport = repository.getRaportFiskalnyByID(id)
            callback(raport)
        }
    }

    fun formatDate(date: Long?): String {
        return date?.let {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it)
        } ?: "N/A"
    }

    fun convertMillisToString(millis: Long): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(Date(millis))
    }

    fun convertMillisToDate(millis: Long): Date {
        return Date(millis)
    }

    fun deleteProduct(product: ProduktRaportFiskalny, callback:() -> Unit){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteProduktRaportFiskalny(product)
            callback()
        }
    }

    fun updateEditedRaport(index: Int, raport: RaportFiskalny, callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _editedRaport[index] = raport
            callback()
        }
    }

    fun updateEditedProduct(index: Int, product: ProduktRaportFiskalny, callback: () -> Unit){
        viewModelScope.launch(Dispatchers.IO) {
            _editedProducts[index] = product
            callback()
        }
    }

    fun updateAllProductsAndRaports(callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _editedRaport.forEach { raport ->
                repository.updateRaportFiskalny(raport)
            }
            _editedProducts.forEach { produkt ->
                repository.updateProduktRaportFiskalny(produkt)
            }
            callback()
        }
    }

    fun addOneProduct(rfId: Int, nrPLU: String, ilosc: String, callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val produkt = ProduktRaportFiskalny(
                raportFiskalnyId = rfId,
                nrPLU = nrPLU,
                ilosc = ilosc
            )
            repository.insertProduktRaportFiskalny(produkt)
            callback()
        }
    }

    fun setRaport(raport: RaportFiskalny) {
        _raport.value = raport
    }
}
