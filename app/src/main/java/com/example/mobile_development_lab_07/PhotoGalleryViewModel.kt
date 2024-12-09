// Указываем пакет, в котором находится наш класс
package com.example.mobile_development_lab_07

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap // Импортируем switchMap

// Класс PhotoGalleryViewModel наследует от AndroidViewModel
class PhotoGalleryViewModel(private val app: Application) : AndroidViewModel(app) {
    private val flickrFetcher = FlickrFetcher() // Создаем экземпляр FlickrFetcher для получения данных из API
    private val mutableSearchTerm = MutableLiveData<String>() // MutableLiveData для хранения текущего поискового запроса

    // Свойство для получения текущего поискового запроса
    val searchTerm: String
        get() = mutableSearchTerm.value ?: "" // Возвращает значение или пустую строку, если значение null

    // Используем switchMap для преобразования изменений в mutableSearchTerm в LiveData с результатами поиска
    val galleryItemLiveData: LiveData<List<GalleryItem>> = mutableSearchTerm.switchMap { searchTerm ->
        if (searchTerm.isBlank()) {
            flickrFetcher.fetchPhotos() // Если поисковый запрос пустой, получаем интересные фотографии
        } else {
            flickrFetcher.searchPhotos(text = searchTerm) // Иначе выполняем поиск по введенному запросу
        }
    }

    init {
        mutableSearchTerm.value = "planets" // Устанавливаем начальное значение для поискового запроса
    }

    // Метод для обновления поискового запроса и сохранения его в предпочтениях
    fun fetchPhotos(query: String = "") {
        QueryPreferences.setStoredQuery(app, query) // Сохраняем поисковый запрос в предпочтениях приложения
        mutableSearchTerm.value = query // Обновляем значение mutableSearchTerm, что вызовет обновление galleryItemLiveData
    }
}
