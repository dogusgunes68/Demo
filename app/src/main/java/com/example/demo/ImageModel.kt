package com.example.demo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class ImageModel(
    @ColumnInfo(name = "imageurl")
    val imageUrl : String,
    @ColumnInfo(name = "label")
    val label : String,
    @ColumnInfo(name = "question")
    val question : String
) {
    @PrimaryKey(autoGenerate = true)
    var uuid : Int = 0
}