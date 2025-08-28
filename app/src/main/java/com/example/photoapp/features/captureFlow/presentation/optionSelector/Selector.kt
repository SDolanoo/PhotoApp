package com.example.photoapp.features.captureFlow.presentation.optionSelector

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ImageDecoder
import android.graphics.pdf.PdfRenderer
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
import androidx.core.content.FileProvider
import com.example.photoapp.core.components.common.MenuButton
import com.example.photoapp.features.captureFlow.presentation.optionSelector.options.addByPDFFaktura.bitmapToUri
import com.example.photoapp.features.captureFlow.presentation.optionSelector.options.addByPDFFaktura.renderFirstPageFromPdf
import java.io.File
import java.io.FileOutputStream

@Composable
fun OptionSelector(
    onOpenCamera: () -> Unit,
    onAddByHand: () -> Unit,
    onPdfPicked: (uri: Uri, bitmap: Bitmap) -> Unit,
    onImageCaptured: (Uri, Bitmap) -> Unit,
    backToHomeScreen: () -> Unit
) {
    var openCamera by remember { mutableStateOf(false) }
    var openGallery by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            if (uri != null) {
                try {
                    context.contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (_: SecurityException) {
                    // już ma uprawnienia
                }

                val bitmap = renderFirstPageFromPdf(context, uri)
                if (bitmap != null) {
                    val imageUri = bitmapToUri(context, bitmap)
                    onPdfPicked(imageUri, bitmap)
                }
            }
        }
    )

    if (openCamera == false && openGallery == false) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                MenuButton(text = "Otwórz aparat", onClick = {onOpenCamera()}, color = Color(0xFF6650a4))
                Spacer(modifier = Modifier.height(8.dp))
                MenuButton(text = "Dodaj zdjęcie z Galerii", onClick = {openGallery = false}, color = Color(0xFF6650a4))
                Spacer(modifier = Modifier.height(8.dp))
                MenuButton(text = "Dodaj fakture z pliku PDF", onClick = {pdfPickerLauncher.launch(arrayOf("application/pdf"))}, color = Color(0xFF6650a4))
                Spacer(modifier = Modifier.height(8.dp))
                MenuButton(text = "Dodaj fakture ręcznie", onClick = {onAddByHand()}, color = Color(0xFF6650a4))
                Spacer(modifier = Modifier.height(8.dp))
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

fun renderFirstPageFromPdf(context: Context, uri: Uri): Bitmap? {
    return try {
        val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
        val pdfRenderer = PdfRenderer(parcelFileDescriptor!!)
        val page = pdfRenderer.openPage(0)

        // Wymiary docelowe (np. A4 high-res)
        val targetWidth = 2604
        val targetHeight = 4624

        val scaleX = targetWidth.toFloat() / page.width.toFloat()
        val scaleY = targetHeight.toFloat() / page.height.toFloat()
        val scale = minOf(scaleX, scaleY) // zachowaj proporcje

        val bitmap = Bitmap.createBitmap(
            (page.width * scale).toInt(),
            (page.height * scale).toInt(),
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)
        canvas.scale(scale, scale)
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

        page.close()
        pdfRenderer.close()

        bitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}



fun bitmapToUri(context: Context, bitmap: Bitmap): Uri {
    val file = File(context.cacheDir, "pdf_preview_${System.currentTimeMillis()}.jpg")
    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
    }

    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider", // 👈 TO MUSI SIĘ ZGADZAĆ Z manifestem
        file
    )
}
