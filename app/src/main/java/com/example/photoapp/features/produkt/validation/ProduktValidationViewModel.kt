package com.example.photoapp.features.produkt.validation

import androidx.lifecycle.ViewModel
import com.example.photoapp.features.faktura.data.faktura.FakturaRepository
import com.example.photoapp.features.faktura.validation.ValidationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ProduktValidationViewModel @Inject constructor(
    private val repository: FakturaRepository
) : ViewModel() {

    private val _validationResult = MutableStateFlow(ValidationResult(true))
    val validationResult: StateFlow<ValidationResult> = _validationResult

    fun validate(
        productName: String,
        productPrice: String,
        callback: (Boolean) -> Unit
    ) {
        val errors = mutableMapOf<String, String>()

        if (productName.isBlank()) {
            errors["PRODUCT_NAME"] = "Nazwa produktu nie może być pusta"
        }

        if (productPrice.isBlank()) {
            errors["PRODUCT_PRICE"] = "CENA produktu nie może być pusta"
        }



        val result = ValidationResult(isValid = errors.isEmpty(), fieldErrors = errors)
        _validationResult.value = result
        callback(result.isValid)
    }
}