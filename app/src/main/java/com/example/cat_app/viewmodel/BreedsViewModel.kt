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
import com.example.cat_app.data.models.BreedsModel
import com.example.cat_app.data.models.FavoritesModel
import com.example.cat_app.data.models.FavoritesRespondeModel
import com.example.cat_app.network.networkConection
import com.example.cat_app.ui.components.toats.toastSnackbar
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException


class BreedsViewModel(private val breedsService: IBreedsService) : ViewModel() {

    //job used to manage automatic reconnection attempts
    private var reconnectJob: Job? = null

    //flag to indicate if a reconnection attempt is in progress
    private var isTryingToReconnect = false

    //current page index for paginated data loading
    private var currentPage = 0

    //number of items to load per page
    private val pageSize = 10

    //internal mutable list of breed items loaded from API
    private val _breedItems = mutableStateListOf<BreedsModel>()

    //public immutable list of breed items for UI consumption
    val breedItems: List<BreedsModel> get() = _breedItems

    //flag representing the loading state (true when data is being fetched)
    val isLoading = mutableStateOf(false)

    //flag indicating whether the last page of data has been reached
    var isLastPage = false

    //internal mutable list of favorite items loaded from API
    private val _favorites = mutableStateListOf<FavoritesRespondeModel>()

    //public immutable list of favorite items for UI consumption
    val favorites: List<FavoritesRespondeModel> get() = _favorites

    //internal mutable list holding search results based on query
    private val _searchResults = mutableStateListOf<BreedsModel>()

    //public immutable list of search results for UI consumption
    val searchResults: List<BreedsModel> get() = _searchResults

    //mutable state indicating if the app is currently performing a search
    private val _isSearching = mutableStateOf(false)

    //public read-only state for observing the searching status
    val isSearching: State<Boolean> get() = _isSearching


    //fetches breeds from the API
    fun fetchBreeds(context: Context, loadMore: Boolean = false) {
        if (isLoading.value || isLastPage) return

        viewModelScope.launch {
            try {
                isLoading.value = true

                val response = breedsService.getBreedsList(limit = pageSize, page = currentPage)
                if (response.isSuccessful) {
                    val newItems = response.body().orEmpty()
                    Log.d("Pagination", " Loading page $currentPage...")
                    if (newItems.isNotEmpty()) {
                        _breedItems.addAll(newItems)
                        currentPage++
                    } else {
                        Log.d("Pagination", "No new items. Last page reached.")
                        isLastPage = true
                    }
                } else {
                    toastSnackbar(context, "Error HTTP: ${response.code()}")
                }
            } catch (e: IOException) {
                tryReloadPeriodically(context)
                toastSnackbar(context, "Connection error. Trying again...")
            } catch (e: Exception) {
                toastSnackbar(context, "Unexpected error: ${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }

    //fetches favorites from the API
    fun fetchFavorites(context: Context) {
        viewModelScope.launch {
            try {
                val response = breedsService.getFavorites()
                if (response.isSuccessful) {
                    _favorites.clear()
                    _favorites.addAll(response.body().orEmpty())
                } else {
                    toastSnackbar(context, "Failed to load favorites")
                }
            } catch (e: Exception) {

            } catch (e: IOException) {
                tryReloadPeriodically(context)
                toastSnackbar(context, "Connection error. Trying again...")
            } finally {
                isLoading.value = false
            }
        }
    }

    //adds item to the favorites list
    fun addFavorite(context: Context, breed: BreedsModel) {
        viewModelScope.launch {
            try {
                val fav = FavoritesModel(imageId = breed.referenceImageId ?: return@launch)
                val response = breedsService.addFavourite(fav)
                if (response.isSuccessful) {
                    fetchFavorites(context)
                    toastSnackbar(
                        context,
                        "${breed.name} added to favorites",
                        backgroundColor = android.graphics.Color.parseColor("#00FF00"),
                        textColor = android.graphics.Color.parseColor("#000000")
                    )
                }
            } catch (e: IOException) {
                toastSnackbar(
                    context,
                    "No internet connection. Please check your network.",
                    backgroundColor = android.graphics.Color.parseColor("#FF0000")
                )
            } catch (e: Exception) {
                toastSnackbar(context, "Failed to remove favorite")
            }
        }
    }

    //remove item to the favorites list
    fun removeFavorite(context: Context, breed: BreedsModel) {
        viewModelScope.launch {
            val fav = favorites.find { it.imageId == breed.referenceImageId } ?: return@launch
            _favorites.remove(fav)

            try {
                val response = breedsService.removeFavourite(fav.id)
                if (response.isSuccessful) {
                    toastSnackbar(
                        context,
                        "${breed.name} removed from favorites",
                        backgroundColor = android.graphics.Color.parseColor("#FF0000")
                    )
                } else {
                    _favorites.add(fav)
                    toastSnackbar(context, "Failed to remove favorite, please try again."
                    )
                }
            } catch (e: IOException) {
                _favorites.add(fav)
                toastSnackbar(
                    context,
                    "No internet connection. Please check your network.",
                    backgroundColor = android.graphics.Color.parseColor("#FF0000")
                )
            } catch (e: Exception) {
                _favorites.add(fav)
                toastSnackbar(context, "Failed to remove favorite.")
            }
        }
    }

    //manages search functionality by query
    fun searchBreeds(context: Context, query: String) {
        viewModelScope.launch {
            try {
                _isSearching.value = true
                val response = breedsService.searchBreeds(query)
                if (response.isSuccessful) {
                    _searchResults.clear()
                    _searchResults.addAll(response.body() ?: emptyList())
                } else {
                    toastSnackbar(context, "Error fetching: ${response.code()}")
                }
            } catch (e: Exception) {
                toastSnackbar(context, "Error fetching a list")
            }
        }
    }

    //retry request when connection is restored
    fun tryReloadPeriodically(context: Context) {
        if (isTryingToReconnect) return
        isTryingToReconnect = true
        reconnectJob = viewModelScope.launch {
            while (isActive) {
                delay(5000)

                if (networkConection(context)) {
                    toastSnackbar(context, "Connection restored! Reloading breeds...")
                    fetchBreeds(context)
                    isTryingToReconnect = false
                    break
                }
            }
        }
    }

    //gets the image URL and displays it inside AsyncImage
    fun getBreedImageUrl(breed: BreedsModel): String {
        return breed.image?.url
            ?: breed.referenceImageId?.let { "https://cdn2.thecatapi.com/images/$it.jpg" }
            ?: "https://cdn2.thecatapi.com/images/default.jpg"
    }

    //clears the search bar
    fun clearSearch() {
        _isSearching.value = false
        _searchResults.clear()
    }
}