package com.example.photoapp.core.AI

import android.graphics.Bitmap
import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun chatWithGemini(
    geminiKey: String,
    bitmap: Bitmap?,
    documentType: DocumentType,
    callback: (Int, String) -> Unit // 1 is good, 2 is bad idk how to handle this for now
) {
    if (bitmap != null) {
        CoroutineScope(Dispatchers.IO).launch {
            val systemInstructionText = when (documentType) {
                DocumentType.PARAGON -> "przeczytaj zdjęcie paragonu i uzyskaj z niego nastepujące informacje. Całość napisz w podanym formacie json zmieniając tylko value, key muszą zostać nie zmienione. Napisz tylko json w podanym niżej formacie. Jeśli jest informacja w nawiasie, zastosuj się do tej informacji, nie wstawiając jej w odpowiedź. Jeśli w jakiejkolwiek nazwie jest 'backslash' nie pisz go. Jeśli nie udało się znaleźć informacji, napisz 'null'. Wszystkie dane, zawsze muszą mieć formę string. Dane MUSZĄ mieć następujące nazwy:{ \"dataZakupu\": \"dataZakupu - format \"yyyy-MM-dd\"\",  \"nazwaSklepu\": \"nazwaSklepu\", \"kwotaCalkowita\": \"kwotaCalkowita\",  \"produkty\": [    {     \"nazwaProduktu\": \"nazwaProduktu\",    \"cenaSuma\": \"cenaSuma\",      \"ilosc\":  \"ilosc\"(jeśli ilość nie jest integerem napisz tylko float np. 0.55)    }  ]}"

                DocumentType.FAKTURA -> "przeczytaj zdjęcie faktury i uzyskaj z niego nastepujące informacje. Całość napisz w podanym formacie json zmieniając tylko value, key muszą zostać nie zmienione. Napisz tylko json w podanym niżej formacie. Jeśli jest informacja w nawiasie, zastosuj się do tej informacji, nie wstawiając jej w odpowiedź. Jeśli nie udało się znaleźć informacji, napisz 'null'. Dane MUSZĄ mieć następujące nazwy:{ \"odbiorca\": { \"nazwa\":\"nazwa\", \"nip\":\"nip\", \"adres\":\"adres\"},  \"sprzedawca\":{ \"nazwa\":\"nazwa\", \"nip\":\"nip\", \"adres\":\"adres\"},\"numerFaktury\": \"numerFaktury\",  \"nrRachunkuBankowego\": \"nrRachunkuBankowego\", \"dataWystawienia\": \"dataWystawienia\",  \"dataSprzedazy\": \"dataSprzedazy\",  \"razemNetto\": \"razemNetto\",  \"razemStawka\": \"razemStawka\",  \"razemPodatek\": \"razemPodatek\",  \"razemBrutto\": \"razemBrutto\", \"waluta\": \"waluta\",  \"formaPlatnosci\": \"formaPlatnosci\",   \"produkty\": [    {     \"nazwaProduktu\": \"nazwaProduktu\",    \"jednostkaMiary\": \"jednostkaMiary\" (zobacz czy nie istnieje skrót 'j. m.' zapisz wartość jako value jednostkiMiary. key jednostkaMiary bez zmian),      \"ilosc\":  \"ilosc\"(jeśli ilość nie jest integerem napisz tylko float np. 0.55, zawsze w String),  \"wartoscNetto\": \"wartoscNetto\",  \"stawkaVat\": \"stawkaVat\",  \"podatekVat\": \"podatekVat\",  \"brutto\": \"brutto\" }  ]}"

                DocumentType.RAPORT_FISKALNY -> "przeczytaj zdjęcie raportu fiskalnego i uzyskaj z niego następujące informacje. Całość napisz w podanym formacie JSON zmieniając tylko value, key muszą zostać niezmienione. Jeśli jest informacja w nawiasie, zastosuj się do tej informacji, nie wstawiając jej w odpowiedź. Jeśli nie udało się znaleźć informacji, napisz 'null'. Wszystkie dane, zawsze muszą mieć formę string. Dane MUSZĄ mieć następujące nazwy:{ \\\"dataDodania\\\": \\\"dataDodania - format \\\"yyyy-MM-dd\\\"\\\", \\\"produkty\\\": [ { \\\"nrPLU\\\": \\\"nrPLU\\\"(nrPLU to numer produktu w kwadratowym nawiasie, numer musi być bez nawiasów, sam numer), \\\"ilosc\\\": \\\"ilosc\\\"(jeśli ilość nie jest integerem napisz tylko float np. 0.55) } ]}"

                DocumentType.PRODUCTS_RAPORT_FISKALNY -> "przeczytaj zdjęcie raportu fiskalnego i uzyskaj z niego tylko listę produktów. Całość napisz w podanym formacie JSON zmieniając tylko value, key muszą zostać niezmienione. Napisz tylko JSON w podanym niżej formacie. Jeśli nie udało się znaleźć informacji, napisz 'null'. Wszystkie dane, zawsze muszą mieć formę string. Dane MUSZĄ mieć następujące nazwy: { \\\"produkty\\\": [ { \\\"nrPLU\\\": \\\"nrPLU\\\"(nrPLU to numer produktu w kwadratowym nawiasie, numer musi być bez nawiasów, sam numer), \\\"ilosc\\\": \\\"ilosc\\\"(jeśli ilość nie jest integerem napisz tylko float np. 0.55) } ]}\""
            }

            val model = GenerativeModel(
                "gemini-1.5-flash",
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
                callback(1, result)
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
    PARAGON, FAKTURA, RAPORT_FISKALNY, PRODUCTS_RAPORT_FISKALNY
}