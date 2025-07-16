package com.example.cat_app.data.api


import com.example.cat_app.data.models.BreedsModel

import com.example.cat_app.data.models.FavoritesModel
import com.example.cat_app.data.models.FavoritesRespondeModel
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.GsonBuilder
import retrofit2.Response
import retrofit2.http.Query

class BreedsService : IBreedsService {

    private val apiService: IBreedsService
    //val key of header to request
    val apiKey = "live_u2drf7PKRdtYsYO55nXCH7t9TaSb2WsIZ5cO2PDYezRh08RPzt9yEVtrOTi3dChV"

    init {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("x-api-key", apiKey)
                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .build()

        val gson = GsonBuilder()
            .setLenient()
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.thecatapi.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        apiService = retrofit.create(IBreedsService::class.java)
    }

    //function to get a breed list from the API
    override suspend fun getBreedsList(limit: Int,page: Int,
    ) = apiService.getBreedsList(limit, page)

    //function to get a favorites from the API
    override suspend fun getFavorites(): Response<List<FavoritesRespondeModel>> {
        return apiService.getFavorites()
    }

    //function to add a breed from the favorites list
    override suspend fun addFavourite(request: FavoritesModel): Response<FavoritesRespondeModel> {
        return apiService.addFavourite(request)
    }

    //function to remove a breed from the favorites list
    override suspend fun removeFavourite(favouriteId: Int): Response<Unit> {
        return apiService.removeFavourite(favouriteId)
    }

    //function to search a breed from the APi
    override suspend fun searchBreeds(
        @Query(value = "q") query: String,
        @Query(value = "attach_image") attachImage: Int
    ): Response<List<BreedsModel>> = apiService.searchBreeds(query)

}