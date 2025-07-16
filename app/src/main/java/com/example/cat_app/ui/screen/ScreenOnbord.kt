package com.example.cat_app.ui.screen

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
import com.example.cat_app.ui.components.utils.OnboardingCarousel
import com.example.cat_app.ui.components.utils.SquareButton
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
    //current context of the composable
    val context = LocalContext.current
    //list of all breeds from the ViewModel, show til 10 items
    val breeds = viewModel.breedItems.take(10)
    //list of favorite breeds from the ViewModel
    val favorites = viewModel.favorites
    //state of the page remember for scroll position
    val pagerState = rememberPagerState()

    //loads initial data when the screen is first displayed
    LaunchedEffect(Unit) {
        if (favorites.isEmpty()) viewModel.fetchFavorites(context)
        if (breeds.isEmpty()) viewModel.fetchBreeds(context)
    }
    //automatic carousel effect
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

            //carousel at the top
            OnboardingCarousel(breeds = breeds, pagerState = pagerState, viewModel = viewModel)

            Spacer(modifier = Modifier.height(16.dp))
            //bottom of the initial screen
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SquareButton("All Cats \uD83D\uDC3E", onNavigateToBreeds)
                    SquareButton("Favorites 💜", onNavigateToFavorites)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SquareButton("About 🔍", onNavigateToAbout)
                    SquareButton("Help ⚙️", onNavigateToHelp)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}