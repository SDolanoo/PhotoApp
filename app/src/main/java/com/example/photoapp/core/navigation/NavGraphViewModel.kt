package com.example.photoapp.core.navigation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import coil3.Bitmap
import android.net.Uri
import android.util.Log
import com.example.photoapp.features.faktura.data.faktura.Faktura
import com.example.photoapp.features.faktura.data.faktura.Produkt
import com.example.photoapp.features.faktura.presentation.details.ProduktFakturaZProduktem
import com.example.photoapp.features.odbiorca.data.Odbiorca
import com.example.photoapp.features.sprzedawca.data.Sprzedawca
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class NavGraphViewModel @Inject constructor() : ViewModel() {
    // State for photo URI and Bitmap
    private val _photoUri = MutableLiveData<Uri?>(null)
    val photoUri: LiveData<Uri?> get() = _photoUri

    private val _photoBitmap = MutableLiveData<Bitmap?>(null)
    val photoBitmap: LiveData<Bitmap?> get() = _photoBitmap

    // For photo and acceptance
    private val _addingPhotoFor = MutableLiveData<String>(null)
    val addingPhotoFor: LiveData<String> get() = _addingPhotoFor

    // State for the current faktura viewed
    private val _fakturaViewedNow = MutableStateFlow<Faktura>(Faktura.default())
    val fakturaViewedNow: StateFlow<Faktura> = _fakturaViewedNow.asStateFlow()

    // State for filtered fakturys
    private val _showFilteredFakturys = MutableLiveData(false)
    val showFilteredFakturys: LiveData<Boolean> get() = _showFilteredFakturys

    private val _fakturaFilteredList = MutableLiveData<List<Faktura>>(emptyList())
    val fakturaFilteredList: LiveData<List<Faktura>> get() = _fakturaFilteredList

    private val _currentlyShowing = MutableLiveData("paragon")
    val currentlyShowing: LiveData<String> get() = _currentlyShowing

    private val _faktura = MutableStateFlow<Faktura>(Faktura.default())
    val faktura: StateFlow<Faktura> = _faktura.asStateFlow()

    private val _sprzedawca = MutableStateFlow<Sprzedawca>(Sprzedawca.empty())
    val sprzedawca: StateFlow<Sprzedawca> = _sprzedawca.asStateFlow()

    private val _odbiorca = MutableStateFlow<Odbiorca>(Odbiorca.empty())
    val odbiorca: StateFlow<Odbiorca> = _odbiorca.asStateFlow()

    private val _produkty = MutableStateFlow<List<ProduktFakturaZProduktem>>(emptyList())
    val produkty: StateFlow<List<ProduktFakturaZProduktem>> = _produkty.asStateFlow()

    private val _produkt = MutableStateFlow<Produkt>(Produkt.default())
    val produkt: StateFlow<Produkt> = _produkt.asStateFlow()

    // Functions to update states
    fun setPhotoUri(uri: Uri) {
        _photoUri.postValue(uri)
    }

    fun setPhotoBitmap(bitmap: Bitmap) {
        _photoBitmap.postValue(bitmap)
    }

    fun setAddingPhotoFor(paragonOrFaktura: String) {
        _addingPhotoFor.postValue(paragonOrFaktura)
    }

    fun setFakturaViewedNow(faktura: Faktura) {
        _fakturaViewedNow.value = faktura
    }

    fun setFakturyFilters(showFilters: Boolean, filteredList: List<Faktura>) {
        _showFilteredFakturys.postValue(showFilters)
        _fakturaFilteredList.postValue(filteredList)
    }

    fun setCurrenltyShowing(what: String) {
        if ( what == "paragon" || what == "faktura") {
            _currentlyShowing.postValue(what)
        }
    }

    fun setFaktura(faktura: Faktura, callback: () -> Unit) {
        _faktura.value = faktura
        callback()
    }

    fun setSprzedawca(sprzedawca: Sprzedawca, callback: () -> Unit) {
        _sprzedawca.value = sprzedawca
        callback()
    }

    fun setOdbiorca(odbiorca: Odbiorca, callback: () -> Unit) {
        _odbiorca.value = odbiorca
        Log.i("Dolan", "Odbiorca Set = $odbiorca")
        callback()
    }

    fun setProdukty(produkty: List<ProduktFakturaZProduktem>, callback: () -> Unit) {
        _produkty.value = produkty
        callback()
    }

    fun addProdukt(produkt: ProduktFakturaZProduktem) {
        _produkty.update { currentList -> currentList + produkt }
    }

    fun removeProdukt(produkt: ProduktFakturaZProduktem) {
        _produkty.update { currentList -> currentList - produkt }
    }

    fun setProdukt(produkt: Produkt, callback: () -> Unit) {
        _produkt.value = produkt
        callback()
    }
}