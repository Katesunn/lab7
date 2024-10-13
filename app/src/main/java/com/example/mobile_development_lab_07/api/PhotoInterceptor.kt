package com.example.mobile_development_lab_07.api

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

private const val API_KEY = "08d7a58a4f8a9d0e7842cc9caebcd60a"

class PhotoInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain)
            : Response {
        val originalRequest: Request =
            chain.request()
        val newUrl: HttpUrl =
            originalRequest.url().newBuilder()
                .addQueryParameter("api_key", API_KEY)
                .addQueryParameter("format", "json")
                .addQueryParameter("nojsoncallback", "1")
                .addQueryParameter("extras", "url_s")
                .addQueryParameter("safesearch", "1")
                .build()
        val newRequest: Request =
            originalRequest.newBuilder()
                .url(newUrl)
                .build()
        return chain.proceed(newRequest)
    }
}