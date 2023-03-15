package com.example.demo.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.demo.ImageModel

@Dao
interface ImageDao {
    @Insert
    suspend fun insertAll(vararg images: ImageModel): List<Long>

    @Query("SELECT * FROM imagemodel")
    suspend fun getAllImages():List<ImageModel>

    @Query("SELECT * FROM imagemodel WHERE uuid = :id")
    suspend fun getImage(id:Int):ImageModel

    @Query("DELETE FROM imagemodel")
    suspend fun deleteAllImages()
}