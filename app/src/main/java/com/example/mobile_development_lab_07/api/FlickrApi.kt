package com.example.mobile_development_lab_07.api

import com.example.mobile_development_lab_07.api.FlickrResponse
//import okhttp3.ResponseBody
//import retrofit2.Call
//import retrofit2.http.GET
//import retrofit2.http.Query
//import retrofit2.http.Url
//
//private const val API_KEY : String = "08d7a58a4f8a9d0e7842cc9caebcd60a"
//private const val FORMAT : String = "json"
//private const val NOJSONCALLBACK : String = "1"
//private const val EXTRAS : String = "url_s"
//
//interface com.example.mobile_development_lab_07.api.FlickrApi {
//    // Fetch photos using query parameters
//    @GET(
//        "services/rest/"+
//                "?method=flickr.interestingness.getList" +
//                "&api_key=$API_KEY" +
//                "&format=$FORMAT" +
//                "&nojsoncallback=$NOJSONCALLBACK" +
//                "&extras=$EXTRAS"
//    )
//    fun fetchPhotos(): Call<FlickrResponse>
//
//    // Fetch URL bytes for a given URL
//    @GET
//    fun fetchUrlBytes(@Url url: String): Call<ResponseBody>
//}

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface FlickrApi {
    // Fetch photos using query parameters
    @GET("services/rest/")
    fun fetchPhotos(
        @Query("method") method: String = "flickr.interestingness.getList",
        @Query("api_key") apiKey: String = "08d7a58a4f8a9d0e7842cc9caebcd60a",
        @Query("format") format: String = "json",
        @Query("nojsoncallback") noJsonCallback: Int = 1,
        @Query("extras") extras: String = "url_s"
    ): Call<FlickrResponse>

    // Fetch URL bytes for a given URL
    @GET
    fun fetchUrlBytes(@Url url: String): Call<ResponseBody>
}
