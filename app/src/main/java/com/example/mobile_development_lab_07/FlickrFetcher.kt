// Указываем пакет, в котором находится наш класс
package com.example.mobile_development_lab_07

// Импортируем необходимые классы и библиотеки
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mobile_development_lab_07.api.FlickrApi
import com.example.mobile_development_lab_07.api.FlickrResponse
import com.example.mobile_development_lab_07.api.PhotoInterceptor
import com.example.mobile_development_lab_07.api.PhotoResponse
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Константа для логирования
private const val TAG = "FlickrFetcher"

// Класс FlickrFetcher отвечает за получение фотографий из API Flickr
class FlickrFetcher {
    private val flickrApi: FlickrApi // Интерфейс для работы с API

    // Инициализация класса
    init {
        // Создаем OkHttpClient с интерсептором PhotoInterceptor для добавления параметров к запросам
        val client = OkHttpClient.Builder()
            .addInterceptor(PhotoInterceptor())
            .build()

        // Создаем Retrofit экземпляр с базовым URL и конвертером JSON
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.flickr.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        // Создаем экземпляр интерфейса FlickrApi
        flickrApi = retrofit.create(FlickrApi::class.java)
    }

    // Метод для получения запроса на получение фотографий
    fun fetchPhotosRequest(): Call<FlickrResponse> {
        return flickrApi.fetchPhotos()
    }

    // Метод для получения фотографий и возвращения их в виде LiveData
    fun fetchPhotos(): LiveData<List<GalleryItem>> {
        return fetchPhotoMetadata(fetchPhotosRequest())
    }

    // Метод для получения запроса на поиск фотографий по тексту
    fun searchPhotosRequest(text: String): Call<FlickrResponse> {
        return flickrApi.searchPhotos(text=text)
    }

    // Метод для поиска фотографий и возвращения их в виде LiveData
    fun searchPhotos(text: String): LiveData<List<GalleryItem>> {
        return fetchPhotoMetadata(flickrApi.searchPhotos(text=text))
    }

    // Приватный метод для получения метаданных фотографий из ответа API
    private fun fetchPhotoMetadata(flickrRequest: Call<FlickrResponse>): LiveData<List<GalleryItem>> {
        val responseLiveData: MutableLiveData<List<GalleryItem>> = MutableLiveData()

        // Асинхронный вызов к API с использованием enqueue()
        flickrRequest.enqueue(object : Callback<FlickrResponse> {
            override fun onFailure(call: Call<FlickrResponse>, t: Throwable) {
                Log.e(TAG, "Failed to fetch photos", t) // Логируем ошибку при неудаче запроса
            }

            override fun onResponse(
                call: Call<FlickrResponse>,
                response: Response<FlickrResponse>
            ) {
                Log.d(TAG, "Response received") // Логируем успешный ответ

                val flickrResponse: FlickrResponse? = response.body() // Получаем тело ответа
                val photoResponse: PhotoResponse? = flickrResponse?.photos // Извлекаем фотографии

                var galleryItems: List<GalleryItem> = photoResponse?.galleryItems ?: emptyList() // Получаем список галерей

                galleryItems = galleryItems.filterNot { it.url.isBlank() } // Фильтруем элементы без URL

                responseLiveData.value = galleryItems // Устанавливаем значение в LiveData
            }
        })
        return responseLiveData // Возвращаем LiveData со списком галерей
    }

    @WorkerThread // Указываем, что этот метод должен выполняться в фоновом потоке
    fun fetchPhoto(url: String): LiveData<Bitmap?> {
        val bitmapLiveData = MutableLiveData<Bitmap?>()

        // Асинхронный вызов для получения изображения по URL
        flickrApi.fetchUrlBytes(url).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e(TAG, "Failed to fetch photo", t) // Логируем ошибку при неудаче запроса
                bitmapLiveData.value = null // Устанавливаем значение null в случае ошибки
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) { // Проверяем успешность ответа
                    response.body()?.byteStream()?.use { inputStream ->
                        val bitmap = BitmapFactory.decodeStream(inputStream) // Декодируем поток в изображение Bitmap
                        Log.i(TAG, "Decoded bitmap=$bitmap from URL=$url") // Логируем успешное декодирование изображения
                        bitmapLiveData.value = bitmap // Устанавливаем полученное изображение в LiveData
                    }
                } else {
                    Log.e(TAG, "Error fetching photo: ${response.errorBody()?.string()}") // Логируем ошибку при получении изображения
                    bitmapLiveData.value = null // Устанавливаем значение null в случае ошибки ответа от сервера 
                }
            }
        })

        return bitmapLiveData // Возвращаем LiveData с изображением Bitmap или null 
    }
}
