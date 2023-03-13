package com.example.demo

import com.google.gson.annotations.SerializedName

data class ImageModel(
    val imageUrl : String,
    @SerializedName("emotıonLabel")
    val label : String
) {
}