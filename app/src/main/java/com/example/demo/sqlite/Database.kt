package com.example.demo.sqlite

import android.content.ContentValues
import android.provider.BaseColumns
import androidx.lifecycle.MutableLiveData
import com.example.demo.ImageModel

class Database(val dbHelper: ImageDbHelper) {

    fun deleteAllImages(){
        val db = dbHelper.writableDatabase
        // Define 'where' part of query.
        val selection = ""
        // Specify arguments in placeholder order.
        val selectionArgs = arrayOf("")
        // Issue SQL statement.
        val deletedRows = db.delete(ImageObject.ImageEntry.TABLE_NAME, null, null)
    }

    fun getImageDataFromSqlite(arg : String) : ImageModel{
        val db = dbHelper.readableDatabase
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        val projection = arrayOf(
            BaseColumns._ID,
            ImageObject.ImageEntry.COLUMN_NAME_IMAGE_URL,
            ImageObject.ImageEntry.COLUMN_NAME_EMOTION_LABEL,
            ImageObject.ImageEntry.COLUMN_NAME_QUESTION
        )
        // Filter results WHERE "title" = 'My Title'
        //val selection = "${FeedEntry.COLUMN_NAME_TITLE} = ?"
        val selection = "${ImageObject.ImageEntry.COLUMN_NAME_IMAGE_URL} LIKE ?"
        val selectionArgs = arrayOf("%${arg}%")
        // How you want the results sorted in the resulting Cursor
        //val sortOrder = "${FeedEntry.COLUMN_NAME_SUBTITLE} DESC"
        val sortOrder = "${ImageObject.ImageEntry.COLUMN_NAME_IMAGE_URL} DESC"

        val cursor = db.query(
            ImageObject.ImageEntry.TABLE_NAME,   // The table to query
            projection,             // The array of columns to return (pass null to get all)
            selection,              // The columns for the WHERE clause
            selectionArgs,          // The values for the WHERE clause
            null,                   // don't group the rows
            null,                   // don't filter by row groups
            sortOrder               // The sort order
        )
        var imageModel = ImageModel("","","")

        with(cursor){

            while (moveToNext()){
                val imageUrl = getString(getColumnIndexOrThrow(ImageObject.ImageEntry.COLUMN_NAME_IMAGE_URL))
                val emotionLabel = getString(getColumnIndexOrThrow(ImageObject.ImageEntry.COLUMN_NAME_EMOTION_LABEL))
                val question = getString(getColumnIndexOrThrow(ImageObject.ImageEntry.COLUMN_NAME_QUESTION))
                imageModel = ImageModel(imageUrl, emotionLabel,question)
            }
        }

        cursor.close()

        return imageModel

    }

    fun getAllImageDataFromSqlite() : MutableList<ImageModel>{
        val db = dbHelper.readableDatabase

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        val projection = arrayOf(
            BaseColumns._ID,
            ImageObject.ImageEntry.COLUMN_NAME_IMAGE_URL,
            ImageObject.ImageEntry.COLUMN_NAME_EMOTION_LABEL,
            ImageObject.ImageEntry.COLUMN_NAME_QUESTION
        )

        // Filter results WHERE "title" = 'My Title'
        //val selection = "${FeedEntry.COLUMN_NAME_TITLE} = ?"
        val selection = ""
        val selectionArgs = arrayOf("")

        // How you want the results sorted in the resulting Cursor
        //val sortOrder = "${FeedEntry.COLUMN_NAME_SUBTITLE} DESC"
        val sortOrder = "${ImageObject.ImageEntry.COLUMN_NAME_IMAGE_URL} DESC"

        val cursor = db.query(
            ImageObject.ImageEntry.TABLE_NAME,   // The table to query
            projection,             // The array of columns to return (pass null to get all)
            null,              // The columns for the WHERE clause
            null,          // The values for the WHERE clause
            null,                   // don't group the rows
            null,                   // don't filter by row groups
            sortOrder               // The sort order
        )

        val imageModelList = mutableListOf<ImageModel>()
        with(cursor) {
            while (moveToNext()) {
                //val itemId = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                val imageUrl = getString(getColumnIndexOrThrow(ImageObject.ImageEntry.COLUMN_NAME_IMAGE_URL))
                val emotionLabel = getString(getColumnIndexOrThrow(ImageObject.ImageEntry.COLUMN_NAME_EMOTION_LABEL))
                val question = getString(getColumnIndexOrThrow(ImageObject.ImageEntry.COLUMN_NAME_QUESTION))

                val imageModel = ImageModel(imageUrl,emotionLabel,question)
                imageModelList.add(imageModel)
            }
        }
        cursor.close()

        return imageModelList;
    }
    fun fillDatabase(){
        val db = dbHelper.writableDatabase

        val imageValues = listOf<ImageModel>(
            ImageModel("http://isguzaktanegitim.net/pepper/betul.jpg","Üzgün",""),
            ImageModel("http://isguzaktanegitim.net/pepper/emreAraba.jpg","Üzgün",""),
            ImageModel("http://isguzaktanegitim.net/pepper/emreTop.jpg","Üzgün",""),
            ImageModel("http://isguzaktanegitim.net/pepper/emreUcurtma.jpg","Üzgün",""),
        )

        // Create a new map of values, where column names are the keys
        imageValues.map { image ->
            val values = ContentValues().apply {

                put(ImageObject.ImageEntry.COLUMN_NAME_IMAGE_URL, image.imageUrl)
                put(ImageObject.ImageEntry.COLUMN_NAME_EMOTION_LABEL, image.label)
                put(ImageObject.ImageEntry.COLUMN_NAME_QUESTION, image.question)

            }
            // Insert the new row, returning the primary key value of the new row
            val newRowId = db?.insert(ImageObject.ImageEntry.TABLE_NAME, null, values)
        }


    }
}