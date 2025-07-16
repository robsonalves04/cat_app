package com.example.cat_app.data.models

import com.google.gson.annotations.SerializedName


//data model used in the request body
data class FavoritesModel(
    @SerializedName("image_id") val imageId: String
)

data class FavoritesRespondeModel(
    val id: Int,
    @SerializedName("image_id") val imageId: String,
    val sub_id: String? = null,
    val created_at: String
)