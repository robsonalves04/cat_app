package com.example.cat_app.data.api

import com.example.cat_app.data.models.BreedsModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface IBreedsService {

    @GET("v1/breeds")
    suspend fun getBreedsList(
        @Query("limit") limit: Int = 10,
        @Query("page") page: Int = 0
    ): Response<List<BreedsModel>>

}