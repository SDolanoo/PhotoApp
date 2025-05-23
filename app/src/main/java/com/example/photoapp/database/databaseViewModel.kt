package com.example.photoapp.database

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoapp.database.data.DatabaseRepository
import com.example.photoapp.database.data.Faktura
import com.example.photoapp.database.data.Paragon
import com.example.photoapp.database.data.ProduktFaktura
import com.example.photoapp.database.data.ProduktParagon
import com.example.photoapp.database.data.ProduktRaportFiskalny
import com.example.photoapp.database.data.RaportFiskalny
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
    val allLiveRaportFiskalny: LiveData<List<RaportFiskalny>> = databaseRepository.allLiveRaportFiskalny
//    val allLiveProduktRaportFiskalny: LiveData<List<ProduktRaportFiskalny>> = databaseRepository.allLiveProduktRaportFiskalny


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

    // [START] PARAGON

    fun addRecipe(jsonString: String) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.addRecipe(jsonString)
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

    // [END] PARAGON



    // [START] FAKTURA

    fun addFaktura(jsonString: String) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.addFaktura(jsonString)
        }
    }

    fun getProductForFaktura(fakturaId: Int): List<ProduktFaktura> {
        return runBlocking {
            withContext(Dispatchers.IO) {
                databaseRepository.getProductForFaktura(fakturaId)
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

    // [END] FAKTURA



    // [START] RAPORT FISKALNY

    fun getAllRaportFiskalny(): List<RaportFiskalny> {
        return runBlocking {
            withContext(Dispatchers.IO) {
                databaseRepository.getAllRaportFiskalny()
            }
        }
    }

    fun addRaportFiskalny(jsonString: String): Long {
//        viewModelScope.launch(Dispatchers.IO) {
//            databaseRepository.addRaportFiskalny(jsonString)
//        }
        return runBlocking {
            withContext(Dispatchers.IO) {
                databaseRepository.addRaportFiskalny(jsonString)
            }
        }
    }

    fun getRaportByID(id: Int): RaportFiskalny {
        return runBlocking {
            withContext(Dispatchers.IO) {
                databaseRepository.getRaportFiskalnyByID(id)
            }
        }
    }

    var currentRaportFiskalny: RaportFiskalny? = null
        private set

    fun setCurrentRaportFiskalny(raport: RaportFiskalny, callback: () -> Unit) {
        currentRaportFiskalny = raport
        callback()
    }

    fun addProduktyRaportFiskalny(jsonString: String, raport: RaportFiskalny) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.addProduktyRaportFiskalny(jsonString, raport)
        }
    }

    fun insertRaportFiskalny(raportFiskalny: RaportFiskalny) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.insertRaportFiskalny(raportFiskalny)
        }
    }

    fun insertProduktRaportFiskalny(produktRaportFiskalny: ProduktRaportFiskalny) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.insertProduktRaportFiskalny(produktRaportFiskalny)
        }
    }

    fun getProductForRaportFiskalny(raportFiskalnyId: Int): List<ProduktRaportFiskalny> {
        return runBlocking {
            withContext(Dispatchers.IO) {
                databaseRepository.getProductForRaportFiskalny(raportFiskalnyId)
            }
        }
    }

    fun deleteProduktRaportFiskalny(produktRaportFiskalny: ProduktRaportFiskalny) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.deleteProduktRaportFiskalny(produktRaportFiskalny)
        }
    }

    fun updateRaportFiskalny(raportFiskalny: RaportFiskalny) {
        databaseRepository.updateRaportFiskalny(raportFiskalny)
    }

    fun updateProduktRaportFiskalny(produktRaportFiskalny: ProduktRaportFiskalny) {
        databaseRepository.updateProduktRaportFiskalny(produktRaportFiskalny)
    }

    // [END] REPORT
}