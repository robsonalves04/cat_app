package com.example.cat_app.ui.components.navigate

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cat_app.ui.components.toats.toastSnackbar
import com.example.cat_app.ui.screen.ScreenBreeds
import com.example.cat_app.ui.screen.ScreenFavorites
import com.example.cat_app.ui.screen.ScreenOnboard
import com.example.cat_app.ui.screen.ScreenSplash
import com.example.cat_app.viewmodel.BreedsViewModel
import org.koin.androidx.compose.koinViewModel


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val dataViewModel: BreedsViewModel = koinViewModel()

    NavHost(navController = navController, startDestination = "splash") {
        //initial splash screen displayed when the app launches
        composable("splash") {
            ScreenSplash(
                navigateToNextScreen = {
                    navController.navigate("onboard") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }
        //screen shown during the onboarding process
        composable("onboard") {
            ScreenOnboard(viewModel = dataViewModel,
                onNavigateToFavorites = { navController.navigate("favorites") },
                onNavigateToBreeds = { navController.navigate("list") },
                onNavigateToAbout = {
                    toastSnackbar(
                        context,
                        "This function is currently unavailable",
                        backgroundColor = android.graphics.Color.parseColor("#FF0000")
                    )
                },
                onNavigateToHelp = {
                    toastSnackbar(
                        context,
                        "This function is currently unavailable",
                        backgroundColor = android.graphics.Color.parseColor("#FF0000")
                    )
                })
        }
        //screen displaying the list of cat breeds
        composable("list") {
            ScreenBreeds(viewModel = dataViewModel,
                navigateBack = { navController.popBackStack() })
        }
        //screen displaying the list of favorites cat breeds
        composable("favorites") {
            ScreenFavorites(
                viewModel = dataViewModel,
                navigateBack = { navController.popBackStack() })
        }
    }
}