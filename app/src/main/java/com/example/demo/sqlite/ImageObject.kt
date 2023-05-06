package com.example.demo.sqlite

import android.provider.BaseColumns

object ImageObject {
    object ImageEntry : BaseColumns {
        const val TABLE_NAME = "pepper"
        const val COLUMN_NAME_IMAGE_URL = "ImageURL"
        const val COLUMN_NAME_EMOTION_LABEL = "EmotionLabel"
        const val COLUMN_NAME_QUESTION = "Question"
    }
}