package com.example.cat_app.ui_ux.components.navigate

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cat_app.ui_ux.screen.ScreenBreeds
import com.example.cat_app.ui_ux.screen.ScreenFavorites
import com.example.cat_app.ui_ux.screen.ScreenOnboard
import com.example.cat_app.ui_ux.screen.ScreenSplash
import com.example.cat_app.viewmodel.BreedsViewModel
import org.koin.androidx.compose.koinViewModel


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val dataViewModel: BreedsViewModel = koinViewModel()

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
                selectedOption.route?.let { route ->
                    navController.navigate(route)
                } ?: run {
                    Toast.makeText(context, "Opção ainda não disponível", Toast.LENGTH_SHORT).show()
                }
            }
        }
        //tela de lista de raças
        composable("list") {
            ScreenBreeds(viewModel = dataViewModel,
                navigateBack = { navController.popBackStack() }) {

            }
        }
        //tela de favoritos
        composable("favorites") {
            ScreenFavorites(
                viewModel = dataViewModel,
                navigateBack = { navController.popBackStack() })
        }

    }
}