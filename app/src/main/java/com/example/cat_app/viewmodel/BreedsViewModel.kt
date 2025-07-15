package com.example.cat_app.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cat_app.data.api.IBreedsService
import com.example.cat_app.data.models.BreedDetailsModel
import com.example.cat_app.data.models.BreedsImageModel
import com.example.cat_app.data.models.BreedsModel
import com.example.cat_app.data.models.FavoritesModel
import com.example.cat_app.data.models.FavoritesRespondeModel
import com.example.cat_app.network.networkConection
import com.example.cat_app.ui_ux.components.toats.toastSnackbar
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException


class BreedsViewModel(private val breedsService: IBreedsService) : ViewModel() {

    private var reconnectJob: Job? = null
    private var isTryingToReconnect = false

    private var currentPage = 0
    private val pageSize = 10

    private val _breedItems = mutableStateListOf<BreedsModel>()
    val breedItems: List<BreedsModel> get() = _breedItems

    val isLoading = mutableStateOf(false)
    var isLastPage = false

    private val _favorites = mutableStateListOf<FavoritesRespondeModel>()
    val favorites: List<FavoritesRespondeModel> get() = _favorites

    private val _searchResults = mutableStateListOf<BreedsModel>()
    val searchResults: List<BreedsModel> get() = _searchResults

    private val _isSearching = mutableStateOf(false)
    val isSearching: State<Boolean> get() = _isSearching

    var breedDetails = mutableStateOf<BreedDetailsModel?>(null)

    val isLoadingDetails = mutableStateOf(false)


    // Busca as raças da API
    fun fetchBreeds(context: Context, loadMore: Boolean = false) {
        if (isLoading.value || isLastPage) return

        viewModelScope.launch {
            try {
                isLoading.value = true

                val response = breedsService.getBreedsList(limit = pageSize, page = currentPage)
                if (response.isSuccessful) {
                    val newItems = response.body().orEmpty()
                    Log.d("PAGINACAO", "🔄 Carregando página $currentPage...")
                    if (newItems.isNotEmpty()) {
                        _breedItems.addAll(newItems)
                        currentPage++
                    } else {
                        Log.d("PAGINACAO", "🚫 Nenhum item novo. Última página atingida.")
                        isLastPage = true
                    }
                } else {
                    toastSnackbar(context, "Erro HTTP: ${response.code()}")
                }

            } catch (e: IOException) {
                tentarRecarregarPeriodicamente(context)
                toastSnackbar(context, "Erro de conexão. Tentando novamente...")
            } catch (e: Exception) {
                toastSnackbar(context, "Erro inesperado: ${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }

    // Busca os favoritos da API
    fun fetchFavorites(context: Context) {
        viewModelScope.launch {
            try {
                val response = breedsService.getFavourites()
                if (response.isSuccessful) {
                    val favList = response.body().orEmpty()
                    _favorites.clear()
                    _favorites.addAll(response.body().orEmpty())

                    favList.forEach { fav ->
                        val name = breedItems.find { it.referenceImageId == fav.imageId }?.name
                            ?: "Desconhecido"
                        Log.d("FAVORITE_DEBUG", "✅ Favorito: $name (imageId = ${fav.imageId})")
                    }
                    Log.d("FAVORITES", "🐾 Favoritos carregados: ${_favorites.size}")
                } else {
                    toastSnackbar(context, "Erro ao carregar favoritos")
                }
            } catch (e: Exception) {
                Log.e("FAVORITES", "Erro ao buscar favoritos }")
            } catch (e: IOException) {
                tentarRecarregarPeriodicamente(context)
                toastSnackbar(context, "Erro de conexão. Tentando novamente...")
            } finally {
                isLoading.value = false
            }
        }
    }

    //faz a adição na lista de favoritos
    fun addFavorite(context: Context, breed: BreedsModel) {
        viewModelScope.launch {
            try {
                val fav = FavoritesModel(imageId = breed.referenceImageId ?: return@launch)
                val response = breedsService.addFavourite(fav)
                if (response.isSuccessful) {
                    Log.d("FAVORITE", "✅ Adicionado aos favoritos: ${breed.name}")
                    fetchFavorites(context)
                    toastSnackbar(
                        context,
                        "${breed.name} adicionado aos favoritos",
                        backgroundColor = android.graphics.Color.parseColor("#00FF00"),
                        textColor = android.graphics.Color.parseColor("#000000")
                    )
                } else {
                    Log.e("FAVORITE", "Erro ao adicionar favorito: ${response.code()}")
                }
            } catch (e: IOException) {
                Log.e("FAVORITE", "Erro de conexão: ${e.message}")
                toastSnackbar(
                    context,
                    "Sem conexão com a internet. Verifique sua rede.",
                    backgroundColor = android.graphics.Color.parseColor("#FF0000")
                )
            } catch (e: Exception) {
                Log.e("FAVORITE", "Erro inesperado: ${e.message}")
                toastSnackbar(context, "Erro ao remover favorito.")
            }
        }
    }

    // faz a remoção do item favoritado
    fun removeFavorite(context: Context, breed: BreedsModel) {
        viewModelScope.launch {
            try {
                val fav = favorites.find { it.imageId == breed.referenceImageId } ?: return@launch
                val response = breedsService.removeFavourite(fav.id)
                if (response.isSuccessful) {
                    fetchFavorites(context)

                    Log.d("FAVORITE", "❌ Removido dos favoritos: ${breed.name}")
                    toastSnackbar(
                        context,
                        "${breed.name} removido dos favoritos",
                        backgroundColor = android.graphics.Color.parseColor("#FF0000")
                    )
                }
            } catch (e: IOException) {
                Log.e("FAVORITE", "Erro de conexão: ${e.message}")
                toastSnackbar(
                    context,
                    "Sem conexão com a internet. Verifique sua rede.",
                    backgroundColor = android.graphics.Color.parseColor("#FF0000")
                )
            } catch (e: Exception) {
                Log.e("FAVORITE", "Erro inesperado: ${e.message}")
                toastSnackbar(context, "Erro ao remover favorito.")
            }
        }
    }

    // controla a pesquisa por Query
    fun searchBreeds(context: Context, query: String) {
        viewModelScope.launch {
            try {
                _isSearching.value = true
                val response = breedsService.searchBreeds(query)
                if (response.isSuccessful) {
                    _searchResults.clear()
                    _searchResults.addAll(response.body() ?: emptyList())
                    Log.d("SEARCH", "🔍 Resultados: ${_searchResults.size}")
                } else {
                    toastSnackbar(context, "Erro ao buscar: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("SEARCH", "Erro ao buscar lista ${e.message}}")
                toastSnackbar(context, "Erro ao buscar lista")
            }
        }
    }


    // Repetir requisição se conexão voltar
    fun tentarRecarregarPeriodicamente(context: Context) {
        if (isTryingToReconnect) return

        isTryingToReconnect = true

        reconnectJob = viewModelScope.launch {
            while (isActive) {
                delay(5000)

                if (networkConection(context)) {
                    toastSnackbar(context, "Conexão restabelecida! Recarregando raças...")
                    fetchBreeds(context)
                    isTryingToReconnect = false
                    break
                }
            }
        }
    }

    //obtem a url de imagem e apresenta dentro do AsyncImage
    fun getBreedImageUrl(breed: BreedsModel): String {
        // Usa a URL direta se estiver disponível
        return breed.image?.url
            ?: breed.referenceImageId?.let { "https://cdn2.thecatapi.com/images/$it.jpg" }
            ?: "https://cdn2.thecatapi.com/images/default.jpg" // fallback
    }

    //limpa a barra de pesquisa
    fun clearSearch() {
        _isSearching.value = false
        _searchResults.clear()
    }

    fun clearBreedDetails() {
        breedDetails.value = null
    }

}