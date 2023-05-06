package com.example.demo.utils

import android.content.Context
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.demo.R


fun ImageView.downloadPic(url:String?, placeHolder: CircularProgressDrawable){

    val options = RequestOptions().placeholder(placeHolder).error(R.mipmap.ic_launcher_round)
    Glide.with(context).setDefaultRequestOptions(options).load(url.toString()).into(this)

}

fun makePlaceHolder(context: Context) :CircularProgressDrawable{
    return CircularProgressDrawable(context).apply {
        strokeWidth = 8f
        centerRadius = 40f
        start()
    }
}