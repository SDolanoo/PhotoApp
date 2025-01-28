package com.example.photoapp.navigation

import android.util.Log
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

/**
 * Destinations used in the [PhotoApp].
 */
object PhotoAppDestinations {
    const val HOME_ROUTE = "home"
    const val MAKE_PHOTO_ROUTE = "makePhoto"
    const val ACCEPT_PHOTO_ROUTE = "acceptPhoto"
    const val TESTING_BUTTONS_ROUTE = "testingButtons"
    const val PARAGON_SCREEN_ROUTE = "paragonScreen"
    const val PARAGON_DETAILS_SCREEN_ROUTE = "paragonDetailsScreen"
    const val FAKTURA_SCREEN_ROUTE = "fakturaScreen"
    const val FAKTURA_DETAILS_SCREEN_ROUTE = "fakturaDetailsScreen"
    const val FILTERS_SCREEN_ROUTE = "filtersScreen"
    const val EXCEL_PACKER_ROUTE = "excelPacker"
}

/**
 * Models the navigation actions in the app.
 */
class PhotoAppNavigationActions(navController: NavHostController) {
    val navigateToHome: () -> Unit = {
        navController.navigate(PhotoAppDestinations.HOME_ROUTE) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }
    }
    val navigateToMakePhoto: () -> Unit = {
        navController.navigate(PhotoAppDestinations.MAKE_PHOTO_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = false
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToAcceptPhoto: () -> Unit = {
        navController.navigate(PhotoAppDestinations.ACCEPT_PHOTO_ROUTE) {
            Log.i("Dolan", "Openinig AcceptPhoto in Navigation")
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = false
            Log.i("Dolan", "Left AcceptPhoto in Navigation")
        }
    }

    val navigateToFilters: () -> Unit = {
        navController.navigate(PhotoAppDestinations.FILTERS_SCREEN_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToTestingButtons: () -> Unit = {
        navController.navigate(PhotoAppDestinations.TESTING_BUTTONS_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }


}