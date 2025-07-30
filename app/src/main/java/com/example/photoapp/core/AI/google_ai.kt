package com.example.photoapp.core.AI

import android.graphics.Bitmap
import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun chatWithGemini(
    geminiKey: String,
    bitmap: Bitmap?,
    documentType: DocumentType,
    callback: (Int, String) -> Unit // 1 is good, 2 is bad idk how to handle this for now
) {
    if (bitmap != null) {
        CoroutineScope(Dispatchers.IO).launch {
            val systemInstructionText = when (documentType) {
                DocumentType.FAKTURA -> buildString {
                    append(InvoicePrompts.extractInvoicePrompt)
                }

            }

            val model = GenerativeModel(
                "gemini-2.5-flash-lite",
                geminiKey,
                generationConfig = generationConfig {
                    temperature = 1f
                    topK = 40
                    topP = 0.95f
                    maxOutputTokens = 8192
                    responseMimeType = "application/json"
                },
                systemInstruction = content { text(systemInstructionText) },
            )
            try {
                val response = model.generateContent(
                    content {
                        image(bitmap)
                        text(" ")
                    }
                )
                val result = response.text ?: ""
                Log.i("Dolan", result)
                withContext(Dispatchers.Main) {
                    callback(1, result)
                }
            } catch (e: Exception) {
                Log.e("GeminiAPI", "Service Unavailable (503): $e")
                callback(2, "Error: Server is temporarily unavailable. Please try again later.")
            } catch (e: Exception) {
                Log.e("GeminiAPI", "Request failed: ${e.localizedMessage}")
            }
        }
    } else {
        callback(2, "")
    }
}

enum class DocumentType {
    FAKTURA
}