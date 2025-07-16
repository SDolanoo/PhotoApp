package com.example.photoapp.ui.acceptPhoto

import android.graphics.Bitmap
import android.net.Uri

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.rememberAsyncImagePainter

@Composable
fun AcceptPhoto(
    photoUri: Uri?,
    bitmapPhoto: Bitmap?,
    addingPhotoFor: String?,
    modifier: Modifier = Modifier.fillMaxSize(),
    contentDescription: String?,
    backToCameraView: () -> Unit,
    backToHome: () -> Unit,
    geminiKey: String,
    acceptanceController: AcceptanceController = hiltViewModel()
    ) {
    var isLoading by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var dialogData by remember { mutableStateOf("") }
    var isPromptSuccess by remember { mutableStateOf(true) }

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

    Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center)
    {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = rememberAsyncImagePainter(photoUri),
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = ContentScale.Crop,
            )
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            ButtonsLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 50.dp)
                    .align(Alignment.BottomCenter),
                onRetry = { backToCameraView() },
                onOk = {
                    isLoading = true
                    acceptanceController.processPhoto(addingPhotoFor, geminiKey, bitmapPhoto) { success, result ->
                        isLoading = false
                        isPromptSuccess = success
                        dialogData = result
                        showDialog = true
                    }
//                    databaseViewModel.addRecipe(bitmapPhoto)

                }
            )
        }
    }

    if (showDialog) {
        showDialog(
            dialogData,
            acceptanceController,
            addingPhotoFor,
            isPromptSuccess,
        ) {
            showDialog = false
        }
    }

}

@Composable
fun ButtonsLayout(modifier: Modifier, onRetry: () -> Unit, onOk: () -> Unit) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black.copy(alpha = 0.3f))
        ) {
            Text(text = "Powtórz", color = Color.White)
        }

        Button(
            onClick = onOk,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black.copy(alpha = 0.3f))
        ) {
            Text(text = "Zatwierdź", color = Color.White)
        }
    }
}

@Composable
fun showDialog(
    data: String,
    controller: AcceptanceController,
    addingPhotoFor: String?,
    isPromptSuccess: Boolean,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            Button(onClick = {
                if (isPromptSuccess) {
                    if (addingPhotoFor == "faktura") {
                        controller.addInvoice()
                    }
                }
                onDismiss()
            }) {
                if (isPromptSuccess) {
                    Text("Akceptuj")
                } else {
                    Text("Ok")
                }
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
            if (isPromptSuccess) {
                Text("Zatwierdzić dane?")
            } else {
                Text("Powtórz zapytanie")
            }
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

