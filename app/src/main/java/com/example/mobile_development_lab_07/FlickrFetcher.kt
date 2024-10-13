package com.example.mobile_development_lab_07

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mobile_development_lab_07.api.FlickrApi
import com.example.mobile_development_lab_07.api.FlickrResponse
import com.example.mobile_development_lab_07.api.PhotoResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val TAG = "FlickrFetcher"

class FlickrFetcher {
    private val flickrApi: FlickrApi

    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.flickr.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        flickrApi = retrofit.create(FlickrApi::class.java)
    }

    fun fetchPhotos(): LiveData<List<GalleryItem>> {
        val responseLiveData: MutableLiveData<List<GalleryItem>> = MutableLiveData()

        // Call fetchPhotos with the required parameters
        val flickrRequest: Call<FlickrResponse> = flickrApi.fetchPhotos() // Ensure you pass your API key

        flickrRequest.enqueue(object : Callback<FlickrResponse> {
            override fun onFailure(call: Call<FlickrResponse>, t: Throwable) {
                Log.e(TAG, "Failed to fetch photos", t)
            }

            override fun onResponse(
                call: Call<FlickrResponse>,
                response: Response<FlickrResponse>
            ) {
                Log.d(TAG, "Response received")

                val flickrResponse: FlickrResponse? = response.body()
                val photoResponse: PhotoResponse? = flickrResponse?.photos //at com.example.mobile_development_lab_07.FlickrFetcher$fetchPhotos$1.onResponse(FlickrFetcher.kt:51)

                // Get gallery items or an empty list if null
                var galleryItems: List<GalleryItem> = photoResponse?.galleryItems ?: emptyList()

                // Filter out items with blank URLs
                galleryItems = galleryItems.filterNot { it.url.isBlank() }

                responseLiveData.value = galleryItems
            }
        })
        return responseLiveData
    }

    @WorkerThread
    fun fetchPhoto(url: String): LiveData<Bitmap?> {
        val bitmapLiveData = MutableLiveData<Bitmap?>()

        // Use enqueue for asynchronous call instead of execute()
        flickrApi.fetchUrlBytes(url).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e(TAG, "Failed to fetch photo", t)
                bitmapLiveData.value = null // Handle failure case
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    response.body()?.byteStream()?.use { inputStream ->
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        Log.i(TAG, "Decoded bitmap=$bitmap from URL=$url")
                        bitmapLiveData.value = bitmap // Set the decoded Bitmap to LiveData
                    }
                } else {
                    Log.e(TAG, "Error fetching photo: ${response.errorBody()?.string()}")
                    bitmapLiveData.value = null // Handle error case
                }
            }
        })

        return bitmapLiveData // Return LiveData for the fetched Bitmap
    }
}