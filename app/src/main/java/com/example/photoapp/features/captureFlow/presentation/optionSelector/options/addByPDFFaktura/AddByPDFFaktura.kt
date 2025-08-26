package com.example.photoapp.features.captureFlow.presentation.optionSelector.options.addByPDFFaktura

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.content.Intent
import android.graphics.Canvas
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import org.apache.poi.hssf.usermodel.HeaderFooter.file
import java.io.File
import java.io.FileOutputStream

@Composable
fun AddByPDFFaktura(
    onPdfPicked: (uri: Uri, bitmap: Bitmap) -> Unit
) {
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

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = {
                pdfPickerLauncher.launch(arrayOf("application/pdf"))
            }) {
            Text("Wybierz PDF")
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
