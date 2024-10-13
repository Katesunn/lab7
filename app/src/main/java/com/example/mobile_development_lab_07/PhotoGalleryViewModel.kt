package com.example.mobile_development_lab_07

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class PhotoGalleryViewModel : ViewModel() {
    val galleryItemLiveData:
            LiveData<List<GalleryItem>>
    init {
        galleryItemLiveData =
            FlickrFetcher().searchPhotos("planets")
    }
}