// Указываем пакет, в котором находится наш интерфейс
package com.example.mobile_development_lab_07.api

// Импортируем необходимые классы из библиотек
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

// Определяем интерфейс FlickrApi для работы с API Flickr
interface FlickrApi {

    // Метод для получения списка интересных фотографий
    @GET("services/rest/") // Указываем HTTP метод и путь к ресурсу
    fun fetchPhotos(
        @Query("method") method: String = "flickr.interestingness.getList", // Параметр метода по умолчанию
        @Query("per_page") perPage: Int = 9
    ): Call<FlickrResponse> // Возвращаем объект Call, который содержит ответ от сервера

    // Метод для получения байтов по указанному URL
    @GET // Указываем HTTP метод
    fun fetchUrlBytes(@Url url: String): Call<ResponseBody> // Возвращаем объект Call с телом ответа

    // Метод для поиска фотографий по текстовому запросу
    @GET("services/rest/") // Указываем HTTP метод и путь к ресурсу
    fun searchPhotos(
        @Query("method") method: String = "flickr.photos.search", // Параметр метода по умолчанию для поиска
        @Query("text") text: String, // Параметр текстового запроса для поиска фотографий
        @Query("per_page") perPage: Int = 30
    ): Call<FlickrResponse> // Возвращаем объект Call с ответом от сервера
}
