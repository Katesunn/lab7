package com.example.mobile_development_lab_07.api

import com.example.mobile_development_lab_07.GalleryItem
import com.google.gson.annotations.SerializedName

class FlickrResponse {
    var photos: PhotosResponse? = null // Change to nullable
//    @SerializedName("photo") var photo: GalleryItem? = null // Change to nullable
    var photo: PhotoResponse? = null // Change to nullable
}
