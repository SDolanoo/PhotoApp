package com.example.photoapp.ui.paragon.screen

import android.app.Application
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoapp.database.data.DatabaseRepository
import com.example.photoapp.database.data.Paragon
import com.example.photoapp.ui.ExcelPacker.ExportRoomViewModel
import com.example.photoapp.utils.normalizedDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ParagonScreenViewModel @Inject constructor(
    application: Application,
    private val repository: DatabaseRepository,
    private val exportRoomViewModel: ExportRoomViewModel,
) : AndroidViewModel(application) {

    private val _groupedParagons = MutableStateFlow<Map<Date?, List<Paragon>>>(emptyMap())
    val groupedParagons: StateFlow<Map<Date?, List<Paragon>>> = _groupedParagons

    val snackbarHostState = SnackbarHostState()
    val isLoading = MutableStateFlow(false)

    fun loadParagons(showFiltered: Boolean, filteredList: List<Paragon>) {
        val sourceList = if (showFiltered) filteredList else repository.allLiveParagony.value ?: emptyList()
        _groupedParagons.value = sourceList
            .sortedByDescending { it.dataZakupu }
            .groupBy { it.dataZakupu?.normalizedDate() }
    }

    fun exportToExcel(showFiltered: Boolean, filteredList: List<Paragon>, onFinish: () -> Unit) {
        val sourceList = if (showFiltered) filteredList else repository.allLiveParagony.value ?: emptyList()
        viewModelScope.launch {
            isLoading.value = true
            delay(3000) // simulate loading
            exportRoomViewModel.exportToExcel("paragon", sourceList)
            isLoading.value = false
            onFinish()
        }
    }

    fun handlePermissionDenied() {
        viewModelScope.launch {
            snackbarHostState.showSnackbar(
                message = "This feature is unavailable because it requires access to the phone's storage"
            )
        }
    }
}
