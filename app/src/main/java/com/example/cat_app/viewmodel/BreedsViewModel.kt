package com.example.cat_app.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cat_app.data.api.IBreedsService
import com.example.cat_app.data.models.BreedsImageModel
import com.example.cat_app.data.models.BreedsModel
import com.example.cat_app.data.models.FavoritesModel
import com.example.cat_app.data.models.FavoritesRespondeModel
import com.example.cat_app.network.networkConection
import com.example.cat_app.ui_ux.components.toats.toastSnackbar
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException


class BreedsViewModel(private val breedsService: IBreedsService) : ViewModel() {


    private var reconnectJob: Job? = null
    private var isTryingToReconnect = false

    private val _isLastPage = mutableStateOf(false)

    private var currentPage = 0
    private val pageSize = 10

    private val _breedItems = mutableStateListOf<BreedsModel>()
    val breedItems: List<BreedsModel> get() = _breedItems

    val isLoading = mutableStateOf(false)
    var isLastPage = false


    private val _favorites = mutableStateListOf<FavoritesRespondeModel>()
    val favorites: List<FavoritesRespondeModel> get() = _favorites


    val uniqueBreeds: List<BreedsModel>
        get() = _breedItems.distinctBy { it.referenceImageId }


    private val _searchResults = mutableStateListOf<BreedsModel>()
    val searchResults: List<BreedsModel> get() = _searchResults

    private val _isSearching = mutableStateOf(false)
    val isSearching: State<Boolean> get() = _isSearching


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
//                    response.body()?.let { _favorites.addAll(it)

                    favList.forEach { fav ->
                        val name = breedItems.find { it.referenceImageId == fav.imageId }?.name ?: "Desconhecido"
                        Log.d("FAVORITE_DEBUG", "✅ Favorito: $name (imageId = ${fav.imageId})")
                    }


                    Log.d("FAVORITES", "🐾 Favoritos carregados: ${_favorites.size}")

                } else {
                    toastSnackbar(context, "Erro ao carregar favoritos")
                }
            } catch (e: Exception) {
                Log.e("FAVORITES", "Erro ao buscar favoritos: ${e.message}")
            }
        }



    }


    fun addFavorite(context: Context, breed: BreedsModel) {
        viewModelScope.launch {
//            val alreadyExists = favorites.any { it.imageId == breed.referenceImageId }
//            if (alreadyExists) return@launch

            val fav = FavoritesModel(imageId = breed.referenceImageId ?: return@launch)
            val response = breedsService.addFavourite(fav)
            if (response.isSuccessful) {
                response.body()?.let {

                    if (_favorites.none { fav -> fav.imageId == it.imageId }) {
                        _favorites.add(it)
                    }


                    Log.d("FAVORITE", "✅ Adicionado aos favoritos: ${breed.name}")
                    toastSnackbar(
                        context,
                        "${breed.name} adicionado aos favoritos",
                        backgroundColor = android.graphics.Color.parseColor("#00FF00"),
                        textColor = android.graphics.Color.parseColor("#000000")
                    )
                }
            }
        }
    }

    fun removeFavorite(context: Context, breed: BreedsModel) {
        viewModelScope.launch {
            val fav = favorites.find { it.imageId == breed.referenceImageId } ?: return@launch
            val response = breedsService.removeFavourite(fav.id)
            if (response.isSuccessful) {
                _favorites.removeAll { it.id == fav.id }

                Log.d("FAVORITE", "❌ Removido dos favoritos: ${breed.name}")
                toastSnackbar(
                    context,
                    "${breed.name} removido dos favoritos",
                    backgroundColor = android.graphics.Color.parseColor("#FF0000")
                )
            }
        }
    }

    fun toggleFavorite(context: Context, breed: BreedsModel) {
        viewModelScope.launch {
            val imageId = breed.referenceImageId ?: return@launch
            val isFav = favorites.any { it.imageId == imageId }

            if (isFav) {
                val favId = favorites.find { it.imageId == imageId }?.id ?: return@launch
                val response = breedsService.removeFavourite(favId)
                if (response.isSuccessful) {
                    _favorites.removeAll { it.id == favId }
                    toastSnackbar(context, "${breed.name} removido dos favoritos",
                        backgroundColor = android.graphics.Color.parseColor("#FF0000"))
                    Log.d("FAVORITE", "❌ Removido dos favoritos: ${breed.name}")
                }
            } else {
                val response = breedsService.addFavourite(FavoritesModel(imageId))
                if (response.isSuccessful) {
                    response.body()?.let {
                        _favorites.add(it)
                        toastSnackbar(
                            context,
                            "${breed.name} adicionado aos favoritos",
                            backgroundColor = android.graphics.Color.parseColor("#00FF00"),
                            textColor = android.graphics.Color.parseColor("#000000"))
                        Log.d("FAVORITE", "✅ Adicionado aos favoritos: ${breed.name}")
                    }
                }
            }
        }
    }

    fun isBreedFavorite(breed: BreedsModel): Boolean {
        return favorites.any { it.imageId == breed.referenceImageId }
    }

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
                Log.e("SEARCH", "Erro: ${e.message}")
                toastSnackbar(context, "Erro na busca: ${e.message}")
            }
        }
    }

    fun clearSearch() {
        _isSearching.value = false
        _searchResults.clear()
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

    fun getBreedImageUrl(breed: BreedsModel): String {
        // Usa a URL direta se estiver disponível
        return breed.image?.url
            ?: breed.referenceImageId?.let { "https://cdn2.thecatapi.com/images/$it.jpg" }
            ?: "https://cdn2.thecatapi.com/images/default.jpg" // fallback
    }

}
