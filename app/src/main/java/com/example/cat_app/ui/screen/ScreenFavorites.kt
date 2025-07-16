package com.example.cat_app.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.cat_app.data.models.BreedsModel
import com.example.cat_app.ui.components.utils.BreedDialog
import com.example.cat_app.ui.components.utils.BreedItemCard
import com.example.cat_app.viewmodel.BreedsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenFavorites(viewModel: BreedsViewModel, navigateBack: () -> Unit = {}) {
    //current context of the composable
    val context = LocalContext.current
    //state of the lazy list for scroll position
    val listState = rememberLazyListState()
    //current text entered in the search bar
    var searchQuery by remember { mutableStateOf("") }
    //list of favorite breeds from the ViewModel
    val favorites = remember { derivedStateOf { viewModel.favorites.distinctBy { it.imageId } } }
    //list of favorite breeds from the ViewModel
    val favorites_viewmodel = viewModel.favorites
    //controls visibility of the detail dialog
    var showDialog by remember { mutableStateOf(false) }
    //currently selected breed for detail view
    var selectedBreed by remember { mutableStateOf<BreedsModel?>(null) }

    //clears search data when the screen is disposed
    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearSearch()
        }
    }
    //loads initial data when the screen is first displayed
    LaunchedEffect(Unit) {
        viewModel.fetchFavorites(context)
        viewModel.fetchBreeds(context)
        viewModel.searchBreeds(context, searchQuery)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("💜 Favorites") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        when {
            favorites.value.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No favorites added yet 😿", style = MaterialTheme.typography.titleMedium)
                }
            }
            else -> {
                //list of found it
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                ) {
                    items(favorites.value) { fav ->
                        val breed = viewModel.breedItems.find { it.referenceImageId == fav.imageId }
                        val isFavorite =
                            favorites_viewmodel.any { it.imageId == breed?.referenceImageId }
                        if (breed != null) {
                            //card displaying cat breed data fetched from the API
                            BreedItemCard(
                                breed = breed,
                                onFavoriteClick = {
                                    if (isFavorite) {
                                        viewModel.removeFavorite(context, breed)
                                    } else {
                                        viewModel.addFavorite(context, breed)
                                    }
                                },
                                isFavorite = isFavorite,
                                viewModel = viewModel,
                                onClick = {
                                    selectedBreed = breed
                                    showDialog = true
                                }
                            )
                        }
                    }
                }
                //card with details about the cat
                if (showDialog && selectedBreed != null) {
                    val isFavorite =
                        favorites_viewmodel.any { it.imageId == selectedBreed!!.referenceImageId }
                    BreedDialog(
                        breed = selectedBreed!!,
                        viewModel = viewModel,
                        isFavorite = isFavorite,
                        onDismiss = { showDialog = false }
                    )

                }
            }
        }
    }
}