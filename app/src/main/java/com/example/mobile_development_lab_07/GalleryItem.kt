package com.example.mobile_development_lab_07
import com.google.gson.annotations.SerializedName

data class GalleryItem(
    var id: String = "",
    var title: String = "",
    var description: String = "",
    var ownerName: String = "",
    var dateTaken: String = "",
    @SerializedName("url_s")var url: String = ""
)