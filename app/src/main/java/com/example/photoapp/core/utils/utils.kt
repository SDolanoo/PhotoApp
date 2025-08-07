package com.example.photoapp.core.utils

import android.annotation.SuppressLint
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.serialization.json.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun jsonTransformer(jsonInput: String, defaultValue: String? = null): String {
    Log.i("Dolan", "Json before transformation: $jsonInput")
    val jsonElement = Json.parseToJsonElement(jsonInput)

    fun transformElement(element: JsonElement): JsonElement {
        return when (element) {
            is JsonObject -> JsonObject(element.mapValues { (_, value) -> transformElement(value) })
            is JsonArray -> JsonArray(element.map { transformElement(it) })
            is JsonPrimitive -> {
                if (element.isString && (element.content == "null" || element.content.isBlank())) {
                    JsonPrimitive(defaultValue)
                } else {
                    element
                }
            }
            else -> element // Handle other cases (if any)
        }
    }

    val transformedJson = transformElement(jsonElement)
    val resultJson = Json.encodeToString(transformedJson)
    Log.i("Dolan", "Json after transformation: $resultJson")
    return resultJson
}

fun Date.normalizedDate(): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.time
}

fun formatDate(date: Long?): String {
    return date?.let {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it)
    } ?: "N/A"
}

fun convertMillisToString(millis: Long): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return formatter.format(Date(millis))
}

fun convertMillisToDate(millis: Long): Date {
    return Date(millis)
}

fun convertStringToDate(dateStr: String): Date? {
    return try {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateStr)
    } catch (e: ParseException) {
        Log.e("Dolan", "Failed to parse date: $dateStr", e)
        null
    }
}

fun convertDateToString(date: Date): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
}

fun currentUserId(): String {
    return FirebaseAuth.getInstance().currentUser?.uid ?: "unauthenticated"
}

@SuppressLint("DefaultLocale")
fun convertDoubleToString(double: Double): String {
    return String.format("%.2f", double).replace('.', ',')
}

@SuppressLint("DefaultLocale")
fun calculateNetPrice(netvalue: String, q: String): String {
    val quantity = q.replace(',', '.').toDoubleOrNull()
    val netValue = netvalue.replace(',', '.').toDoubleOrNull()

    if (quantity == null || netValue == null || quantity == 0.0 || netValue == 0.0) return "0"

    val netPrice = netValue / quantity
    return convertDoubleToString(netPrice)
}

@SuppressLint("DefaultLocale")
fun calculateGrossValue(netvalue: String, vat: String): String? {
    val vatRate = vat.replace(',', '.').toIntOrNull()
    val netPrice = netvalue.replace(',', '.').toDoubleOrNull()

    if (vatRate == null || vatRate == 0) return null

    if (netPrice == null || netPrice == 0.0) return "0"

    val grossPrice = netPrice * (1 + vatRate / 100.0)
    return convertDoubleToString(grossPrice)
}

@SuppressLint("DefaultLocale")
fun calculateNetValue(grprice: String, vat: String): String? {
    val vatRate = vat.replace(',', '.').toIntOrNull()
    val grossPrice = grprice.replace(',', '.').toDoubleOrNull()

    if (grossPrice == null || grossPrice == 0.0) return "0"

    if (vatRate == null || vatRate == 0) return null

    val netValue = grossPrice / (1 + vatRate / 100.0)
    return convertDoubleToString(netValue)
}

@SuppressLint("DefaultLocale")
fun calculateNetValueQuantity(q: String, price: String): String {
    val quantity = q.replace(',', '.').toDoubleOrNull()
    val netPrice = price.replace(',', '.').toDoubleOrNull()

    if (quantity == null || netPrice == null || quantity == 0.0 || netPrice == 0.0) return "0"

    val netValue = quantity * netPrice
    return convertDoubleToString(netValue)
}

@SuppressLint("DefaultLocale")
fun calculateGrossValueQuantity(q: String, price: String): String {
    val quantity = q.replace(',', '.').toDoubleOrNull()
    val netPrice = price.replace(',', '.').toDoubleOrNull()

    if (quantity == null || netPrice == null || quantity == 0.0 || netPrice == 0.0) return "0"

    val netValue = quantity * netPrice
    return convertDoubleToString(netValue)
}

fun calculateSubstraction(a: String, b: String): String {
    val aa = a.replace(',', '.').toDoubleOrNull()
    val bb = b.replace(',', '.').toDoubleOrNull()

    if (aa == null || bb == null ) return "0"

    val result = aa - bb
    return convertDoubleToString(result)
}

fun calculateSum(a: String, b: String): String {
    val aa = a.replace(',', '.').toDoubleOrNull()
    val bb = b.replace(',', '.').toDoubleOrNull()

    if (aa == null || bb == null ) return "0"

    val result = aa + bb
    return convertDoubleToString(result)
}