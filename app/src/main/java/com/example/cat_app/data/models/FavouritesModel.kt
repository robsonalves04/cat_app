package com.example.cat_app.data.models

import com.google.gson.annotations.SerializedName

data class FavouritesModel(
    @SerializedName("image_id") val imageId: String
)

data class FavouritesRespondeModel(
    val id: Int,
    @SerializedName("image_id") val imageId: String,
    val sub_id: String? = null,
    val created_at: String
)
