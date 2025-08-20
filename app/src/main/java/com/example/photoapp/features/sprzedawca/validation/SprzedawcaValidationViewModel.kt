package com.example.photoapp.features.sprzedawca.validation

import androidx.lifecycle.ViewModel
import com.example.photoapp.features.faktura.validation.ValidationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SprzedawcaValidationViewModel @Inject constructor() : ViewModel() {

    private val _validationResult = MutableStateFlow(ValidationResult(true))
    val validationResult: StateFlow<ValidationResult> = _validationResult

    fun validate(
        sellerName: String,
        callback: (Boolean) -> Unit
    ) {
        val errors = mutableMapOf<String, String>()

        if (sellerName.isBlank()) {
            errors["SELLER_NAME"] = "Nazwa sprzedawcy nie może być pusta"
        }

        val result = ValidationResult(isValid = errors.isEmpty(), fieldErrors = errors)
        _validationResult.value = result
        callback(result.isValid)
    }
}