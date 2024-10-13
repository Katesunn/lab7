package com.example.mobile_development_lab_07

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap // Импортируем switchMap

class PhotoGalleryViewModel : ViewModel() {
    private val flickrFetcher = FlickrFetcher()
    private val mutableSearchTerm = MutableLiveData<String>()

    // Используем switchMap напрямую на mutableSearchTerm
    val galleryItemLiveData: LiveData<List<GalleryItem>> = mutableSearchTerm.switchMap { searchTerm ->
        flickrFetcher.searchPhotos(searchTerm)
    }

    init {
        mutableSearchTerm.value = "planets" // Устанавливаем начальное значение
    }

    fun fetchPhotos(query: String = "") {
        mutableSearchTerm.value = query
    }
}
