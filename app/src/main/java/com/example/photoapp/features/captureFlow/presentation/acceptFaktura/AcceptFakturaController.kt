package com.example.photoapp.features.captureFlow.presentation.acceptFaktura

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoapp.features.faktura.data.faktura.Faktura
import com.example.photoapp.features.faktura.data.faktura.FakturaRepository
import com.example.photoapp.features.faktura.data.faktura.Produkt
import com.example.photoapp.features.odbiorca.data.OdbiorcaRepository
import com.example.photoapp.features.sprzedawca.data.SprzedawcaRepository
import com.example.photoapp.features.faktura.presentation.details.ProduktFakturaZProduktem
import com.example.photoapp.features.odbiorca.data.Odbiorca
import com.example.photoapp.features.sprzedawca.data.Sprzedawca
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.collections.find

@HiltViewModel
class AcceptFakturaController @Inject constructor(
    private val repository: FakturaRepository,
    private val sprzedawcaRepository: SprzedawcaRepository,
    private val odbiorcaRepository: OdbiorcaRepository
) : ViewModel() {

    fun allProducts(): List<Produkt> {
        return runBlocking {
            withContext(Dispatchers.IO) {
                repository.getAllProdukty()
            }
        }
    }

    fun checkForExistingProducts(produkt: Produkt, callback: (Long) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val existingProducts = allProducts()

            val match = existingProducts.find {
                it.nazwaProduktu == produkt.nazwaProduktu &&
                        it.cenaNetto == produkt.cenaNetto
            }

            if (match != null) {
                // Taki produkt już istnieje – używamy jego ID
                callback(match.id)
            } else {
                // Nie ma takiego produktu – dodajemy nowy
                val newId = repository.insertProdukt(produkt)
                callback(newId)
            }
        }
    }

    fun addToDatabase(
        faktura: Faktura,
        sprzedawca: Sprzedawca,
        odbiorca: Odbiorca,
        produkty: List<ProduktFakturaZProduktem>
        ) {
        viewModelScope.launch(Dispatchers.IO) {

            val sprzedawcaId = sprzedawcaRepository.upsertSprzedawcaSmart(sprzedawca)
            val odbiorcaId = odbiorcaRepository.upsertOdbiorcaSmart(odbiorca)

            val fakturaId = repository.insertFaktura(faktura.copy(sprzedawcaId = sprzedawcaId, odbiorcaId = odbiorcaId))

            produkty.forEach { produkt ->
                checkForExistingProducts(produkt.produkt) { produktId ->
                    repository.insertProduktFaktura(produkt.produktFaktura.copy(id = 0L, produktId = produktId, fakturaId = fakturaId))
                }
            }




        }
    }
}
