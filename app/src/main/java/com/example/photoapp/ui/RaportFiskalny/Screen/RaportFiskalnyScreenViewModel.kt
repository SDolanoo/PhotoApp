package com.example.photoapp.ui.RaportFiskalny.Screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.photoapp.database.DatabaseViewModel
import com.example.photoapp.database.data.DatabaseRepository
import com.example.photoapp.database.data.RaportFiskalny
import com.example.photoapp.utils.normalizedDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class RaportFiskalnyScreenViewModel @Inject constructor(
    private val repository: DatabaseRepository
) : ViewModel() {

    val allRaportFiskalny: LiveData<List<RaportFiskalny>> = repository.allLiveRaportFiskalny

    fun getGroupedRaportFiskalnyList(raportFiskalnyList: List<RaportFiskalny>): Map<Date?, List<RaportFiskalny>> {
        return raportFiskalnyList
            .sortedByDescending { it.dataDodania }
            .groupBy { raport -> raport.dataDodania?.normalizedDate() }
    }
}
