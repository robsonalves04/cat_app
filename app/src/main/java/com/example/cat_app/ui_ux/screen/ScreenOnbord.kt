package com.example.cat_app.ui_ux.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.cat_app.ui_ux.components.form.OnboardingCarousel
import com.example.cat_app.ui_ux.components.form.SquareButton
import com.example.cat_app.viewmodel.BreedsViewModel
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.delay


@RequiresApi(Build.VERSION_CODES.O)
@Composable

fun ScreenOnboard(
    viewModel: BreedsViewModel,
    onNavigateToBreeds: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToHelp: () -> Unit,
) {
    val context = LocalContext.current
    val breeds = viewModel.breedItems.take(10) // Exibe até 10 gatos no carrossel
    val favorites = viewModel.favorites
    val pagerState = rememberPagerState()


    // Carrega dados iniciais
    LaunchedEffect(Unit) {
        if (favorites.isEmpty()) viewModel.fetchFavorites(context)
        if (breeds.isEmpty()) viewModel.fetchBreeds(context)
    }
    //efeito de carrousel automatico
    LaunchedEffect(breeds.size) {
        if (breeds.isNotEmpty()) {
            while (true) {
                delay(3000)
                val nextPage = (pagerState.currentPage + 1) % breeds.size
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor =  MaterialTheme.colorScheme.background
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // 🐾 Carrossel no topo
            OnboardingCarousel(breeds = breeds, pagerState = pagerState)

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SquareButton("Todos \uD83D\uDC3E", onNavigateToBreeds)
                    SquareButton("Favoritos 💜", onNavigateToFavorites)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SquareButton("Sobre 🔍", onNavigateToAbout)
                    SquareButton("Ajuda ⚙️", onNavigateToHelp)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}