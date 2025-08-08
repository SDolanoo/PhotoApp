package com.example.photoapp.core.AI

import android.graphics.Bitmap
import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun chatWithGemini(
    geminiKey: String,
    bitmap: Bitmap?,
    documentType: DocumentType,
    callback: (Int, List<String>) -> Unit // 1 is good, 2 is bad idk how to handle this for now
) {
    if (bitmap != null) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Definicja promptów dla 3 AI
                val prompts = listOf(
                    InvoicePrompts.extractInvoicePrompt,  // Dane ogólne
                    InvoicePrompts.extractSellerBuyerPrompt, // Sprzedawca / Nabywca
                    InvoicePrompts.extractProductsPrompt // Produkty
                )

                // Model konfig
                fun createModel(prompt: String) = GenerativeModel(
                    "gemini-2.5-flash-lite",
                    geminiKey,
                    generationConfig = generationConfig {
                        temperature = 0.0f
                        topK = 1
                        topP = 1.0f
                        maxOutputTokens = 8192
                        responseMimeType = "application/json"
                    },
                    systemInstruction = content { text(prompt) }
                )

                // Odpalamy 3 zapytania równolegle
                val deferredResponses = prompts.map { prompt ->
                    async {
                        val model = createModel(prompt)
                        val response = model.generateContent(
                            content {
                                image(bitmap)
                                text(" ")
                            }
                        )
                        response.text ?: ""
                    }
                }

                // Czekamy na wszystkie odpowiedzi
                val results = deferredResponses.awaitAll()

                withContext(Dispatchers.Main) {
                    callback(1, results) // Zwracamy listę 3 stringów
                }

            } catch (e: Exception) {
                Log.e("GeminiAPI", "Błąd: $e")
                withContext(Dispatchers.Main) {
                    callback(2, listOf("Error: $e"))
                }
            }
        }
    } else {
        callback(2, listOf("Error: brak zdjęcia w google_ai.kt"))
    }
}

enum class DocumentType {
    FAKTURA
}

