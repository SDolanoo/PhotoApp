package com.example.photoapp.AI

import android.graphics.Bitmap
import android.provider.Settings.Global.getString
import android.util.Log
import com.example.photoapp.R
import com.google.ai.client.generativeai.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun chatWithGeminiForParagon(geminiKey: String, bitmap: Bitmap?, callback: (String) -> Unit) {
    if (bitmap != null) {
        CoroutineScope(Dispatchers.IO).launch {
            val model = GenerativeModel(
                "gemini-1.5-flash",
                // Retrieve API key as an environmental variable defined in a Build Configuration
                // see https://github.com/google/secrets-gradle-plugin for further instructions
                geminiKey,
                generationConfig = generationConfig {
                    temperature = 1f
                    topK = 40
                    topP = 0.95f
                    maxOutputTokens = 8192
                    responseMimeType = "application/json"
                },
                systemInstruction = content { text("przeczytaj zdjęcie paragonu i uzyskaj z niego nastepujące informacje. Całość napisz w podanym formacie json zmieniając tylko value, key muszą zostać nie zmienione. Napisz tylko json w podanym niżej formacie. Jeśli jest informacja w nawiasie, zastosuj się do tej informacji, nie wstawiając jej w odpowiedź. Jeśli w jakiejkolwiek nazwie jest 'backslash' nie pisz go. Jeśli nie udało się znaleźć informacji, napisz 'null'. Wszystkie dane, zawsze muszą mieć formę string. Dane MUSZĄ mieć następujące nazwy:{ \"dataZakupu\": \"dataZakupu - format \"yyyy-MM-dd\"\",  \"nazwaSklepu\": \"nazwaSklepu\", \"kwotaCalkowita\": \"kwotaCalkowita\",  \"produkty\": [    {     \"nazwaProduktu\": \"nazwaProduktu\",    \"cenaSuma\": \"cenaSuma\",      \"ilosc\":  \"ilosc\"(jeśli ilość nie jest integerem napisz tylko float np. 0.55)    }  ]}") },
            )

            val response = model.generateContent(
                content {
                    image(bitmap)
                    text(" ")
                }
            )
//            val text = response.text
            val result = response.text ?: ""
            Log.i("Dolan", "${result}")
            callback(result)
//            if (text != null) {
//                result = text
//                Log.i("Dolan", "${text.javaClass.kotlin.qualifiedName}")
//
//            }


    // Note that sendMessage() is a suspend function and should be called from
    // a coroutine scope or another suspend function
//            Log.i("Dolan", "${text}")
        }
    } else { callback("")}
}

fun chatWithGeminiForFaktura(geminiKey: String, bitmap: Bitmap?, callback: (String) -> Unit) {
    if (bitmap != null) {
        CoroutineScope(Dispatchers.IO).launch {
            val model = GenerativeModel(
                "gemini-1.5-flash",
                // Retrieve API key as an environmental variable defined in a Build Configuration
                // see https://github.com/google/secrets-gradle-plugin for further instructions
                geminiKey,
                generationConfig = generationConfig {
                    temperature = 1f
                    topK = 40
                    topP = 0.95f
                    maxOutputTokens = 8192
                    responseMimeType = "application/json"
                },
                systemInstruction = content { text("przeczytaj zdjęcie faktury i uzyskaj z niego nastepujące informacje. Całość napisz w podanym formacie json zmieniając tylko value, key muszą zostać nie zmienione. Napisz tylko json w podanym niżej formacie. Jeśli jest informacja w nawiasie, zastosuj się do tej informacji, nie wstawiając jej w odpowiedź. Jeśli nie udało się znaleźć informacji, napisz 'null'. Dane MUSZĄ mieć następujące nazwy:{ \"odbiorca\": { \"nazwa\":\"nazwa\", \"nip\":\"nip\", \"adres\":\"adres\"},  \"sprzedawca\":{ \"nazwa\":\"nazwa\", \"nip\":\"nip\", \"adres\":\"adres\"},\"numerFaktury\": \"numerFaktury\",  \"nrRachunkuBankowego\": \"nrRachunkuBankowego\", \"dataWystawienia\": \"dataWystawienia\",  \"dataSprzedazy\": \"dataSprzedazy\",  \"razemNetto\": \"razemNetto\",  \"razemStawka\": \"razemStawka\",  \"razemPodatek\": \"razemPodatek\",  \"razemBrutto\": \"razemBrutto\", \"waluta\": \"waluta\",  \"formaPlatnosci\": \"formaPlatnosci\",   \"produkty\": [    {     \"nazwaProduktu\": \"nazwaProduktu\",    \"jednostkaMiary\": \"jednostkaMiary\" (zobacz czy nie istnieje skrót 'j. m.' zapisz wartość jako value jednostkiMiary. key jednostkaMiary bez zmian),      \"ilosc\":  \"ilosc\"(jeśli ilość nie jest integerem napisz tylko float np. 0.55, zawsze w String),  \"wartoscNetto\": \"wartoscNetto\",  \"stawkaVat\": \"stawkaVat\",  \"podatekVat\": \"podatekVat\",  \"brutto\": \"brutto\" }  ]}") },
            )

            val response = model.generateContent(
                content {
                    image(bitmap)
                    text(" ")
                }
            )
//            val text = response.text
            val result = response.text ?: ""
            Log.i("Dolan", "${result}")
            callback(result)
//            if (text != null) {
//                result = text
//                Log.i("Dolan", "${text.javaClass.kotlin.qualifiedName}")
//
//            }


            // Note that sendMessage() is a suspend function and should be called from
            // a coroutine scope or another suspend function
//            Log.i("Dolan", "${text}")
        }
    } else { callback("")}
}
