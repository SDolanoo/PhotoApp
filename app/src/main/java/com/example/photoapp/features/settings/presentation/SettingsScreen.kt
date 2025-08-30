package com.example.photoapp.features.settings.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import com.google.firebase.auth.FirebaseAuth
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.auth.EmailAuthProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onDelete: () -> Unit,
    onBack: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showReauthDialog by remember { mutableStateOf(false) }

    var userEmail by remember { mutableStateOf("Ładowanie...") }

    LaunchedEffect(Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            try {
                user.reload().addOnSuccessListener {
                    userEmail = user.email ?: "brak adresu e-mail"
                }.addOnFailureListener {
                    userEmail = "Błąd ładowania e-maila"
                }
            } catch (e: Exception) {
                userEmail = "Wystąpił błąd: ${e.localizedMessage}"
            }
        } else {
            userEmail = "Użytkownik niezalogowany"
        }
    }

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ustawienia") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Wstecz")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Konto", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Adres e-mail", style = MaterialTheme.typography.labelSmall)
                        Text(userEmail, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }

            item {
                Text("Informacje", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Wersja aplikacji")
                        Text("1.0.0")
                    }
                }

                Spacer(Modifier.height(8.dp))

                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Kontakt:  ")
                        Text("kontaktdolandev@gmail.com")
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))

                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Black
                    ),
                    border = BorderStroke(1.dp, Color.Red)
                ) {
                    Text("Usuń konto")
                }
            }

        }

        // Dialog potwierdzający usunięcie konta
        if (showDeleteDialog) {
            ConfirmDeleteAccountDialog(
                onDismiss = { showDeleteDialog = false },
                onConfirm = {
                    showDeleteDialog = false
                    showReauthDialog = true // ✅ Wywołujesz dialog do podania hasła
                }
            )
        }

        if (showReauthDialog) {
            ReauthDialog(
                onDismiss = { showReauthDialog = false },
                onPasswordConfirmed = { password ->
                    reauthenticateAndDeleteAccount(
                        context = context,
                        password = password,
                        onSuccess = {
                            onDelete()
                        },
                        onFailure = { error ->
                            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                        }
                    )
                    showReauthDialog = false
                }
            )
        }
    }
}

@Composable
fun ConfirmDeleteAccountDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Usuń konto") },
        text = { Text("Czy na pewno chcesz usunąć konto? Tej operacji nie można cofnąć.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Usuń", color = Color.Red)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Anuluj")
            }
        }
    )
}

fun reauthenticateAndDeleteAccount(
    context: Context,
    password: String,
    onSuccess: () -> Unit,
    onFailure: (String) -> Unit
) {
    val user = FirebaseAuth.getInstance().currentUser
    val email = user?.email

    if (user == null || email == null) {
        onFailure("Użytkownik niezalogowany lub brak adresu e-mail.")
        return
    }

    val credential = EmailAuthProvider.getCredential(email, password)

    user.reauthenticate(credential)
        .addOnSuccessListener {
            user.delete()
                .addOnSuccessListener {
                    Toast.makeText(context, "Konto zostało usunięte.", Toast.LENGTH_LONG).show()
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    onFailure("Błąd usuwania konta: ${e.localizedMessage}")
                }
        }
        .addOnFailureListener { e ->
            onFailure("Błąd uwierzytelnienia: złe hasło")
        }
}


@Composable
fun ReauthDialog(
    onDismiss: () -> Unit,
    onPasswordConfirmed: (String) -> Unit
) {
    var password by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Potwierdź tożsamość") },
        text = {
            Column {
                Text("Wpisz swoje hasło, aby potwierdzić usunięcie konta.")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Hasło") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onPasswordConfirmed(password)
            }) {
                Text("Potwierdź")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Anuluj")
            }
        }
    )
}
