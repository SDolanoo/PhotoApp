package com.example.photoapp.core.AI

// old prompt                 DocumentType.FAKTURA -> "przeczytaj zdjęcie faktury i uzyskaj z niego nastepujące informacje. Całość napisz w podanym formacie json zmieniając tylko value, key muszą zostać nie zmienione. Napisz tylko json w podanym niżej formacie. Jeśli jest informacja w nawiasie, zastosuj się do tej informacji, nie wstawiając jej w odpowiedź. Jeśli nie udało się znaleźć informacji, napisz 'null'. Dane MUSZĄ mieć następujące nazwy:{ \"odbiorca\": { \"nazwa\":\"nazwa\", \"nip\":\"nip\", \"adres\":\"adres\"},  \"sprzedawca\":{ \"nazwa\":\"nazwa\", \"nip\":\"nip\", \"adres\":\"adres\"},\"numerFaktury\": \"numerFaktury\", \"dataWystawienia\": \"dataWystawienia\",  \"dataSprzedazy\": \"dataSprzedazy\", \"terminPlatnosci\": \"terminPlatnosci\", \"razemNetto\": \"razemNetto\",  \"razemVAT\": \"razemVAT\",  \"razemBrutto\": \"razemBrutto\", \"doZaplaty\": \"doZaplaty\", \"waluta\": \"waluta\",  \"formaPlatnosci\": \"formaPlatnosci\",   \"produkty\": [    {     \"nazwaProduktu\": \"nazwaProduktu\",    \"jednostkaMiary\": \"jednostkaMiary\" (zobacz czy nie istnieje skrót 'j. m.' zapisz wartość jako value jednostkiMiary. key jednostkaMiary bez zmian),      \"ilosc\":  \"ilosc\"(jeśli ilość nie jest integerem napisz tylko float np. 0.55, zawsze w String),  \"wartoscNetto\": \"wartoscNetto\",  \"stawkaVat\": \"stawkaVat\", \"wartoscBrutto\": \"wartoscBrutto\" }  ]}"

object InvoicePrompts {

    val extractInvoicePrompt = """
        Twoim zadaniem jest wyodrębnienie danych z faktury na podstawie tekstu OCR i uzupełnienie gotowego JSON-a w podanej strukturze Kotlin DTO.

        Zasady:

        1. Nie zmyślaj danych – jeżeli nie ma jakiejś informacji, wstaw "null" (jako string).
        2. W polach liczbowych również wpisz "null" jeśli nie jesteś w stanie znaleźć danej liczby.
        3. Zachowuj dokładnie nazwy pól – jak w strukturze poniżej.
        4. Lista produktów musi zawierać tyle obiektów, ile jest pozycji na fakturze. Jeśli nie rozpoznajesz którejś pozycji – pomiń ją.
        5. Data musi mieć format YYYY-MM-DD lub "null" jeśli nie istnieje.
        6. Jeżeli nie możesz określić waluty – wpisz "PLN"
        7. Wszystkie wartości mają być typu string – również liczby (np. "123.45").
        8. odpowiedz daj tylko w json, nic więcej

        Struktura JSON do uzupełnienia:

        {
          "typFaktury": "null",
          "numerFaktury": "null",
          "dataWystawienia": "null",
          "dataSprzedazy": "null",
          "miejsceWystawienia": "null",
          "razemNetto": "null",
          "razemVAT": "null",
          "razemBrutto": "null",
          "doZaplaty": "null",
          "waluta": "PLN",
          "formaPlatnosci": "null",
          "odbiorca": {
            "nazwa": "null",
            "nip": "null",
            "adres": "null",
            "kodPocztowy": "null",
            "miejscowosc": "null",
            "kraj": "null",
            "opis": "null",
            "email": "null",
            "telefon": "null"
          },
          "sprzedawca": {
            "nazwa": "null",
            "nip": "null",
            "adres": "null",
            "kodPocztowy": "null",
            "miejscowosc": "null",
            "kraj": "null",
            "opis": "null",
            "email": "null",
            "telefon": "null"
          },
          "produkty": [
            {
              "nazwaProduktu": "null",
              "jednostkaMiary": "null",
              "cenaNetto": "null",
              "stawkaVat": "null",
              "ilosc": "null",
              "rabat": "null",
              "wartoscNetto": "null",
              "wartoscBrutto": "null",
              "pkwiu": "null"
            }
          ]
        }
    """.trimIndent()
}
