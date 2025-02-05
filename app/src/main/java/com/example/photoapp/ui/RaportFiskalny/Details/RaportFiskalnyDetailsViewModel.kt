package com.example.photoapp.ui.RaportFiskalny.Details

import androidx.compose.runtime.mutableStateListOf
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
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class RaportFiskalnyViewModel @Inject constructor(
    private val repository: DatabaseRepository
) : ViewModel() {

    private val _produkty = MutableStateFlow<List<ProduktRaportFiskalny>>(emptyList())
    val produkty: StateFlow<List<ProduktRaportFiskalny>> = _produkty.asStateFlow()

    var _editedProducts = mutableStateListOf<ProduktRaportFiskalny>()

    fun loadProducts(raportFiskalnyId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val fetchedProducts = repository.getProductForRaportFiskalny(raportFiskalnyId)
            _produkty.value = fetchedProducts
            _editedProducts.clear()
            _editedProducts.addAll(fetchedProducts)
        }
    }

    fun formatDate(date: Long?): String {
        return date?.let {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it)
        } ?: "N/A"
    }

    fun deleteProduct(product: ProduktRaportFiskalny, callback:() -> Unit){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteProduktRaportFiskalny(product)
            callback()
        }
    }

//    fun updateProduct(product: ProduktRaportFiskalny, callback: () -> Unit){
//        viewModelScope.launch(Dispatchers.IO) {
//            repository.updateProduktRaportFiskalny(product)
//            callback()
//        }
//    }

    fun updateEditedProduct(index: Int, product: ProduktRaportFiskalny, callback: () -> Unit){
        viewModelScope.launch(Dispatchers.IO) {
            _editedProducts[index] = product
            callback()
        }
    }

    fun updateAllProducts(callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
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

//    fun setRaportInDBReporsitory(raport: RaportFiskalny, callback: () -> Unit){
//        viewModelScope.launch(Dispatchers.Main) {
//            repository.setRaportID(raport)
//            callback()
//        }
//    }
}
