package com.example.photoapp.core.AI

// old prompt                 DocumentType.FAKTURA -> "przeczytaj zdjęcie faktury i uzyskaj z niego nastepujące informacje. Całość napisz w podanym formacie json zmieniając tylko value, key muszą zostać nie zmienione. Napisz tylko json w podanym niżej formacie. Jeśli jest informacja w nawiasie, zastosuj się do tej informacji, nie wstawiając jej w odpowiedź. Jeśli nie udało się znaleźć informacji, napisz 'null'. Dane MUSZĄ mieć następujące nazwy:{ \"odbiorca\": { \"nazwa\":\"nazwa\", \"nip\":\"nip\", \"adres\":\"adres\"},  \"sprzedawca\":{ \"nazwa\":\"nazwa\", \"nip\":\"nip\", \"adres\":\"adres\"},\"numerFaktury\": \"numerFaktury\", \"dataWystawienia\": \"dataWystawienia\",  \"dataSprzedazy\": \"dataSprzedazy\", \"terminPlatnosci\": \"terminPlatnosci\", \"razemNetto\": \"razemNetto\",  \"razemVAT\": \"razemVAT\",  \"razemBrutto\": \"razemBrutto\", \"doZaplaty\": \"doZaplaty\", \"waluta\": \"waluta\",  \"formaPlatnosci\": \"formaPlatnosci\",   \"produkty\": [    {     \"nazwaProduktu\": \"nazwaProduktu\",    \"jednostkaMiary\": \"jednostkaMiary\" (zobacz czy nie istnieje skrót 'j. m.' zapisz wartość jako value jednostkiMiary. key jednostkaMiary bez zmian),      \"ilosc\":  \"ilosc\"(jeśli ilość nie jest integerem napisz tylko float np. 0.55, zawsze w String),  \"wartoscNetto\": \"wartoscNetto\",  \"stawkaVat\": \"stawkaVat\", \"wartoscBrutto\": \"wartoscBrutto\" }  ]}"

object InvoicePrompts {

    val extractInvoicePrompt = """
        Twoim zadaniem jest wyodrębnienie ogólnych danych z faktury na podstawie tekstu OCR i uzupełnienie gotowego JSON-a w podanej strukturze Kotlin DTO.
        
        Zasady:
        1. Nie zmyślaj danych – jeżeli nie ma jakiejś informacji, wstaw "-" (jako string).
        2. W polach liczbowych również wpisz "-" jeśli nie jesteś w stanie znaleźć danej liczby.
        3. Zachowuj dokładnie nazwy pól – jak w strukturze poniżej.
        4. Data musi mieć format YYYY-MM-DD lub "-" jeśli nie istnieje.
        5. Jeżeli nie możesz określić waluty – wpisz "PLN".
        6. Wszystkie wartości mają być typu string – również liczby (np. "123.45").
        7. Odpowiedź daj tylko w JSON, nic więcej.
        
        Struktura JSON do uzupełnienia:
        {
          "typFaktury": "-",
          "numerFaktury": "-",
          "dataWystawienia": "-",
          "dataSprzedazy": "-",
          "miejsceWystawienia": "-",
          "razemNetto": "-",
          "razemVAT": "-",
          "razemBrutto": "-",
          "doZaplaty": "-",
          "waluta": "PLN",
          "formaPlatnosci": "-"
        }
        """.trimIndent()

    val extractSellerBuyerPrompt = """
        Twoim zadaniem jest wyodrębnienie danych o odbiorcy i sprzedawcy z faktury na podstawie tekstu OCR i uzupełnienie gotowego JSON-a w podanej strukturze Kotlin DTO.
        
        Zasady:
        1. Nie zmyślaj danych – jeżeli nie ma jakiejś informacji, wstaw "-" (jako string).
        2. Zachowuj dokładnie nazwy pól – jak w strukturze poniżej.
        3. Wszystkie wartości mają być typu string.
        4. Odpowiedź daj tylko w JSON, nic więcej.
        
        Struktura JSON do uzupełnienia:
        {
          "odbiorca": {
            "nazwa": "-",
            "nip": "-",
            "adres": "-",
            "kodPocztowy": "-",
            "miejscowosc": "-",
            "kraj": "-",
            "opis": "-",
            "email": "-",
            "telefon": "-"
          },
          "sprzedawca": {
            "nazwa": "-",
            "nip": "-",
            "adres": "-",
            "kodPocztowy": "-",
            "miejscowosc": "-",
            "kraj": "-",
            "opis": "-",
            "email": "-",
            "telefon": "-"
          }
        }
        """.trimIndent()

    val extractProductsPrompt = """
        Twoim zadaniem jest wyodrębnienie listy produktów z faktury na podstawie tekstu OCR i uzupełnienie gotowego JSON-a w podanej strukturze Kotlin DTO.
        
        Zasady:
        1. Lista produktów musi zawierać tyle obiektów, ile jest pozycji na fakturze. Jeśli nie rozpoznajesz którejś pozycji – pomiń ją.
        2. Wszystkie wartości mają być typu string – również liczby (np. "123.45").
        3. Jeżeli nie możesz znaleźć danej informacji, wstaw "-".
        4. Odpowiedź daj tylko w JSON, nic więcej.
        
        Struktura JSON do uzupełnienia:
        {
          "produkty": [
            {
              "nazwaProduktu": "-",
              "jednostkaMiary": "-",
              "cenaNetto": "-",
              "stawkaVat": "-",
              "ilosc": "-",
              "rabat": "-",
              "wartoscNetto": "-",
              "wartoscBrutto": "-",
              "pkwiu": "-"
            }
          ]
        }
        """.trimIndent()

}
