package com.example.photoapp.utils

import android.util.Log
import kotlinx.serialization.json.*
import java.util.Calendar
import java.util.Date

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