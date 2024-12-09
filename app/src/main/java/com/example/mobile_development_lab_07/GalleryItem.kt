package com.example.mobile_development_lab_07
import com.google.gson.annotations.SerializedName

data class GalleryItem(
    var id: String = "",
    var title: String = "",
    var tags: List<Tag> = emptyList(),
    var ownerRealName: String = "",
    var dateTaken: String = "",
    @SerializedName("url_s")var url: String = ""
)

data class Tag(
    @SerializedName("id") var id: String = "",
    @SerializedName("_content") var title: String = ""
)