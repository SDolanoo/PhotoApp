package com.example.photoapp.core.AI

//import android.graphics.Bitmap
//import android.util.Log
//import com.google.ai.client.generativeai.GenerativeModel
//import com.google.ai.client.generativeai.type.content
//import com.google.ai.client.generativeai.type.generationConfig
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//
//fun chatWithGemini(
//    geminiKey: String,
//    bitmap: Bitmap?,
//    documentType: DocumentType,
//    callback: (Int, String) -> Unit // 1 is good, 2 is bad idk how to handle this for now
//) {
//    if (bitmap != null) {
//        CoroutineScope(Dispatchers.IO).launch {
//            val systemInstructionText = when (documentType) {
//                DocumentType.FAKTURA -> buildString {
//                    append(InvoicePrompts.extractInvoicePrompt)
//                }
//
//            }
//
//            val model = GenerativeModel(
//                "gemini-2.5-flash-lite",
//                geminiKey,
//                generationConfig = generationConfig {
//                    temperature = 0f
//                    topK = 1
//                    topP = 1.0f
//                    maxOutputTokens = 8192
//                    responseMimeType = "application/json"
//                },
//                systemInstruction = content { text(systemInstructionText) },
//            )
//            try {
//                val response = model.generateContent(
//                    content {
//                        image(bitmap)
//                        text(" ")
//                    }
//                )
//                val result = response.text ?: ""
//                Log.i("Dolan", result)
//                withContext(Dispatchers.Main) {
//                    callback(1, result)
//                }
//            } catch (e: Exception) {
//                Log.e("GeminiAPI", "Service Unavailable (503): $e")
//                callback(2, "Error: Server is temporarily unavailable. Please try again later.")
//            } catch (e: Exception) {
//                Log.e("GeminiAPI", "Request failed: ${e.localizedMessage}")
//            }
//        }
//    } else {
//        callback(2, "")
//    }
//}
//
//enum class DocumentType {
//    FAKTURA
//}
//
//object InvoicePrompts {
//
//    val extractInvoicePrompt = """
//        Twoim zadaniem jest wyodrębnienie danych z faktury na podstawie tekstu OCR i uzupełnienie gotowego JSON-a w podanej strukturze Kotlin DTO.
//
//        Zasady:
//
//        1. Nie zmyślaj danych – jeżeli nie ma jakiejś informacji, wstaw "null" (jako string).
//        2. W polach liczbowych również wpisz "null" jeśli nie jesteś w stanie znaleźć danej liczby.
//        3. Zachowuj dokładnie nazwy pól – jak w strukturze poniżej.
//        4. Lista produktów musi zawierać tyle obiektów, ile jest pozycji na fakturze. Jeśli nie rozpoznajesz którejś pozycji – pomiń ją.
//        5. Data musi mieć format YYYY-MM-DD lub "null" jeśli nie istnieje.
//        6. Jeżeli nie możesz określić waluty – wpisz "PLN"
//        7. Wszystkie wartości mają być typu string – również liczby (np. "123.45").
//        8. odpowiedz daj tylko w json, nic więcej
//
//        Struktura JSON do uzupełnienia:
//
//        {
//          "typFaktury": "null",
//          "numerFaktury": "null",
//          "dataWystawienia": "null",
//          "dataSprzedazy": "null",
//          "miejsceWystawienia": "null",
//          "razemNetto": "null",
//          "razemVAT": "null",
//          "razemBrutto": "null",
//          "doZaplaty": "null",
//          "waluta": "PLN",
//          "formaPlatnosci": "null",
//          "odbiorca": {
//            "nazwa": "null",
//            "nip": "null",
//            "adres": "null",
//            "kodPocztowy": "null",
//            "miejscowosc": "null",
//            "kraj": "null",
//            "opis": "null",
//            "email": "null",
//            "telefon": "null"
//          },
//          "sprzedawca": {
//            "nazwa": "null",
//            "nip": "null",
//            "adres": "null",
//            "kodPocztowy": "null",
//            "miejscowosc": "null",
//            "kraj": "null",
//            "opis": "null",
//            "email": "null",
//            "telefon": "null"
//          },
//          "produkty": [
//            {
//              "nazwaProduktu": "null",
//              "jednostkaMiary": "null",
//              "cenaNetto": "null",
//              "stawkaVat": "null",
//              "ilosc": "null",
//              "rabat": "null",
//              "wartoscNetto": "null",
//              "wartoscBrutto": "null",
//              "pkwiu": "null"
//            }
//          ]
//        }
//    """.trimIndent()
//}
