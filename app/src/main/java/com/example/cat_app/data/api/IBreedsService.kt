package com.example.cat_app.data.api

import com.example.cat_app.data.models.BreedsModel
import com.example.cat_app.data.models.FavoritesModel
import com.example.cat_app.data.models.FavoritesRespondeModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface IBreedsService {
    // GET - breed list
    @GET("v1/breeds")
    suspend fun getBreedsList(
        @Query("limit") limit: Int ,
        @Query("page") page: Int
    ): Response<List<BreedsModel>>

    // GET - favorite list
    @GET("v1/favourites")
    suspend fun getFavorites(): Response<List<FavoritesRespondeModel>>

    // POST - add favorites
    @POST("v1/favourites")
    suspend fun addFavourite(
        @Body request: FavoritesModel
    ): Response<FavoritesRespondeModel>

    // DELETE - Remove favorites
    @DELETE("v1/favourites/{favourite_id}")
    suspend fun removeFavourite(
        @Path("favourite_id") favouriteId: Int
    ): Response<Unit>

    // GET - breed search
    @GET("v1/breeds/search")
    suspend fun searchBreeds(
        @Query("q") query: String,
        @Query("attach_image") attachImage: Int = 1
    ): Response<List<BreedsModel>>
}