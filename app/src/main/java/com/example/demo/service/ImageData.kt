package com.example.demo.service

import com.example.demo.ImageModel
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface ImageData {
    @GET("Pepper/")
    fun getAllImageData():Single<List<ImageModel>>

    @GET("Pepper/{id}")
    fun getImageById(@Path("id",encoded = false) id : Int) : Single<ImageModel>
}