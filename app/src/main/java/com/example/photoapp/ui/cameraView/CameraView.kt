package com.example.photoapp.ui.cameraView

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

lateinit var photoUri: Uri
lateinit var filePath: String


@Composable
fun CameraView(
    outputDirectory: File,
    executor: Executor,
    onImageCaptured: (Uri, Bitmap) -> Unit,
    onError: (ImageCaptureException) -> Unit,
    backToHomeScreen: () -> Unit
) {


    var openCamera by remember { mutableStateOf(false) }
    var openGallery by remember { mutableStateOf(false) }

    // 3

    if (openCamera == false && openGallery == false) {
        Box(modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(onClick = {openCamera = true}) { Text("Otwórz aparat") }
                Button(onClick = {openGallery = true}) { Text("Otwórz Galerie") }
                Button(onClick = {backToHomeScreen()}) { Text("Wyjdź") }
            }
        }
    }

    if (openCamera) {
        MakePhoto(outputDirectory, executor, onImageCaptured, onError, backToHomeScreen)
    }

    if (openGallery) {
        RequestContentPermission(onImageCaptured, closeGallery = {openCamera = false}, backToHomeScreen)
    }
}

@Composable
fun MakePhoto(
    outputDirectory: File,
    executor: Executor,
    onImageCaptured: (Uri, Bitmap) -> Unit,
    onError: (ImageCaptureException) -> Unit,
    backToHomeScreen: () -> Unit
) {
    // 1
    val lensFacing = CameraSelector.LENS_FACING_BACK
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    Log.i("Dolan", "lifecycleOwner = $lifecycleOwner")

    val preview = Preview.Builder().build()
    val previewView = remember { PreviewView(context) }
    val imageCapture: ImageCapture = remember { ImageCapture.Builder().build() }
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing)
        .build()

//    fun goBack() {
//        onImageCaptured()
//    }
    // 2
    LaunchedEffect(lensFacing) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture
        )

        preview.setSurfaceProvider(previewView.surfaceProvider)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView({ previewView }, modifier = Modifier.fillMaxSize())

        IconButton(
            modifier = Modifier
                .padding(bottom = 50.dp)
                .align(Alignment.BottomCenter),
            onClick = {
                Log.i("kilo", "ON CLICK")
                takePhoto(
                    filenameFormat = "yyyy-MM-dd-HH-mm-ss-SSS",
                    imageCapture = imageCapture,
                    outputDirectory = outputDirectory,
                    executor = executor,
                    onImageCaptured = onImageCaptured,
                    onError = onError
                )

            },
            content = {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Take picture",
                    tint = Color.White,
                    modifier = Modifier
                        .size(100.dp)
                        .padding(1.dp)
                        .border(1.dp, Color.White, CircleShape)
                )
            }
        )

        IconButton(
            modifier = Modifier
                .padding(all = 20.dp)
                .align(Alignment.TopStart),
            onClick = { backToHomeScreen() },

            content = {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White,
                    modifier = Modifier
                        .size(100.dp)
                        .padding(1.dp)
                        .border(1.dp, Color.White, CircleShape)
                )
            }
        )
    }
}


private fun convertPhotoToBitmap(photoFile: File): Bitmap? {
    return try {
        // Decode the file to create a Bitmap
        BitmapFactory.decodeFile(photoFile.absolutePath)
    } catch (e: Exception) {
        Log.e("Dolan", "Error converting photo to bitmap", e)
        null
    }
}

private fun takePhoto(
    filenameFormat: String,
    imageCapture: ImageCapture,
    outputDirectory: File,
    executor: Executor,
    onImageCaptured: (Uri, Bitmap) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {

    val photoFile = File(
        outputDirectory,
        SimpleDateFormat(filenameFormat, Locale.US).format(System.currentTimeMillis()) + ".jpg"
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(outputOptions, executor, object: ImageCapture.OnImageSavedCallback {
        override fun onError(exception: ImageCaptureException) {
            Log.e("Dolan", "Take photo error:", exception)
            onError(exception)
        }

        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
            Log.i("Dolan", "Successfully taken photo")
            val savedUri = Uri.fromFile(photoFile)
            Log.i("Dolan", "photo saved")
            photoUri = savedUri
            Log.i("Dolan", "photoUri saved")
            filePath = photoFile.absolutePath
            Log.i("Dolan", "filePath saved at path $filePath")

            val bitmapPhoto = convertPhotoToBitmap(photoFile)

            if (bitmapPhoto != null) {
                Log.i("Dolan", "onImageCaptured")
                onImageCaptured(savedUri, bitmapPhoto)
            } else {
                Log.i("Dolan", "bitmapPhoto is empty1!!!")
            }
        }
    })
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also { cameraProvider ->
        cameraProvider.addListener({
            continuation.resume(cameraProvider.get())
        }, ContextCompat.getMainExecutor(this))
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



//    LaunchedEffect(imageUri) {
//        onImageCaptured(imageUri, bitmap)
//    }

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
