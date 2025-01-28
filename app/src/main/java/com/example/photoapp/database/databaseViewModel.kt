package com.example.photoapp.database

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoapp.AppApplication
import com.example.photoapp.database.di.AppDatabase
import com.example.photoapp.database.data.DatabaseRepository
import com.example.photoapp.database.data.Faktura
import com.example.photoapp.database.data.Paragon
import com.example.photoapp.database.data.ProduktFaktura
import com.example.photoapp.database.data.ProduktParagon
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject


@HiltViewModel
class DatabaseViewModel @Inject constructor(
    val databaseRepository: DatabaseRepository): ViewModel() {

    val allLiveParagony: LiveData<List<Paragon>> = databaseRepository.allLiveParagony
    val allLiveFaktura: LiveData<List<Faktura>> = databaseRepository.allLiveFaktury

    fun addUser(login: String, password: String, email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.addUser(login, password, email)
        }
    }

    fun addTestRecipe() {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.addTestRecipe()
        }
    }

    fun addTestRecipeProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.addTestRecipeProducts()
        }
    }

    fun addRecipe(jsonString: String) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.addRecipe(jsonString)
        }
    }

    fun addFaktura(jsonString: String) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.addFaktura(jsonString)
        }
    }

    fun getProductForParagon(paragonID: Int): List<ProduktParagon>  {
        return runBlocking {
            withContext(Dispatchers.IO) {
             databaseRepository.getProductForParagon(paragonID)
            }
        }
//        viewModelScope.launch(Dispatchers.IO) {
//            val products = databaseRepository.getProductForParagon(paragonID)
//            withContext(Dispatchers.Main) { onResult(products) }
//        }

    }

    fun getProductForFaktura(fakturaId: Int): List<ProduktFaktura> {
        return runBlocking {
            withContext(Dispatchers.IO) {
                databaseRepository.getProductForFaktura(fakturaId)
            }
        }
    }

    fun fetchFilteredParagony(
        startDate: Date?,
        endDate: Date?,
        minPrice: Double?,
        maxPrice: Double?,
    ): List<Paragon> {
//        viewModelScope.launch(Dispatchers.IO) {
//            val result = databaseRepository.fetchFilteredParagony(startDate, endDate, minPrice, maxPrice)
//            withContext(Dispatchers.Main) { onResult(result) }
//        }
        return runBlocking {
            withContext(Dispatchers.IO) {
                Log.i("Dolan", "Fetching Filtered Paragony")
                databaseRepository.fetchFilteredParagony(startDate, endDate, minPrice, maxPrice)//, currentFilter)
            }
        }
    }

    fun fetchFilteredFaktury(
        startDate: Date?,
        endDate: Date?,
        minPrice: Double?,
        maxPrice: Double?,
        filterDate: String,
        filterPrice: String,
    ): List<Faktura> {
//        viewModelScope.launch(Dispatchers.IO) {
//            val result = databaseRepository.fetchFilteredFaktury(startDate, endDate, minPrice, maxPrice, filterDate, filterPrice)
//            withContext(Dispatchers.Main) { onResult(result) }
//        }
        return runBlocking {
            withContext(Dispatchers.IO) {
                Log.i("Dolan", "Fetching Filtered Paragony")
                databaseRepository.fetchFilteredFaktury(startDate, endDate, minPrice, maxPrice, filterDate, filterPrice)//, currentFilter)
            }
        }
    }
}