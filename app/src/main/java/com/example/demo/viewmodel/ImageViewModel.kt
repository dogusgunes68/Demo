package com.example.demo.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.example.demo.ImageModel
import com.example.demo.room.ImageDao
import com.example.demo.room.ImageDatabase
import com.example.demo.service.ImageDataService
import com.example.demo.sqlite.Database
import com.example.demo.sqlite.ImageDbHelper
import com.example.demo.sqlite.ReadableDatabase
import com.example.demo.sqlite.WritableDatabase
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

    var dbHelper = ImageDbHelper(application.applicationContext)
    var database = Database(dbHelper)

    /*
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


     */

    fun deleteAllImages(){
        launch {
            database.deleteAllImages()
        }
    }

     fun storeInSQLite(){
        launch {
            database.fillDatabase()
        }
    }
    fun getImageFromSqlite(arg : String){
        launch {
            image.value = database.getImageDataFromSqlite(arg)
            println(image.value)

        }
    }

     fun getAllImagesFromSQLite(){
        launch {
           images.value = database.getAllImageDataFromSqlite()
        }
    }



}