package com.example.demo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.demo.service.ImageDataService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

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



}