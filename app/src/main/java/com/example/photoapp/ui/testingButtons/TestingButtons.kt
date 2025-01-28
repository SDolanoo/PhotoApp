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
import com.example.photoapp.database.DatabaseViewModel

@Composable
fun TestingButtons(
    backToHome: () -> Unit,
    databaseViewModel: DatabaseViewModel = hiltViewModel(),
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Button(
            onClick = { backToHome() },
            modifier = Modifier.fillMaxWidth().padding(top = 30.dp, bottom = 8.dp)
        ) {
            Text(text = "Cofnij stronę")
        }

        Button(
            onClick = { databaseViewModel.addUser(
                login = "dolan",
                password = "123",
                email = "dolan@dolan.com") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        ) {
            Text(text = "Add uzytkownik")
        }

        Button(
            onClick = { databaseViewModel.addTestRecipe() },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        ) {
            Text(text = "Add Test Paragony")
        }

        Button(
            onClick = { databaseViewModel.addTestRecipeProducts() },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        ) {
            Text(text = "Add Test Recipe Products")
        }

        Button(
            onClick = { /* TODO: addTestInvoices */ },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        ) {
            Text(text = "Add Test Invoices")
        }

        Button(
            onClick = { /* TODO: addTestInvoicesProducts */ },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        ) {
            Text(text = "Add Test Invoices Products")
        }
    }
}
