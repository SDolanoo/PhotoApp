package com.example.photoapp.features.faktura.presentation.details

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoapp.core.utils.convertDoubleToString
import com.example.photoapp.features.faktura.data.faktura.Faktura
import com.example.photoapp.features.faktura.data.faktura.FakturaRepository
import com.example.photoapp.features.odbiorca.data.Odbiorca
import com.example.photoapp.features.odbiorca.data.OdbiorcaRepository
import com.example.photoapp.features.produkt.data.Produkt
import com.example.photoapp.features.produkt.data.ProduktFaktura
import com.example.photoapp.features.sprzedawca.data.Sprzedawca
import com.example.photoapp.features.sprzedawca.data.SprzedawcaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FakturaDetailsViewModel @Inject constructor(
    private val repository: FakturaRepository,
    private val sprzedawcaRepository: SprzedawcaRepository,
    private val odbiorcaRepository: OdbiorcaRepository
) : ViewModel() {

    // ---- FAKTURA ----
    private val _actualFaktura = MutableStateFlow<Faktura>(Faktura.default())
    val actualFaktura: StateFlow<Faktura> = _actualFaktura.asStateFlow()

    private val _editedFaktura = MutableStateFlow<Faktura>(Faktura.default())
    val editedFaktura: StateFlow<Faktura> = _editedFaktura.asStateFlow()

    // ---- PRODUKTY ----
    private val _actualProdukty = MutableStateFlow<List<ProduktFakturaZProduktem>>(emptyList())
    val actualProdukty: StateFlow<List<ProduktFakturaZProduktem>> = _actualProdukty.asStateFlow()

    private val _editedProdukty = MutableStateFlow<List<ProduktFakturaZProduktem>>(emptyList())
    val editedProdukty: StateFlow<List<ProduktFakturaZProduktem>> = _editedProdukty.asStateFlow()


    // ---- SPRZEDAWCA ----
    private val _actualSprzedawca = MutableStateFlow<Sprzedawca>(Sprzedawca.empty())
    val actualSprzedawca: StateFlow<Sprzedawca> = _actualSprzedawca.asStateFlow()

    private val _editedSprzedawca = MutableStateFlow<Sprzedawca>(Sprzedawca.empty())
    val editedSprzedawca: StateFlow<Sprzedawca> = _editedSprzedawca.asStateFlow()

    // ---- ODBIORCA ----
    private val _actualOdbiorca = MutableStateFlow<Odbiorca>(Odbiorca.empty())
    val actualOdbiorca: StateFlow<Odbiorca> = _actualOdbiorca.asStateFlow()

    private val _editedOdbiorca = MutableStateFlow<Odbiorca>(Odbiorca.empty())
    val editedOdbiorca: StateFlow<Odbiorca> = _editedOdbiorca.asStateFlow()


    fun loadProducts(faktura: Faktura) {
        viewModelScope.launch(Dispatchers.IO) {
            val produktyFaktury = repository.getProduktyFakturaForFaktura(faktura)
            val sprzedawca = sprzedawcaRepository.getById(faktura.sprzedawcaId)
            val odbiorca = odbiorcaRepository.getById(faktura.odbiorcaId)
            // Ustawienie actual i edited jednocześnie
            _actualFaktura.value = faktura
            _editedFaktura.value = faktura

            val produktyZProduktami = produktyFaktury.mapNotNull { produktFaktura ->
                val produkt = repository.getProduktForProduktFaktura(produktFaktura)
                produkt.let {
                    ProduktFakturaZProduktem(
                        produktFaktura = produktFaktura,
                        produkt = it
                    )
                }
            }

            _actualProdukty.value = produktyZProduktami
            _editedProdukty.value = produktyZProduktami

            _actualSprzedawca.value = sprzedawca!!
            _editedSprzedawca.value = sprzedawca
            Log.i("Dolan", sprzedawca.toString())

            _actualOdbiorca.value = odbiorca!!
            _editedOdbiorca.value = odbiorca
            Log.i("Dolan", odbiorca.toString())
        }
    }

    fun getFakturaByID(id: String, callback: (Faktura) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val faktura = repository.getFakturaByID(id)
            callback(faktura!!)
        }
    }

    fun editingSuccess() {
        viewModelScope.launch(Dispatchers.IO) {
            updateAllEditedToDB {
                loadProducts(it)
            }
        }
    }

    fun editingFailed() {
        viewModelScope.launch(Dispatchers.IO) {
            loadProducts(_actualFaktura.value!!)
        }
    }

    fun updateEditedFakturaTemp(faktura: Faktura, callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _editedFaktura.value = faktura
            callback()
        }
    }

    fun updateEditedProductTemp(index: Int, produkt: ProduktFakturaZProduktem, callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _editedProdukty.value = _editedProdukty.value.toMutableList().also {
                it[index] = produkt
            }
            callback()
        }
    }

    fun deleteEditedProduct(produkt: ProduktFakturaZProduktem, callback: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            _editedProdukty.update { currentList ->
                currentList.filterNot { it == produkt }
            }

            callback()
        }
    }

    fun updateAllEditedToDB(callback: (Faktura) -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val faktura = editedFaktura.value
                val produktyEdited = editedProdukty.value
                val sprzedawca = editedSprzedawca.value
                val odbiorca = editedOdbiorca.value

                val nowaListaId = mutableListOf<String>()


                var razemNetto = 0.0
                var razemBrutto = 0.0
                // 🧠 OBSŁUGA KAŻDEGO PRODUKTU INDYWIDUALNIE
                produktyEdited.forEachIndexed { index, produkt ->
                    val isNowyProdukt = produkt.produktFaktura.id.length <= 4

                    val produktId = if (isNowyProdukt) {
                        repository.insertProduktFaktura(produkt.produktFaktura.copy(id = "")) // Zwraca nowe ID
                    } else {
                        repository.updateProduktFaktura(produkt.produktFaktura)
                        produkt.produktFaktura.id
                    }

                    nowaListaId.add(produktId)

                    // Jeżeli był nowy, nadpisujemy id w pamięci
                    val produktFakturaZProduktem = produkt.copy(
                        produktFaktura = produkt.produktFaktura.copy(id = produktId)
                    )
                    razemNetto += produkt.produktFaktura.wartoscNetto.replace(",", ".").toDoubleOrNull() ?: 0.0
                    razemBrutto += produkt.produktFaktura.wartoscBrutto.replace(",", ".").toDoubleOrNull() ?: 0.0
                    updateEditedProductTemp(index, produktFakturaZProduktem, callback = {})
                }

                // 🔥 DETEKCJA USUNIĘTYCH PRODUKTÓW
                val aktualnePozycjeZBazy = repository.getProduktyFakturaForFaktura(faktura)
                val aktualneIdsZBazy = aktualnePozycjeZBazy.map { it.id }.toSet() // Set<String>
                val aktualneIdsPoEdycji = nowaListaId.toSet()


                val usunieteIds = aktualneIdsZBazy - aktualneIdsPoEdycji

                usunieteIds.forEach { id ->
                    aktualnePozycjeZBazy.find { it.id == id }?.let { produktDoUsuniecia ->
                        repository.deleteProduktFakturaFromFaktura(produktDoUsuniecia)
                    }
                }

                // 🔁 Aktualizacja sprzedawcy i odbiorcy
                val sprzedawcaId = sprzedawcaRepository.upsertSprzedawcaSmart(sprzedawca)
                val odbiorcaId = odbiorcaRepository.upsertOdbiorcaSmart(odbiorca)

                val updatedFaktura = faktura.copy(
                    sprzedawcaId = sprzedawcaId,
                    odbiorcaId = odbiorcaId,
                    razemNetto = convertDoubleToString(razemNetto),
                    razemVAT = convertDoubleToString(razemBrutto-razemNetto),
                    razemBrutto = convertDoubleToString(razemBrutto),
                    doZaplaty = convertDoubleToString(razemBrutto))
                repository.updateFaktura(updatedFaktura)
                callback(updatedFaktura)

            } catch (e: Exception) {
                Log.e("Dolan", "Błąd podczas zapisu do DB: ${e}")
            }
        }
    }

    private var nextTempProductId = -1L

    fun addOneProductToEdited(callback: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            val newProduct = ProduktFakturaZProduktem(produktFaktura = ProduktFaktura.default().copy(id = (nextTempProductId--).toString()), produkt = Produkt.default())


            _editedProdukty.update { currentList ->
                val index = currentList.indexOfFirst { it.produkt.nazwaProduktu == newProduct.produkt.nazwaProduktu }

                if (index >= 0) {
                    val existing = currentList[index]
                    val newIlosc = (existing.produktFaktura.ilosc.toIntOrNull() ?: 1) + 1

                    currentList.toMutableList().apply {
                        set(index, existing.copy(produktFaktura = existing.produktFaktura.copy(ilosc = newIlosc.toString())))
                    }
                } else {
                    currentList + newProduct
                }
            }

            callback()
        }
    }

    fun setFaktura(faktura: Faktura) {
        _actualFaktura.value = faktura
    }

    // ---- SPRZEDAWCA ----
    fun editEditedSprzedawca(sprzedawca: Sprzedawca) {
        viewModelScope.launch(Dispatchers.IO) {
            _editedSprzedawca.value = sprzedawca
        }
    }

    fun replaceEditedSprzedawca(newSprzedawca: Sprzedawca, callback: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            _editedSprzedawca.value = newSprzedawca
            _editedFaktura.value = _editedFaktura.value.copy(sprzedawcaId = newSprzedawca.id)
            callback()
        }
    }

    // ---- ODBIORCA ----
    fun editEditedOdbiorca(odbiorca: Odbiorca) {
        viewModelScope.launch(Dispatchers.IO) {
            _editedOdbiorca.value = odbiorca
        }
    }

    fun replaceEditedOdbiorca(newOdbiorca: Odbiorca, callback: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            _editedOdbiorca.value = newOdbiorca
            _editedFaktura.value = _editedFaktura.value.copy(odbiorcaId = newOdbiorca.id)
            callback()
        }
    }

    fun getListOfSprzedacwa(): List<Sprzedawca> {
        return runBlocking {
            withContext(Dispatchers.IO) {
                sprzedawcaRepository.getAll()
                    .sortedBy { it.id }                       // Najpierw sortujemy po ID
                    .distinctBy { it.nazwa.trim().lowercase() } // Unikalne po nazwie (ignorując wielkość liter i spacje)
            }
        }
    }

    fun getListOfOdbiorca(): List<Odbiorca> {
        return runBlocking {
            withContext(Dispatchers.IO) {
                odbiorcaRepository.getAllOdbiorcy()
                    .sortedBy { it.id }
                    .distinctBy { it.nazwa.trim().lowercase() }
            }
        }
    }

    fun getListOfProdukty(): List<Produkt> {
        return runBlocking {
            withContext(Dispatchers.IO) {
                repository.getAllProdukty()
                    .sortedBy { it.id }
                    .distinctBy { it.nazwaProduktu.trim().lowercase() }
            }
        }
    }

    fun replaceEditedProdukt(index: Int, product: Produkt,  callback: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            _editedProdukty.update { currentList ->
                currentList.toMutableList().apply {
                    this[index] = ProduktFakturaZProduktem(
                        produktFaktura = ProduktFaktura.default().copy(id = (nextTempProductId--).toString(),
                            produktId = product.id), produkt = product
                    )
                }
            }
            callback()
        }
    }

}

data class ProduktFakturaZProduktem(
    val produktFaktura: ProduktFaktura,
    val produkt: Produkt
)
