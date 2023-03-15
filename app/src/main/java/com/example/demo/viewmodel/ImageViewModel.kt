package com.example.demo.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.example.demo.ImageModel
import com.example.demo.room.ImageDao
import com.example.demo.room.ImageDatabase
import com.example.demo.service.ImageDataService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch

class ImageViewModel(application: Application) : BaseViewModel(application) {
    var images = MutableLiveData<List<ImageModel>>()
    var image = MutableLiveData<ImageModel>()

    var service = ImageDataService()
    var compositeDisposable = CompositeDisposable()

     fun getAllData(){
        compositeDisposable.add(service.getAllData()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<List<ImageModel>>(){

                override fun onSuccess(t: List<ImageModel>) {
                    images.value = t!!
                }

                override fun onError(e: Throwable) {
                    println(e!!.localizedMessage)
                }

            })
        )
    }

    fun getDataById(id:Int){
        compositeDisposable.add(service.getDataById(id)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<ImageModel>(){

                override fun onSuccess(t: ImageModel) {
                    image.value = t!!
                }

                override fun onError(e: Throwable) {
                    println(e!!.localizedMessage)
                }

            })
        )
    }

     fun storeInSQLite(list : List<ImageModel>){
        launch {
            val imageDao = ImageDatabase(getApplication()).imageDao()
            imageDao.deleteAllImages()
            val listLong = imageDao.insertAll(*list.toTypedArray())
            var i=0
            while (i < list.size){
                list[i].uuid = listLong[i].toInt()
                i++
            }

            //images.value = list

        }
    }

     fun getAllImagesFromSQLite(){
        launch {
            val imageDao = ImageDatabase(getApplication()).imageDao()
            val list = imageDao.getAllImages()
            println("Image List")
            println(list)
            images.value = list
        }
    }



}