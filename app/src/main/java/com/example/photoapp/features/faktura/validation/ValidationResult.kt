package com.example.photoapp.features.faktura.validation

data class ValidationResult(
    val isValid: Boolean,
    val fieldErrors: Map<String, String> = emptyMap()
)

