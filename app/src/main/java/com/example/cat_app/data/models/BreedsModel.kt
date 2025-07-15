package com.example.cat_app.data.models

import com.google.gson.annotations.SerializedName

data class BreedsModel(
    val id: String? = null,
    val name: String? = null,
    val origin: String? = null,

    val temperament: String? = null,
    val description: String? = null,

    @SerializedName("life_span")
    val lifeSpan: String? = null,

    @SerializedName("reference_image_id")
    val referenceImageId: String? = null,

    val image: BreedsImageModel? = null,

    val weight: BreedsWeightModel? = null
)

data class BreedsImageModel(
    val id: String? = null,
    val url: String? = null,
    val width: Int? = null,
    val height: Int? = null
)

data class BreedsWeightModel(
    val imperial: String? = null,
    val metric: String? = null
)

data class BreedDetailsModel(
    val id: Int,
    val name: String,
    val weight: String,
    val height: String,
    val life_span: String,
    val bred_for: String?,
    val breed_group: String?
)

