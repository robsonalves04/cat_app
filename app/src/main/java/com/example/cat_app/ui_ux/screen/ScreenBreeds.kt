package com.example.cat_app.ui_ux.screen

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.cat_app.ui_ux.components.form.BreedItemCard
import com.example.cat_app.viewmodel.BreedsViewModel


//@RequiresApi(Build.VERSION_CODES.O)
//@Composable
//fun ScreenBreeds(
//    viewModel: BreedsViewModel = remember { BreedsViewModel(BreedsServiceImpl(CatApiClient.apiService)) },
//    navigateToNextScreen: () -> Unit = {}
//) {
//    val breeds = viewModel.breedItems
//    val context = LocalContext.current
//
//    LaunchedEffect(Unit) {
//        viewModel.fetchBreeds(context = context)
//    }
//
//    if (breeds.isEmpty()) {
//        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//            CircularProgressIndicator()
//        }
//    } else {
//        LazyColumn (
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(16.dp)
//        ) {
//            items(breeds) { breed ->
//                BreedItemCard(breed = breed)
//            }
//        }
//    }
//}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScreenBreeds(
    viewModel: BreedsViewModel,
    navigateToNextScreen: () -> Unit = {}
) {
    val context = LocalContext.current
    val breedList = viewModel.breedItems
    val isLoading = viewModel.isLoading.value

    // Dispara a requisição apenas uma vez
    LaunchedEffect(Unit) {
        if (breedList.isEmpty()) {
            viewModel.fetchBreeds(context)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(breedList) { breed ->
                    BreedItemCard(breed)
                }
            }
        }
    }
}

