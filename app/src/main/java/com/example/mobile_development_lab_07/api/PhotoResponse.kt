package com.example.mobile_development_lab_07.api

import com.example.mobile_development_lab_07.GalleryItem
import com.google.gson.annotations.SerializedName

class PhotoResponse {
    @SerializedName("photo") lateinit var galleryItems: List<GalleryItem>
}