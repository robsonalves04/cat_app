package com.example.cat_app.ui_ux.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
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
    val breedList = viewModel.breedItems
    val isLoading = viewModel.isLoading.value
    val listState = rememberLazyListState()
    val breeds = viewModel.breedItems
    val coroutineScope = rememberCoroutineScope()


    // Primeira carga
    LaunchedEffect(Unit) {
        viewModel.fetchBreeds(context)
    }

    // Paginação ao fim do scroll
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .distinctUntilChanged()
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex == viewModel.breedItems.size - 1 && !viewModel.isLoading.value) {
                    viewModel.fetchBreeds(context, loadMore = true)
                }
            }
    }


    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text("🐾 Lista de Gatos") },
                navigationIcon = {
                    IconButton (onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            items(breeds) { breed ->
                val isFavorite = viewModel.isBreedFavorite(breed)
                BreedItemCard(
                    breed = breed,
                    viewModel = viewModel,
                    initialFavorite = isFavorite,
                    onFavoriteClick = {
                        viewModel.toggleFavorite(context, breed)
                    },
                    onClick = {
                        // Exemplo: você pode navegar ou exibir detalhes

                    }
                )
            }

            items(breedList) { breed ->
                val isFav = viewModel.isBreedFavorite(breed) // função que retorna se é favorito
                BreedItemCard(
                    initialFavorite = isFav,
                    onFavoriteClick = { fav ->
                        if (fav) {
                            viewModel.toggleFavorite(context,breed)
                        } else {
                            viewModel.toggleFavorite(context,breed)
                        }
                    },
                    onClick = { /* ação ao clicar no card */ },
                    breed = breed,
                    viewModel = viewModel
                )
            }

            if (viewModel.isLoading.value) {
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



