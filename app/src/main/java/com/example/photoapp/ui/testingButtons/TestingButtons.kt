package com.example.photoapp.ui.testingButtons

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.photoapp.features.raportFiskalny.data.ProduktRaportFiskalny
import com.example.photoapp.features.raportFiskalny.data.RaportFiskalny
import java.util.Calendar

@Composable
fun TestingButtons(
    backToHome: () -> Unit,
//    databaseViewModel: DatabaseViewModel = hiltViewModel(),
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Button(
            onClick = { backToHome() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 30.dp, bottom = 8.dp)
        ) {
            Text(text = "Cofnij stronę")
        }

//        Button(
//            onClick = { databaseViewModel.addUser(
//                login = "dolan",
//                password = "123",
//                email = "dolan@dolan.com") },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(bottom = 8.dp)
//        ) {
//            Text(text = "Add uzytkownik")
//        }
//
//        Button(
//            onClick = { databaseViewModel.addTestRecipe() },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(bottom = 8.dp)
//        ) {
//            Text(text = "Add Test Paragony")
//        }
//
//        Button(
//            onClick = { databaseViewModel.addTestRecipeProducts() },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(bottom = 8.dp)
//        ) {
//            Text(text = "Add Test Recipe Products")
//        }

        Button(
            onClick = { /* TODO: addTestInvoices */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Text(text = "Add Test Invoices")
        }

        Button(
            onClick = { /* TODO: addTestInvoicesProducts */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Text(text = "Add Test Invoices Products")
        }
//
//        Button(
//            onClick = {
//                for (i in 0 .. 13) {
//                    val raportFiskalny = RaportFiskalny(
//                        dataDodania = Calendar.getInstance().apply {
//                            set(2025, 1, i)
//                        }.time
//                    )
//                    databaseViewModel.insertRaportFiskalny(raportFiskalny)
//                }
//            },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(bottom = 8.dp)
//        ) {
//            Text(text = "Add Test Raporty Fiskalne")
//        }
//
//        Button(
//            onClick = {
//                val raporty = databaseViewModel.getAllRaportFiskalny()
//
//                for (raport in raporty) {
//                    for (i in 4 .. 8) {
//                        val produkt = ProduktRaportFiskalny(
//                            raportFiskalnyId = raport.id,
//                            nrPLU = "${i * 2}",
//                            ilosc = "${i * 3}"
//                        )
//                        databaseViewModel.insertProduktRaportFiskalny(produkt)
//                    }
//            }
//            },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(bottom = 8.dp)
//        ) {
//            Text(text = "Add Test Produkty Raport Fiskalny")
//        }
    }
}
