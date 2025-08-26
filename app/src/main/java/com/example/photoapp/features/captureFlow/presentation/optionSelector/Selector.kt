package com.example.photoapp.features.captureFlow.presentation.optionSelector

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.photoapp.core.components.common.MenuButton

@Composable
fun OptionSelector(
    onOpenCamera: () -> Unit,
    onAddByHand: () -> Unit,
    onAddPDF: () -> Unit,
    onImageCaptured: (Uri, Bitmap) -> Unit,
    backToHomeScreen: () -> Unit
) {
    var openCamera by remember { mutableStateOf(false) }
    var openGallery by remember { mutableStateOf(false) }

    if (openCamera == false && openGallery == false) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                MenuButton(text = "Otwórz aparat", onClick = {onOpenCamera()}, color = Color(0xFFD0BCFF))
                MenuButton(text = "Dodaj zdjęcie z Galerii", onClick = {openGallery = false}, color = Color(0xFFD0BCFF))
                MenuButton(text = "Dodaj fakture z pliku PDF", onClick = {onAddPDF()}, color = Color(0xFFD0BCFF))
                MenuButton(text = "Dodaj fakture ręcznie", onClick = {onAddByHand()}, color = Color(0xFFD0BCFF))
                MenuButton(text = "Wyjdź", onClick = {backToHomeScreen()})
            }
        }
    }

    if (openGallery) {
        RequestContentPermission(onImageCaptured, closeGallery = {openCamera = false}, backToHomeScreen)
    }
}

@Composable
fun RequestContentPermission(
    onImageCaptured: (Uri, Bitmap) -> Unit,
    closeGallery: () -> Unit,
    backToHomeScreen: () -> Unit
) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current
    var bitmap by remember {
        mutableStateOf<Bitmap?>(null)
    }

    val launcher = rememberLauncherForActivityResult(contract =
        ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
        Log.i("Dolan", "GOT THE IMAGE URI CAN LAUNCH FUCTION NOW")
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            OutlinedButton(
                onClick = {
                    closeGallery()
                }) {
                Text(text = "Wróć")
            }
            if (imageUri == null) {
                Button(
                    onClick = {
                        launcher.launch("image/*")
                    }) { Text("Wybierz zdjęcie") }
            } else {
                OutlinedButton(
                    onClick = {
                        launcher.launch("image/*")
                    }) { Text(text = "Wybierz inne") }
            }
            if (imageUri != null) {
                Button(
                    onClick = {
                        onImageCaptured(imageUri!!, bitmap!!)
                    }) {
                    Text(text = "Zatwierdź")
                }
            }


            Spacer(modifier = Modifier.height(12.dp))

            imageUri?.let {
                if (Build.VERSION.SDK_INT < 28) {
                    bitmap = MediaStore.Images
                        .Media.getBitmap(context.contentResolver, it)

                } else {
                    val source = ImageDecoder
                        .createSource(context.contentResolver, it)
                    bitmap = ImageDecoder.decodeBitmap(source)
                }

                bitmap?.let { btm ->
                    Image(
                        bitmap = btm.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.size(400.dp)
                    )
                }
            }
        }
    }
}
