package com.example.photoapp.features.faktura.validation

import androidx.lifecycle.ViewModel
import com.example.photoapp.features.faktura.presentation.details.ProduktFakturaZProduktem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import kotlin.collections.forEachIndexed

@HiltViewModel
class ValidationViewModel @Inject constructor() : ViewModel() {

    private val _validationResult = MutableStateFlow(ValidationResult(true))
    val validationResult: StateFlow<ValidationResult> = _validationResult

    fun validate(
        sellerName: String,
        buyerName: String,
        products: List<ProduktFakturaZProduktem>,
        callback: (Boolean) -> Unit
    ) {
        val errors = mutableMapOf<String, String>()

        if (sellerName.isBlank()) {
            errors["SELLER_NAME"] = "Nazwa sprzedawcy nie może być pusta"
        }

        if (buyerName.isBlank()) {
            errors["BUYER_NAME"] = "Nazwa odbiorcy nie może być pusta"
        }

        products.forEachIndexed { index, product ->
            if (product.produkt.nazwaProduktu.isBlank()) {
                errors["PRODUCT_NAME_$index"] = "Produkt ${index + 1}: Nazwa nie może być pusta"
            }
            if (product.produktFaktura.ilosc.isBlank()) {
                errors["PRODUCT_QUANTITY_$index"] = "Produkt ${index + 1}: Ilość nie może być pusta"
            }
            if (product.produktFaktura.wartoscBrutto.isBlank()) {
                errors["PRODUCT_BRUTTO_$index"] = "Produkt ${index + 1}: Wartość brutto nie może być pusta"
            }
        }

        val result = ValidationResult(isValid = errors.isEmpty(), fieldErrors = errors)
        _validationResult.value = result
        callback(result.isValid)
    }

    fun clearValidation() {
        _validationResult.value = ValidationResult(true)
    }
}


object ValidationKeys {
    const val SELLER_NAME = "SELLER_NAME"
    const val BUYER_NAME = "BUYER_NAME"
    fun productName(index: Int) = "PRODUCT_NAME_$index"
    fun productQuantity(index: Int) = "PRODUCT_QUANTITY_$index"
    fun productBrutto(index: Int) = "PRODUCT_BRUTTO_$index"
}
