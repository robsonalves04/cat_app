package com.example.cat_app.ui.screen

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.cat_app.data.models.BreedsModel
import com.example.cat_app.ui.components.utils.BreedDialog
import com.example.cat_app.ui.components.utils.BreedItemCard
import com.example.cat_app.viewmodel.BreedsViewModel
import kotlinx.coroutines.flow.distinctUntilChanged


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScreenBreeds(
    viewModel: BreedsViewModel,
    navigateBack: () -> Unit = {},
) {
    //current context of the composable
    val context = LocalContext.current
    //list of all breeds from the ViewModel
    val breeds = viewModel.breedItems
    //list of favorite breeds from the ViewModel
    val favorites = viewModel.favorites
    //state of the lazy list for scroll position
    val listState = rememberLazyListState()
    //current text entered in the search bar
    var searchQuery by remember { mutableStateOf("") }
    //flag indicating if the user is performing a search
    val isSearching by viewModel.isSearching
    //filtered list of breeds matching the search query
    val searchResults = viewModel.searchResults
    //full list of breeds (same as breeds variable)
    val allBreeds = viewModel.breedItems
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
        if (breeds.isEmpty()) viewModel.fetchBreeds(context)

    }

    //handles loading more items as the user scrolls (pagination)
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .distinctUntilChanged()
            .collect { lastVisibleIndex ->
                if (!isSearching && lastVisibleIndex == allBreeds.size - 1 && !viewModel.isLoading.value) {
                    viewModel.fetchBreeds(context, loadMore = true)
                }
            }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🐾 List of Cats") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }

    ) { paddingValues ->
        val breedsToShow = if (isSearching) searchResults else allBreeds
        Column(modifier = Modifier.padding(paddingValues)) {
            //search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    if (searchQuery.isNotBlank()) {
                        viewModel.searchBreeds(context, searchQuery)
                    } else {
                        viewModel.clearSearch()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                label = { Text("Search breed...") },
                singleLine = true,
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            searchQuery = ""
                            viewModel.clearSearch()
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "clean search")
                        }
                    }
                },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
            )
            //loading if not found it
            if (isSearching && searchQuery.isNotBlank() && searchResults.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No breeds found 😿",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Gray
                    )
                }
            }
            //list of found it
            LazyColumn(state = listState) {
                items(breedsToShow,
                    key = { it.referenceImageId ?: it.id ?: it.hashCode().toString() }
                ) { breed ->
                    val isFavorite = favorites.any { it.imageId == breed.referenceImageId }
                    //card displaying cat breed data fetched from the API
                    BreedItemCard(
                        breed = breed,
                        viewModel = viewModel,
                        isFavorite = isFavorite,
                        onFavoriteClick = {
                            if (isFavorite) {
                                viewModel.removeFavorite(context, breed)
                            } else {
                                viewModel.addFavorite(context, breed)
                            }
                        },
                        onClick = {
                            selectedBreed = breed
                            showDialog = true
                        }
                    )
                }
                //loading of waiting
                if (!isSearching && viewModel.isLoading.value && showDialog) {
                    item {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                                .wrapContentWidth(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
            //card with details about the cat
            if (showDialog && selectedBreed != null) {
                val isFavorite = favorites.any { it.imageId == selectedBreed!!.referenceImageId }
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