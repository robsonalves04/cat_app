package com.example.cat_app.data.api

import com.example.cat_app.data.models.BreedsModel
import com.example.cat_app.data.models.FavouritesModel
import com.example.cat_app.data.models.FavouritesRespondeModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface IBreedsService {

    @GET("v1/breeds")
    suspend fun getBreedsList(
        @Query("limit") limit: Int ,
        @Query("page") page: Int
    ): Response<List<BreedsModel>>

    // GET - Lista de favoritos
    @GET("v1/favourites")
    suspend fun getFavourites(): Response<List<FavouritesRespondeModel>>

    // POST - Adiciona um favorito
    @POST("v1/favourites")
    suspend fun addFavourite(
        @Body request: FavouritesModel
    ): Response<FavouritesRespondeModel>

    // DELETE - Remove um favorito
    @DELETE("v1/favourites/{favourite_id}")
    suspend fun removeFavourite(
        @Path("favourite_id") favouriteId: Int
    ): Response<Unit>


}