package com.example.cat_app.ui_ux.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.cat_app.ui_ux.components.form.BreedItemCard
import com.example.cat_app.viewmodel.BreedsViewModel
import kotlinx.coroutines.flow.distinctUntilChanged


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScreenBreeds(
    viewModel: BreedsViewModel,
    navigateBack: () -> Unit = {},
    navigateToNextScreen: () -> Unit = {}
) {
    val context = LocalContext.current
    val breeds = viewModel.breedItems
    val favorites = viewModel.favorites
    val listState = rememberLazyListState()
    var searchQuery by remember { mutableStateOf("") }

    val isSearching by viewModel.isSearching
    val searchResults = viewModel.searchResults
    val allBreeds = viewModel.breedItems

    // limpar dados apos a busca
    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearSearch()
        }
    }

    // Carrega dados iniciais
    LaunchedEffect(Unit) {
        if (favorites.isEmpty()) viewModel.fetchFavorites(context)
        if (breeds.isEmpty()) viewModel.fetchBreeds(context)
    }

    // Paginação
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
                title = { Text("🐾 Lista de Gatos") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }

    ) { paddingValues ->
        val breedsToShow = if (isSearching) searchResults else allBreeds
        Column(modifier = Modifier.padding(paddingValues)) {
            // Barra de pesquisa
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
                label = { Text("Buscar raça...") },
                singleLine = true,
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            searchQuery = ""
                            viewModel.clearSearch()
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Limpar busca")
                        }
                    }
                },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
            )

            LazyColumn(state = listState) {
                items(breedsToShow) { breed ->
                    // Card de gatos (retorno da API)
                    BreedItemCard(
                        breed = breed,
                        viewModel = viewModel,
                        onClick = {
                            navigateToNextScreen()
                        }
                    )
                }
                // Loading de espera
                if (!isSearching && viewModel.isLoading.value) {
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
        }
    }
}