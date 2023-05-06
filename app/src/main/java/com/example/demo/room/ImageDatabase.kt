package com.example.demo.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.demo.ImageModel
import java.io.FileOutputStream

@Database(entities = arrayOf(ImageModel::class), version = 1)
abstract class ImageDatabase : RoomDatabase(){

    abstract fun imageDao() : ImageDao

    companion object{
        @Volatile private var instance : ImageDatabase? = null

        private var lock = Any()
        operator fun invoke(context: Context) = instance ?: synchronized(lock){
            instance ?: makeDatabase(context).also {
                instance = it
                //getAndCopyAssetDatabase(context)
            }
        }

        private fun makeDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,ImageDatabase::class.java,"imagedb"
        ).build()

        private fun getAndCopyAssetDatabase(context: Context) {
            context.assets.open("Pepper.db").copyTo(
                FileOutputStream(context.getDatabasePath("imagedb")),
                (8 * 1024)
            )
        }
    }
}