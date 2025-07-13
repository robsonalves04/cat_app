package com.example.cat_app.data.api


import com.example.cat_app.data.models.BreedsModel
import com.example.cat_app.data.models.FavouritesModel
import com.example.cat_app.data.models.FavouritesRespondeModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.GsonBuilder
import retrofit2.Response

class BreedsService : IBreedsService {

    private val apiService: IBreedsService
    val apiKey = "live_u2drf7PKRdtYsYO55nXCH7t9TaSb2WsIZ5cO2PDYezRh08RPzt9yEVtrOTi3dChV"

    init {
//        val client = OkHttpClient.Builder()
//            .addInterceptor(HttpLoggingInterceptor().apply {
//                level = HttpLoggingInterceptor.Level.BODY
//            })
//            .build()

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

    override suspend fun getBreedsList(
        limit: Int,
        page: Int,
    ) = apiService.getBreedsList(limit, page)

    override suspend fun getFavourites(): Response<List<FavouritesRespondeModel>> {
        return apiService.getFavourites()
    }

    override suspend fun addFavourite(request: FavouritesModel): Response<FavouritesRespondeModel> {
        return apiService.addFavourite(request)
    }

    override suspend fun removeFavourite(favouriteId: Int): Response<Unit> {
        return apiService.removeFavourite(favouriteId)
    }

}