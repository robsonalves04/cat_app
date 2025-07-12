package com.example.cat_app.ui_ux.components.navigate

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cat_app.ui_ux.screen.ScreenBreeds
import com.example.cat_app.ui_ux.screen.ScreenOnboard
import com.example.cat_app.ui_ux.screen.ScreenSplash


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

//    val dataViewModel: ChanelViewModel = koinViewModel()

    NavHost(navController = navController, startDestination = "splash") {
        //tela de splash
        composable("splash") {
            ScreenSplash(
                navigateToNextScreen = {
                    navController.navigate("onboard") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }
        //tela de onboard
        composable("onboard") {
            ScreenOnboard { selectedOption ->
                when (selectedOption.title) {
                    "Raças" -> navController.navigate("list")
                    "Favoritos" -> navController.navigate("favorites")
                    "Sobre" -> navController.navigate("about")
                    else -> {} // ou mostrar toast
                }
            }
        }

        //tela de lista de raças
        composable("list") {
            ScreenBreeds(
                navigateToNextScreen = {
                    navController.navigate("onboard")
                }
            )
        }
    }
}