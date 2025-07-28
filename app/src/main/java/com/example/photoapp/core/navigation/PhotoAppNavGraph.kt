package com.example.photoapp.core.navigation


import android.util.Log
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.photoapp.features.faktura.ui.details.FakturaDetailsScreen
import com.example.photoapp.features.faktura.ui.details.ProduktFakturaZProduktem
import com.example.photoapp.features.faktura.ui.screen.FakturaScreen
import com.example.photoapp.ui.ExcelPacker.ExcelPacker
import com.example.photoapp.ui.FilterScreen.FilterScreenContent
import com.example.photoapp.ui.acceptFaktura.AcceptFakturaScreen
import com.example.photoapp.ui.acceptPhoto.AcceptPhoto
import com.example.photoapp.ui.cameraView.CameraView
//import com.example.photoapp.ui.FilterScreen.FilterScreen
import com.example.photoapp.ui.home.HomeDrawer
import com.example.photoapp.ui.home.HomeScreen
import com.example.photoapp.ui.testingButtons.TestingButtons
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.ExecutorService

const val POST_ID = "postId"

@Composable
fun PhotoAppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = PhotoAppDestinations.HOME_ROUTE,
    /* CameraView Variables */
    outputDirectory: File,
    executor: ExecutorService,
    geminiKey: String,
    navGraphViewModel: NavGraphViewModel = hiltViewModel()
) {
    var drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    // Observable states
    val photoUri by navGraphViewModel.photoUri.observeAsState()
    val photoBitmap by navGraphViewModel.photoBitmap.observeAsState()

    val faktura by navGraphViewModel.faktura.collectAsState()
    val sprzedawca by navGraphViewModel.sprzedawca.collectAsState()
    val odbiorca by navGraphViewModel.odbiorca.collectAsState()
    val produkty by navGraphViewModel.produkty.collectAsState()

    // Example function called when photoUri changes
    fun onPhotoUriChanged() {
        Log.i("PhotoApp", "Photo URI changed:")
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
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(
            route = PhotoAppDestinations.HOME_ROUTE,
        ) { Log.i("Dolan", "Odpalam HOME w navGraph")
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    HomeDrawer(
                        navigateToHome = { navController.navigate(PhotoAppDestinations.HOME_ROUTE) },
                        navigateToAcceptPhoto = {navController.navigate(PhotoAppDestinations.MAKE_PHOTO_ROUTE)},
                        navigateToTestingButtons = {navController.navigate(PhotoAppDestinations.TESTING_BUTTONS_ROUTE)},
                        closeDrawer = { coroutineScope.launch {drawerState = drawerState.apply { close() }}},

                    )
                },
                gesturesEnabled = true
            ) {
                HomeScreen (
                    openDrawer = { coroutineScope.launch {drawerState = drawerState.apply { open() }}},
                    navigateToCameraView = { addingPhotoFor ->
                        navGraphViewModel.setAddingPhotoFor(addingPhotoFor)
                        navController.navigate(PhotoAppDestinations.MAKE_PHOTO_ROUTE)},
                    navigateToFakturaScreen = {
                        navController.navigate(PhotoAppDestinations.FAKTURA_SCREEN_ROUTE)

                    },
                    navigateToExcelPacker = {
                        navController.navigate(PhotoAppDestinations.EXCEL_PACKER_ROUTE)
                    }
                )
            }
        }

        composable(PhotoAppDestinations.FAKTURA_SCREEN_ROUTE) {
            Log.i("Dolan", "Odpalam FAKTURA_SCREEN w navGraph")
            FakturaScreen(
                navController = navController,
                navigateToCameraView = { addingPhotoFor ->
                    navGraphViewModel.setAddingPhotoFor(addingPhotoFor)
                    navController.navigate(PhotoAppDestinations.MAKE_PHOTO_ROUTE)},
                navigateToFakturaDetailsScreen = { faktura ->
                    navGraphViewModel.setFakturaViewedNow(faktura)
                    navController.navigate(PhotoAppDestinations.FAKTURA_DETAILS_SCREEN_ROUTE)
                }
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
                backToHomeScreen = { navController.navigate(PhotoAppDestinations.HOME_ROUTE) },
            )
        }

        composable (PhotoAppDestinations.ACCEPT_PHOTO_ROUTE) {
            Log.i("Dolan", "Odpalam ACCEPT_PHOTO w navGraph")
            AcceptPhoto(
                photoUri = photoUri,
                bitmapPhoto = photoBitmap,
                contentDescription = null,
                backToCameraView = {navController.navigate(PhotoAppDestinations.MAKE_PHOTO_ROUTE)},
                goToAcceptFakturaScreen = { faktura, sprzedawca, odbiorca, produktFakturaZProduktem ->
                    navGraphViewModel.setFaktura(faktura)
                    navGraphViewModel.setSprzedawca(sprzedawca)
                    navGraphViewModel.setOdbiorca(odbiorca)
                    navGraphViewModel.setProdukty(produkty)
                    navController.navigate(PhotoAppDestinations.ACCEPT_FAKTURA_ROUTE)
                                          },
                backToHome = {navController.navigate(PhotoAppDestinations.HOME_ROUTE)},
                geminiKey = geminiKey
            )
        }


        composable (PhotoAppDestinations.ACCEPT_FAKTURA_ROUTE) {
            Log.i("Dolan", "Odpalam ACCEPT_FAKTURA w navGraph")
            AcceptFakturaScreen(
                faktura = faktura,
                sprzedawca = sprzedawca,
                odbiorca = odbiorca,
                produkty = produkty,
                onConfirm = {},
                onCancel = {},
            )
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
                backToHome = {navController.navigate(PhotoAppDestinations.HOME_ROUTE)},
            )
        }
    }
}

