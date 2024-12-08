// Указываем пакет, в котором находится наш класс
package com.example.mobile_development_lab_07.api

// Импортируем необходимые классы из библиотеки OkHttp
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

// Константа для хранения API-ключа
private const val API_KEY = "08d7a58a4f8a9d0e7842cc9caebcd60a"

// Класс PhotoInterceptor реализует интерфейс Interceptor
class PhotoInterceptor : Interceptor {
    // Переопределяем метод intercept, который будет вызываться для каждого запроса
    override fun intercept(chain: Interceptor.Chain): Response {
        // Получаем оригинальный запрос
        val originalRequest: Request = chain.request()

        // Создаем новый URL, добавляя необходимые параметры запроса
        val newUrl: HttpUrl = originalRequest.url().newBuilder()
            .addQueryParameter("api_key", API_KEY) // Добавляем API-ключ
            .addQueryParameter("format", "json") // Указываем формат ответа
            .addQueryParameter("nojsoncallback", "1") // Отключаем JSONP
            .addQueryParameter("extras", "url_s") // Запрашиваем дополнительные параметры (например, URL изображения)
            .addQueryParameter("safesearch", "1") // Включаем безопасный поиск
            .build()

        // Создаем новый запрос с обновленным URL
        val newRequest: Request = originalRequest.newBuilder()
            .url(newUrl) // Устанавливаем новый URL в запросе
            .build()

        // Пропускаем новый запрос через цепочку интерсепторов и возвращаем ответ
        return chain.proceed(newRequest)
    }
}
