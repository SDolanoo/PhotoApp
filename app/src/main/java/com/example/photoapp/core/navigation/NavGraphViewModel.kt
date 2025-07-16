package com.example.photoapp.core.navigation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import coil3.Bitmap
import android.net.Uri
import com.example.photoapp.features.faktura.data.faktura.Faktura
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val _fakturaViewedNow = MutableLiveData<Faktura?>(null)
    val fakturaViewedNow: LiveData<Faktura?> get() = _fakturaViewedNow

    // State for filtered fakturys
    private val _showFilteredFakturys = MutableLiveData(false)
    val showFilteredFakturys: LiveData<Boolean> get() = _showFilteredFakturys

    private val _fakturaFilteredList = MutableLiveData<List<Faktura>>(emptyList())
    val fakturaFilteredList: LiveData<List<Faktura>> get() = _fakturaFilteredList

    private val _currentlyShowing = MutableLiveData("paragon")
    val currentlyShowing: LiveData<String> get() = _currentlyShowing

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
        _fakturaViewedNow.postValue(faktura)
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
}