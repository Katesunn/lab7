package com.example.mobile_development_lab_07

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap // Импортируем switchMap

class PhotoGalleryViewModel(private val app: Application) : AndroidViewModel(app) {
    private val flickrFetcher = FlickrFetcher()
    private val mutableSearchTerm = MutableLiveData<String>()
    val searchTerm: String
        get() = mutableSearchTerm.value ?: ""

    // Используем switchMap напрямую на mutableSearchTerm
    val galleryItemLiveData: LiveData<List<GalleryItem>> = mutableSearchTerm.switchMap { searchTerm ->
        if (searchTerm.isBlank()) {
            flickrFetcher.fetchPhotos()
        } else {
            flickrFetcher.searchPhotos(searchTerm)
        }
    }

    init {
        mutableSearchTerm.value = "planets" // Устанавливаем начальное значение
    }

    fun fetchPhotos(query: String = "") {
        QueryPreferences.setStoredQuery(app, query)
        mutableSearchTerm.value = query
    }
}
