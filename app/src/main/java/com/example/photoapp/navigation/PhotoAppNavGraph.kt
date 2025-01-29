package com.example.photoapp.navigation


import android.util.Log
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.photoapp.database.DatabaseViewModel
import com.example.photoapp.ui.ExcelPacker.ExcelPacker
import com.example.photoapp.ui.acceptPhoto.AcceptPhoto
import com.example.photoapp.ui.cameraView.CameraView
import com.example.photoapp.ui.fakturaView.FakturaDetailsScreen
import com.example.photoapp.ui.paragonView.ParagonDetailsScreen
import com.example.photoapp.ui.FilterScreen.FilterScreen
import com.example.photoapp.ui.fakturaView.FakturaScreen
import com.example.photoapp.ui.home.HomeDrawer
import com.example.photoapp.ui.home.HomeScreen
import com.example.photoapp.ui.paragonView.ParagonScreen
import com.example.photoapp.ui.testingButtons.TestingButtons
import dagger.hilt.processor.internal.definecomponent.codegen._dagger_hilt_android_components_ViewModelComponent
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
    geminiKey: String
) {
    val navGraphViewModel: NavGraphViewModel = viewModel()

    var drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    // Observable states
    val photoUri by navGraphViewModel.photoUri.observeAsState()
    val photoBitmap by navGraphViewModel.photoBitmap.observeAsState()

    // For photo and acceptance
    val addingPhotoFor by navGraphViewModel.addingPhotoFor.observeAsState()

    // For details
    val paragonViewedNow by navGraphViewModel.paragonViewedNow.observeAsState()
    val fakturaViewedNow by navGraphViewModel.fakturaViewedNow.observeAsState()

    // For homeScreen
    val showFilteredParagons by navGraphViewModel.showFilteredParagons.observeAsState(false)
    val paragonFilteredList by navGraphViewModel.paragonFilteredList.observeAsState(emptyList())
    val currentlyShowing by navGraphViewModel.currentlyShowing.observeAsState("paragon")
    val showFilteredFaktury by navGraphViewModel.showFilteredFakturys.observeAsState(false)
    val fakturaFilteredList by navGraphViewModel.fakturaFilteredList.observeAsState(emptyList())

    // Example function called when photoUri changes
    fun onPhotoUriChanged() {
        Log.i("PhotoApp", "Photo URI changed:")
        navController.navigate(PhotoAppDestinations.ACCEPT_PHOTO_ROUTE)
        // Add your logic here, e.g., save to database, perform navigation, etc.
    }

    LaunchedEffect(photoUri) {
        photoUri?.let {
            onPhotoUriChanged() // Trigger your desired logic
        }}



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
                    navigateToParagonScreen = {
                        navController.navigate(PhotoAppDestinations.PARAGON_SCREEN_ROUTE)
                    },
                    navigateToFakturaScreen = {
                        navController.navigate(PhotoAppDestinations.FAKTURA_SCREEN_ROUTE)

                    },
                    navigateToExcelPacker = {
                        navController.navigate(PhotoAppDestinations.EXCEL_PACKER_ROUTE)
                    }
                )
            }
        }

        composable(PhotoAppDestinations.PARAGON_SCREEN_ROUTE) {
            Log.i("Dolan", "Odpalam PARAGON_DETAILS_SCREEN w navGraph")
            ParagonScreen(
                navController = navController,
                navigateToCameraView = { addingPhotoFor ->
                    navGraphViewModel.setAddingPhotoFor(addingPhotoFor)
                    navController.navigate(PhotoAppDestinations.MAKE_PHOTO_ROUTE)},
                navigateToParagonDetailsScreen = { paragon ->
                    navGraphViewModel.setParagonViewedNow(paragon)
                    navController.navigate(PhotoAppDestinations.PARAGON_DETAILS_SCREEN_ROUTE)
                },
                navigateToFiltersScreen = {
                    navController.navigate(PhotoAppDestinations.FILTERS_SCREEN_ROUTE)
                },
//                currentlyShowing = currentlyShowing,
                showFilteredParagons = showFilteredParagons,
                paragonFilteredList = paragonFilteredList,
            )
        }

        composable(PhotoAppDestinations.PARAGON_DETAILS_SCREEN_ROUTE) {
            Log.i("Dolan", "Odpalam PARAGON_DETAILS_SCREEN w navGraph")
            ParagonDetailsScreen(
                navController = navController,
                paragon = paragonViewedNow,
            )
        }

        composable(PhotoAppDestinations.FAKTURA_SCREEN_ROUTE) {
            Log.i("Dolan", "Odpalam PARAGON_DETAILS_SCREEN w navGraph")
            FakturaScreen(
                navController = navController,
                navigateToCameraView = { addingPhotoFor ->
                    navGraphViewModel.setAddingPhotoFor(addingPhotoFor)
                    navController.navigate(PhotoAppDestinations.MAKE_PHOTO_ROUTE)},
                navigateToFakturaDetailsScreen = { faktura ->
                    navGraphViewModel.setFakturaViewedNow(faktura)
                    navController.navigate(PhotoAppDestinations.FAKTURA_DETAILS_SCREEN_ROUTE)
                },
                navigateToFiltersScreen = {
                    navController.navigate(PhotoAppDestinations.FILTERS_SCREEN_ROUTE)
                },
//                currentlyShowing = currentlyShowing,
                showFilteredFaktury = showFilteredFaktury,
                fakturaFilteredList = fakturaFilteredList
            )
        }

        composable(PhotoAppDestinations.FAKTURA_DETAILS_SCREEN_ROUTE) {
            Log.i("Dolan", "Odpalam PARAGON_DETAILS_SCREEN w navGraph")
            FakturaDetailsScreen(
                navController = navController,
                faktura = fakturaViewedNow,
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
                addingPhotoFor = addingPhotoFor,
                contentDescription = null,
                backToCameraView = {navController.navigate(PhotoAppDestinations.MAKE_PHOTO_ROUTE)},
                backToHome = {navController.navigate(PhotoAppDestinations.HOME_ROUTE)},
                geminiKey = geminiKey,
            )
        }

        composable (PhotoAppDestinations.FILTERS_SCREEN_ROUTE) {
            Log.i("Dolan", "Odpalam FILTERS_SCREEN_ROUTE w navGraph")
            FilterScreen (
                onBackClick = {navController.navigate(PhotoAppDestinations.HOME_ROUTE)},
                onParagonsFiltersApplied = { currentFilter, showFilters, lista ->
                    navGraphViewModel.setFilters(showFilters, lista)
                    navController.navigate(PhotoAppDestinations.PARAGON_SCREEN_ROUTE)
                },
                onFakturyFiltersApplied = { currentFilter, showFilters, lista ->
                    navGraphViewModel.setCurrenltyShowing(currentFilter)
                    navGraphViewModel.setFakturyFilters(showFilters, lista)
                    navController.navigate(PhotoAppDestinations.FAKTURA_SCREEN_ROUTE)
                },
            )
        }

        composable (PhotoAppDestinations.EXCEL_PACKER_ROUTE) {
            Log.i("Dolan", "Odpalam EXCEL_PACKER_ROUTE w navGraph")
            ExcelPacker(
                navController = navController,
                navigateToParagonDetailsScreen = { paragon ->
                    navGraphViewModel.setParagonViewedNow(paragon)
                    navController.navigate(PhotoAppDestinations.PARAGON_DETAILS_SCREEN_ROUTE)
                },
                navigateToFiltersScreen = {
                    navController.navigate(PhotoAppDestinations.FILTERS_SCREEN_ROUTE)
                },
                showFilteredParagons = showFilteredParagons,
                paragonFilteredList = paragonFilteredList,
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

