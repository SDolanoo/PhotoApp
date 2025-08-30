package com.example.photoapp.core.navigation


import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.photoapp.features.faktura.presentation.screen.FakturaScreen
import com.example.photoapp.features.excelPacker.presentation.ExcelPacker
import com.example.photoapp.features.selector.presentation.selector.SelectorScreen
import com.example.photoapp.features.captureFlow.presentation.acceptFaktura.AcceptFakturaScreen
import com.example.photoapp.features.captureFlow.presentation.acceptPhoto.AcceptPhoto
import com.example.photoapp.features.captureFlow.presentation.optionSelector.options.cameraView.CameraView
import com.example.photoapp.features.faktura.data.faktura.Faktura
import com.example.photoapp.features.faktura.presentation.details.FakturaDetailsScreen
import com.example.photoapp.features.login.LoginScreen
import com.example.photoapp.features.odbiorca.data.Odbiorca
import com.example.photoapp.features.selector.presentation.selector.odbiorca.details.OdbiorcaDetailsScreen
import com.example.photoapp.features.selector.presentation.selector.produkt.details.ProduktDetailsScreen
import com.example.photoapp.features.selector.presentation.selector.sprzedawca.details.SprzedawcaDetailsScreen
import com.example.photoapp.features.sprzedawca.data.Sprzedawca
import com.example.photoapp.features.MainDrawer
import com.example.photoapp.features.captureFlow.presentation.optionSelector.OptionSelector
import com.example.photoapp.features.captureFlow.presentation.optionSelector.options.addByHandFaktura.AddByHandFaktura
import com.example.photoapp.features.settings.presentation.SettingsScreen
import com.example.photoapp.ui.testingButtons.TestingButtons
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.ExecutorService


@Composable
fun PhotoAppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    /* CameraView Variables */
    outputDirectory: File,
    executor: ExecutorService,
    geminiKey: String,
    navGraphViewModel: NavGraphViewModel = hiltViewModel()
) {
    var drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val fakturaViewedNow by navGraphViewModel.fakturaViewedNow.collectAsState()

    // Observable states
    val photoUri by navGraphViewModel.photoUri.observeAsState()
    val photoBitmap by navGraphViewModel.photoBitmap.observeAsState()

    val produkt by navGraphViewModel.produkt.collectAsState()
    val faktura by navGraphViewModel.faktura.collectAsState()
    val sprzedawca by navGraphViewModel.sprzedawca.collectAsState()
    val odbiorca by navGraphViewModel.odbiorca.collectAsState()
    val produkty by navGraphViewModel.produkty.collectAsState()

    var lastSeenSelector by remember { mutableStateOf("main") }

    // Example function called when photoUri changes
    fun onPhotoUriChanged() {
        Log.i("PhotoApp", "Photo URI changed: $photoUri")
        Log.i("PhotoApp", "Photo Bitmap changed: $photoBitmap")
        navController.navigate(PhotoAppDestinations.ACCEPT_PHOTO_ROUTE)
        // Add your logic here, e.g., save to database, perform navigation, etc.
    }

    LaunchedEffect(photoUri) {
        photoUri?.let {
            onPhotoUriChanged() // Trigger your desired logic
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (FirebaseAuth.getInstance().currentUser?.email.isNullOrEmpty()) PhotoAppDestinations.LOGIN_SCREEN_ROUTE else PhotoAppDestinations.FAKTURA_SCREEN_ROUTE,
        modifier = modifier
    ) {
        composable(PhotoAppDestinations.FAKTURA_SCREEN_ROUTE) {
            Log.i("Dolan", "Odpalam FAKTURA_SCREEN w navGraph")

            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    MainDrawer(
                        navigateToMakePhoto = { navController.navigate(PhotoAppDestinations.OPTION_SELECTOR_ROUTE) },
                        navigateToExport = {navController.navigate(PhotoAppDestinations.EXCEL_PACKER_ROUTE)},
                        navigateToSettings = {navController.navigate(PhotoAppDestinations.SETTINGS_ROUTE)},
                        closeDrawer = { coroutineScope.launch {drawerState = drawerState.apply { close() }}},
                        onSignout = {
                            coroutineScope.launch {drawerState = drawerState.apply { close() }}
                            FirebaseAuth.getInstance().signOut().run {
                                navController.navigate(PhotoAppDestinations.LOGIN_SCREEN_ROUTE)
                            }
                        }
                    )
                },
                gesturesEnabled = true
            ) {
                FakturaScreen(
                    navController = navController,
                    navigateToCameraView = { addingPhotoFor ->
                        navGraphViewModel.setAddingPhotoFor(addingPhotoFor)
                        navController.navigate(PhotoAppDestinations.OPTION_SELECTOR_ROUTE)},
                    navigateToFakturaDetailsScreen = { faktura ->
                        navGraphViewModel.setFakturaViewedNow(faktura)
                        navController.navigate(PhotoAppDestinations.FAKTURA_DETAILS_SCREEN_ROUTE)
                    },
                    openDrawer = { coroutineScope.launch {drawerState = drawerState.apply { open() }} }
                )
            }
        }

        composable(PhotoAppDestinations.FAKTURA_DETAILS_SCREEN_ROUTE) {
            Log.i("Dolan", "Odpalam FAKTURA_DETAILS_SCREEN_ROUTE w navGraph")
            FakturaDetailsScreen(
                faktura = fakturaViewedNow,
                leaveDetailsScreen = { navController.navigate(PhotoAppDestinations.FAKTURA_SCREEN_ROUTE) }
            )
        }

        composable(PhotoAppDestinations.OPTION_SELECTOR_ROUTE) {
            Log.i("Dolan", "Odpalam OPTION_SELECTOR_ROUTE w navGraph")
            OptionSelector(
                onOpenCamera = {navController.navigate(PhotoAppDestinations.MAKE_PHOTO_ROUTE)},
                onAddByHand = {navController.navigate(PhotoAppDestinations.ADD_BY_HAND_ROUTE)},
                onPdfPicked = { uri, photoBitmap ->
                    Log.i("Dolan", "Image captured: $uri")
                    navGraphViewModel.setPhotoBitmap(photoBitmap)
                    navGraphViewModel.setPhotoUri(uri)
                },
                onImageCaptured = { uri, photoBitmap ->
                    Log.i("Dolan", "Image captured: $uri")
                    navGraphViewModel.setPhotoBitmap(photoBitmap)
                    navGraphViewModel.setPhotoUri(uri)
                },
                backToHomeScreen = { navController.navigate(PhotoAppDestinations.FAKTURA_SCREEN_ROUTE) },
            )
        }

        composable(PhotoAppDestinations.ADD_BY_HAND_ROUTE) {
            Log.i("Dolan", "Odpalam ADD_BY_HAND_ROUTE w navGraph")
            AddByHandFaktura(
                backToHomeScreen = { navController.navigate(PhotoAppDestinations.FAKTURA_SCREEN_ROUTE) },
            )
        }

        composable(PhotoAppDestinations.MAKE_PHOTO_ROUTE) {
            Log.i("Dolan", "Odpalam MAKE_PHOTO w navGraph")
            CameraView(
                outputDirectory = outputDirectory,
                executor = executor,
                onImageCaptured = { uri, photoBitmap ->
                    Log.i("Dolan", "Image captured: $uri")
                    navGraphViewModel.setPhotoBitmap(photoBitmap)
                    navGraphViewModel.setPhotoUri(uri)
                },
                onError = { Log.e("Dolan", "View error:", it) },
                backToHomeScreen = { navController.navigate(PhotoAppDestinations.FAKTURA_SCREEN_ROUTE) },
            )
        }

        composable (PhotoAppDestinations.ACCEPT_PHOTO_ROUTE) {
            Log.i("Dolan", "Odpalam ACCEPT_PHOTO w navGraph")
            AcceptPhoto(
                photoUri = photoUri,
                bitmapPhoto = photoBitmap,
                contentDescription = null,
                backToCameraView = {navController.navigate(PhotoAppDestinations.OPTION_SELECTOR_ROUTE)},
                goToAcceptFakturaScreen = { f, s, o, p ->
                    navGraphViewModel.setFaktura(f) {
                        navGraphViewModel.setSprzedawca(s) {
                            Log.i("Dolan", "faktura in ACCEPT_PHOTO_ROUTE just changed $faktura")
                            navGraphViewModel.setOdbiorca(o) {
                                navGraphViewModel.setProdukty(p) {
                                    navController.navigate(PhotoAppDestinations.ACCEPT_FAKTURA_ROUTE)
                                }
                            }
                        }
                    }
                },// I have to do this this way, cause the navigate is faster than setting objects
                backToHome = {navController.navigate(PhotoAppDestinations.FAKTURA_SCREEN_ROUTE)},
                geminiKey = geminiKey
            )
        }


        composable (PhotoAppDestinations.ACCEPT_FAKTURA_ROUTE) {
            Log.i("Dolan", "Odpalam ACCEPT_FAKTURA w navGraph")
            Log.i("Dolan", "ACCEPT_FAKTURA_ROUTE $faktura")
            if (
                faktura != Faktura.default() &&
                sprzedawca != Sprzedawca.empty() &&
                odbiorca != Odbiorca.empty() &&
                produkty.isNotEmpty()
            ) {
                AcceptFakturaScreen(
                    faktura = faktura,
                    sprzedawca = sprzedawca,
                    odbiorca = odbiorca,
                    produkty = produkty,
                    onConfirm = {navController.navigate(PhotoAppDestinations.FAKTURA_SCREEN_ROUTE)},
                    onCancel = {navController.navigate(PhotoAppDestinations.ACCEPT_PHOTO_ROUTE)},
                )
            } else {
                // Placeholder na czas ładowania danych
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }

            }
        }

        composable (PhotoAppDestinations.EXCEL_PACKER_ROUTE) {
            Log.i("Dolan", "Odpalam EXCEL_PACKER_ROUTE w navGraph")
            ExcelPacker(
                navController = navController,
            )
        }

        composable (PhotoAppDestinations.TESTING_BUTTONS_ROUTE) {
            Log.i("Dolan", "Odpalam TESTING_BUTTONS w navGraph")
            TestingButtons(
                backToHome = {navController.navigate(PhotoAppDestinations.FAKTURA_SCREEN_ROUTE)},
            )
        }

        composable(PhotoAppDestinations.EDITING_SELECTOR) {
            Log.i("Dolan", "Odpalam EDITING_SELECTOR w navGraph")
            SelectorScreen(
                navController = navController,
                goToOdbiorcaDetails = { o ->
                    navGraphViewModel.setOdbiorca(o) {
                        lastSeenSelector = "Odbiorcy"
                        navController.navigate(PhotoAppDestinations.ODBIORCA_DETAILS_SCREEN_ROUTE)}
                    },
                goToSprzedawcaDetails = { s ->
                    navGraphViewModel.setSprzedawca(s) {
                        lastSeenSelector = "Sprzedawcy"
                        navController.navigate(PhotoAppDestinations.SPRZEDAWCA_DETAILS_SCREEN_ROUTE)
                    }
                },
                goToProduktDetails = { p ->
                    navGraphViewModel.setProdukt(p) {
                        lastSeenSelector = "Produkty"
                        navController.navigate(PhotoAppDestinations.PRODUKT_DETAILS_SCREEN_ROUTE)
                    }
                },
                goBack = {
                    lastSeenSelector = "main"
                    navController.navigate(PhotoAppDestinations.FAKTURA_SCREEN_ROUTE)
                },
                lastScreen = lastSeenSelector
            )
        }

        composable(PhotoAppDestinations.ODBIORCA_DETAILS_SCREEN_ROUTE) {
            Log.i("Dolan", "Odpalam ODBIORCA_DETAILS_SCREEN_ROUTE w navGraph")
            OdbiorcaDetailsScreen(
                odbiorca = odbiorca,
                leaveDetailsScreen = {navController.navigate(PhotoAppDestinations.EDITING_SELECTOR)},
            )
        }

        composable(PhotoAppDestinations.SPRZEDAWCA_DETAILS_SCREEN_ROUTE) {
            Log.i("Dolan", "Odpalam SPRZEDAWCA_DETAILS_SCREEN_ROUTE w navGraph")
            SprzedawcaDetailsScreen(
                sprzedawca = sprzedawca,
                leaveDetailsScreen = {navController.navigate(PhotoAppDestinations.EDITING_SELECTOR)},
            )
        }

        composable(PhotoAppDestinations.PRODUKT_DETAILS_SCREEN_ROUTE) {
            Log.i("Dolan", "Odpalam PRODUKT_DETAILS_SCREEN_ROUTE w navGraph")
            ProduktDetailsScreen(
                produkt = produkt,
                leaveDetailsScreen = {navController.navigate(PhotoAppDestinations.EDITING_SELECTOR)},
            )
        }

        composable(PhotoAppDestinations.LOGIN_SCREEN_ROUTE) {
            Log.i("Dolan", "Odpalam PRODUKT_DETAILS_SCREEN_ROUTE w navGraph")
            LoginScreen(
                navController = navController
            )
        }

        composable(PhotoAppDestinations.SETTINGS_ROUTE) {
            Log.i("Dolan", "Odpalam SETTINGS_ROUTE w navGraph")
            SettingsScreen(
                onDelete = {navController.navigate(PhotoAppDestinations.LOGIN_SCREEN_ROUTE)},
                onBack = { navController.navigate(PhotoAppDestinations.FAKTURA_SCREEN_ROUTE) }
            )
        }
    }
}

