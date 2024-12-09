package com.example.mobile_development_lab_07
import com.google.gson.annotations.SerializedName
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "gallery_items")
data class GalleryItem(
    @PrimaryKey var id: String = "",
    var title: String = "",
    var ownerRealName: String = "",
    var dateTaken: String = "",
    @SerializedName("url_s")var url: String = ""
)

@Entity(tableName = "tags")
data class Tag(
    @PrimaryKey @SerializedName("id") var id: String = "",
    @SerializedName("_content") var title: String = ""
)

@Entity(tableName = "gallery_item_tag_cross_ref")
data class GalleryItemTagCrossRef(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val galleryItemId: String,
    val tagId: String
)