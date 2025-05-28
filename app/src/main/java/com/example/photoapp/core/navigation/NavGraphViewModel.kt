package com.example.photoapp.core.navigation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import coil3.Bitmap
import android.net.Uri
import com.example.photoapp.features.faktura.data.Faktura
import com.example.photoapp.features.paragon.data.Paragon
import com.example.photoapp.features.raportFiskalny.data.RaportFiskalny

class NavGraphViewModel : ViewModel() {
    // State for photo URI and Bitmap
    private val _photoUri = MutableLiveData<Uri?>(null)
    val photoUri: LiveData<Uri?> get() = _photoUri

    private val _photoBitmap = MutableLiveData<Bitmap?>(null)
    val photoBitmap: LiveData<Bitmap?> get() = _photoBitmap

    // For photo and acceptance
    private val _addingPhotoFor = MutableLiveData<String>(null)
    val addingPhotoFor: LiveData<String> get() = _addingPhotoFor

    // State for the current paragon viewed
    private val _paragonViewedNow = MutableLiveData<Paragon?>(null)
    val paragonViewedNow: LiveData<Paragon?> get() = _paragonViewedNow

    // State for filtered paragons
    private val _showFilteredParagons = MutableLiveData(false)
    val showFilteredParagons: LiveData<Boolean> get() = _showFilteredParagons

    private val _paragonFilteredList = MutableLiveData<List<Paragon>>(emptyList())
    val paragonFilteredList: LiveData<List<Paragon>> get() = _paragonFilteredList

    // State for the current faktura viewed
    private val _fakturaViewedNow = MutableLiveData<Faktura?>(null)
    val fakturaViewedNow: LiveData<Faktura?> get() = _fakturaViewedNow

    // State for filtered fakturys
    private val _showFilteredFakturys = MutableLiveData(false)
    val showFilteredFakturys: LiveData<Boolean> get() = _showFilteredFakturys

    private val _fakturaFilteredList = MutableLiveData<List<Faktura>>(emptyList())
    val fakturaFilteredList: LiveData<List<Faktura>> get() = _fakturaFilteredList

    private val _currentlyShowing = MutableLiveData("paragon")
    val currentlyShowing: LiveData<String> get() = _currentlyShowing

    private val _raportFiskalnyViewedNow = MutableLiveData<RaportFiskalny?>(null)
    val raportFiskalnyViewedNow: MutableLiveData<RaportFiskalny?> get() = _raportFiskalnyViewedNow

    // State for filtered Raport Fiskalny
    private val _showFilteredRaportyFiskalne = MutableLiveData(false)
    val showFilteredRaportyFiskalne: LiveData<Boolean> get() = _showFilteredRaportyFiskalne

    private val _raportFiskalnyFilteredList = MutableLiveData<List<RaportFiskalny>>(emptyList())
    val raportFiskalnyFilteredList: LiveData<List<RaportFiskalny>> get() = _raportFiskalnyFilteredList

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

    fun setParagonViewedNow(paragon: Paragon) {
        _paragonViewedNow.postValue(paragon)
    }

    fun setFilters(showFilters: Boolean, filteredList: List<Paragon>) {
        _showFilteredParagons.postValue(showFilters)
        _paragonFilteredList.postValue(filteredList)
    }

    fun setFakturaViewedNow(faktura: Faktura) {
        _fakturaViewedNow.postValue(faktura)
    }

    fun setFakturyFilters(showFilters: Boolean, filteredList: List<Faktura>) {
        _showFilteredFakturys.postValue(showFilters)
        _fakturaFilteredList.postValue(filteredList)
    }

    fun setRaportFiskalnyViewedNow(raport: RaportFiskalny) {
        _raportFiskalnyViewedNow.postValue(raport)
    }

    fun setRaportFiskalnyFilters(showFilters: Boolean, filteredList: List<RaportFiskalny>) {
        _showFilteredRaportyFiskalne.postValue(showFilters)
        _raportFiskalnyFilteredList.postValue(filteredList)
    }

    fun setCurrenltyShowing(what: String) {
        if ( what == "paragon" || what == "faktura") {
            _currentlyShowing.postValue(what)
        }
    }
}