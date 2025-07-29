package com.example.photoapp.features.captureFlow.presentation.acceptPhoto

import android.graphics.Bitmap
import android.net.Uri

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.rememberAsyncImagePainter
import com.example.photoapp.features.faktura.data.faktura.Faktura
import com.example.photoapp.features.faktura.presentation.details.ProduktFakturaZProduktem
import com.example.photoapp.features.odbiorca.data.Odbiorca
import com.example.photoapp.features.sprzedawca.data.Sprzedawca

@Composable
fun AcceptPhoto(
    photoUri: Uri?,
    bitmapPhoto: Bitmap?,
    modifier: Modifier = Modifier.fillMaxSize(),
    contentDescription: String?,
    backToCameraView: () -> Unit,
    goToAcceptFakturaScreen: (Faktura, Sprzedawca, Odbiorca, List<ProduktFakturaZProduktem>) -> Unit,
    backToHome: () -> Unit,
    geminiKey: String,
    acceptanceController: AcceptanceController = hiltViewModel()
    ) {
    var isLoading by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var dialogData by remember { mutableStateOf("") }
    var isPromptSuccess by remember { mutableStateOf(true) }

    val faktura by acceptanceController.faktura.collectAsState()
    val sprzedawca by acceptanceController.sprzedawca.collectAsState()
    val odbiorca by acceptanceController.odbiorca.collectAsState()
    val produkty by acceptanceController.produkty.collectAsState()

    val alphaAnimation by animateFloatAsState(
        targetValue = if (isLoading) 1f else 0f,
        animationSpec = tween(durationMillis = 300) // Animacja przejścia w 300 ms
    )

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)) // Półprzezroczyste tło
        )
    }
    CircularProgressIndicator(
        modifier = Modifier
            .graphicsLayer(alpha = alphaAnimation) // Animacja przezroczystości
    )

    Column(modifier = Modifier.fillMaxSize()) {

        // PHOTO preview
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // zdjęcie zajmuje większość ekranu
                .background(Color.Black)
        ) {
            Image(
                painter = rememberAsyncImagePainter(photoUri),
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }

        // BUTTONS
        ButtonsLayout(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onRetry = { backToCameraView() },
            onOk = {
                isLoading = true
                acceptanceController.processPhoto(geminiKey, bitmapPhoto) { success, result ->
                    isLoading = false
                    isPromptSuccess = success
                    dialogData = result
                    if (isPromptSuccess) {
                        goToAcceptFakturaScreen(faktura, sprzedawca, odbiorca, produkty)
                    } else {
                        showDialog = true
                    }
                }
            }
        )
    }

    if (showDialog) {
        ShowDialog(
            dialogData
        ) {
            showDialog = false
        }
    }
}

@Composable
fun ShowDialog(
    data: String,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            Button(
                onClick = { onDismiss() }
            ) {
                Text("Ok")
            }
        },
        dismissButton = {
            Button(onClick = {
                onDismiss()
            }) {
                Text("Anuluj")
            }
        },
        title = {
            Text("Powtórz zapytanie")
        },
        text = {
            Box(
                modifier = Modifier
                    .heightIn(min = 100.dp, max = 300.dp) // Ograniczenie wysokości tekstu
                    .verticalScroll(rememberScrollState()) // Dodanie przewijalności
            ) {
                Text(data)
            }
        }
    )
}

@Composable
fun ButtonsLayout(
    modifier: Modifier,
    onRetry: () -> Unit,
    onOk: () -> Unit
) {
    Row(
        modifier = modifier
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .fillMaxWidth()
            .navigationBarsPadding(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 🔁 POWTÓRZ
        TextButton(
            onClick = onRetry,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color.LightGray),
            colors = ButtonDefaults.textButtonColors(
                contentColor = Color.Black,
                containerColor = Color.Transparent
            ),
            modifier = Modifier
                .height(56.dp)
                .weight(1f)
        ) {
            Text("Powtórz", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.width(16.dp))

        // ✅ ZATWIERDŹ
        Button(
            onClick = onOk,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ),
            modifier = Modifier
                .height(56.dp)
                .weight(1f)
        ) {
            Text("Zatwierdź", fontSize = 16.sp)
        }
    }
}
