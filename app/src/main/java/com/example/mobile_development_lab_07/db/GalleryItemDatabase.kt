package com.example.mobile_development_lab_07.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.mobile_development_lab_07.GalleryItem
import com.example.mobile_development_lab_07.GalleryItemTagCrossRef
import com.example.mobile_development_lab_07.Tag

// Указываем версии и сущности базы данных
@Database(entities = [GalleryItem::class, Tag::class, GalleryItemTagCrossRef::class], version = 2)
abstract class GalleryItemDatabase : RoomDatabase() {
    abstract fun galleryItemDao(): GalleryItemDao

    companion object {
        @Volatile
        private var INSTANCE: GalleryItemDatabase? = null

        fun getDatabase(context: Context?): GalleryItemDatabase? {
            if (context!= null){
                return INSTANCE ?: synchronized(this) {
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        GalleryItemDatabase::class.java,
                        "app_database" // Имя вашей базы данных
                    ).build()
                    INSTANCE = instance
                    instance
                }
            }
            return null;
        }
    }
}
