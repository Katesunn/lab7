package com.example.mobile_development_lab_07.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

//import retrofit2.Call
//import retrofit2.http.GET
//import retro

//private const val API_KEY : String = "08d7a58a4f8a9d0e7842cc9caebcd60a"
//private const val FORMAT : String = "json"
//private const val NOJSONCALLBACK : String = "1"
//private const val EXTRAS : String = "url_s"

interface FlickrApi {
//    @GET(
//        "services/rest/"+
//                "?method=flickr.interestingness.getList" +
//                "&api_key=$API_KEY" +
//                "&format=$FORMAT" +
//                "&nojsoncallback=$NOJSONCALLBACK" +
//                "&extras=$EXTRAS"
//    )
    @GET
    fun fetchUrlBytes(@Url url: String):
            Call<ResponseBody>
    fun fetchPhotos(): Call<FlickrResponse>
}