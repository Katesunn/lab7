package com.example.mobile_development_lab_07.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mobile_development_lab_07.GalleryItem
import com.example.mobile_development_lab_07.GalleryItemTagCrossRef
import com.example.mobile_development_lab_07.Tag

@Dao
interface GalleryItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGalleryItem(galleryItem: GalleryItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTags(tags: List<Tag>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGalleryItemTagCrossRef(crossRef: GalleryItemTagCrossRef)

    @Query("SELECT * FROM gallery_items")
    suspend fun getAllGalleryItems(): List<GalleryItem>

    @Query("SELECT * FROM tags WHERE id IN (SELECT tagId FROM gallery_item_tag_cross_ref WHERE galleryItemId = :galleryItemId)")
    suspend fun getTagsForGalleryItem(galleryItemId: String): List<Tag>

    @Query("DELETE FROM gallery_items")
    suspend fun deleteAllGalleryItems()

    @Query("DELETE FROM gallery_items WHERE id = :galleryItemId")
    suspend fun deleteGalleryItem(galleryItemId: String)
}
