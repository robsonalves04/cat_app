package com.example.cat_app.ui_ux.screen

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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.cat_app.ui_ux.components.form.BreedItemCard
import com.example.cat_app.viewmodel.BreedsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenFavorites(viewModel: BreedsViewModel, navigateBack: () -> Unit = {}) {
    val context = LocalContext.current
    val favorites = viewModel.favorites
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.fetchFavorites(context)
        viewModel.fetchBreeds(context)
    }

    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text("💜 Favoritos")  },
                navigationIcon = {
                    IconButton (onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn (
            state = listState,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            items(favorites) { fav ->
                val breed = viewModel.breedItems.find { it.referenceImageId == fav.imageId }
                if (breed != null) {
                    BreedItemCard(
                        breed = breed,
                        viewModel = viewModel,
                        initialFavorite = true,
                        onFavoriteClick = {
                            viewModel.toggleFavorite(context, breed)
                        },
                        onClick = { /* Você pode abrir detalhes aqui também */ }
                    )
                }
            }
        }
    }
}
