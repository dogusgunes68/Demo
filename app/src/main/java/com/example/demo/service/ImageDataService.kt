package com.example.demo.service

import com.example.demo.ImageModel
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class ImageDataService {
    //http://localhost:5090/Pepper/1
    private val api = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:5090/")
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ImageData::class.java)

     fun getAllData() : Single<List<ImageModel>> {
        return api.getAllImageData()
     }

    fun getDataById(id:Int):Single<ImageModel>{
        return api.getImageById(id)
    }

}