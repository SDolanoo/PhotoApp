package com.example.photoapp.utils

import android.util.Log
import kotlinx.serialization.json.*

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