package com.example.photoapp.features.raportFiskalny.ui.Details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoapp.features.raportFiskalny.data.ProduktRaportFiskalny
import com.example.photoapp.features.raportFiskalny.data.RaportFiskalny
import com.example.photoapp.features.raportFiskalny.data.RaportFiskalnyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.toMutableList

//RaportFiskalnyDetailsViewModel
@HiltViewModel
class RaportFiskalnyViewModel @Inject constructor(
    private val repository: RaportFiskalnyRepository
) : ViewModel() {

    private val _actualRaport = MutableStateFlow<RaportFiskalny?>(null)
    val actualRaport: StateFlow<RaportFiskalny?> = _actualRaport

    private val _actualProdukty = MutableStateFlow<List<ProduktRaportFiskalny>>(emptyList())
    val actualProdukty: StateFlow<List<ProduktRaportFiskalny>> = _actualProdukty.asStateFlow()

    private val _editedProdukty = MutableStateFlow<List<ProduktRaportFiskalny>>(emptyList())
    val editedProdukty: StateFlow<List<ProduktRaportFiskalny>> = _editedProdukty.asStateFlow()

    var _editedRaport = MutableStateFlow<RaportFiskalny?>(null)
    val editedRaport: StateFlow<RaportFiskalny?> = _editedRaport

    fun loadProducts(raport: RaportFiskalny) {
        viewModelScope.launch(Dispatchers.IO) {
            val fetchedProducts = repository.getProduktyForRaportId(id = raport.id)
            _actualProdukty.value = fetchedProducts
            _editedProdukty.value = fetchedProducts
            _editedRaport.value = raport
        }
    }


    fun loadOnlyProducts(raport: RaportFiskalny) {
        viewModelScope.launch(Dispatchers.IO) {
            val fetchedProducts =
                repository.getProduktyForRaportId(id = raport.id)
            _actualProdukty.value = fetchedProducts
            _editedProdukty.value = fetchedProducts
        }
    }

    fun getRaportByID(id: Int, callback: (RaportFiskalny) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val raport = repository.getRaportById(id)
            callback(raport)
        }
    }

    fun editingSuccess() {
        viewModelScope.launch(Dispatchers.IO) {
            updateToDBProductsAndRaports() {}
            loadProducts(_editedRaport.value!!)
        }
    }

    fun editingFailed() {
        viewModelScope.launch(Dispatchers.IO) {
            loadProducts(_actualRaport.value!!)
        }
    }

    fun deleteProduct(product: ProduktRaportFiskalny, callback:() -> Unit){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteProdukt(product)
            callback()
        }
    }

    fun updateEditedRaportTemp(raport: RaportFiskalny, callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _editedRaport.value = raport
            callback()
        }
    }

    fun updateEditedProductTemp(index: Int, product: ProduktRaportFiskalny, callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _editedProdukty.value = _editedProdukty.value.toMutableList().also {
                it[index] = product
            }
            callback()
        }
    }


    fun updateToDBProductsAndRaports(callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateRaport(_editedRaport.value!!)
            setRaport(_editedRaport.value!!)
            _editedProdukty.value.toMutableList().forEach { produkt ->
                repository.updateProdukt(produkt)
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
            repository.insertProdukt(produkt)
            callback()
        }
    }

    fun setRaport(raport: RaportFiskalny) {
        _actualRaport.value = raport
    }
}
