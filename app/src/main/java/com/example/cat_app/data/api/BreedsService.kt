package com.example.cat_app.data.api


import com.example.cat_app.data.models.BreedsModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.GsonBuilder

class BreedsService : IBreedsService {

   private val apiService: IBreedsService

    init {
        val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

        val gson = GsonBuilder()
            .setLenient()
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.thecatapi.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()

        apiService = retrofit.create(BreedsService::class.java)
    }

    override suspend fun getBreedsList(
        limit: Int,
        page: Int,
    ) = apiService.getBreedsList()



}