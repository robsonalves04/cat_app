package com.example.cat_app.data.models

import com.google.gson.annotations.SerializedName

data class BreedsModel(
    val id: String ? = null,
    val name: String ? = null,
    val origin: String ? = null ,
    val temperament: String ? = null,
    val description: String ? = null,
    @SerializedName("life_span") val lifeSpan: String?,
    @SerializedName("reference_image_id") val referenceImageId: String?,
    val image: BreedsImageModel ? = null
)

data class BreedsImageModel(
    val id: String ? = null,
    val url: String ? = null
)
