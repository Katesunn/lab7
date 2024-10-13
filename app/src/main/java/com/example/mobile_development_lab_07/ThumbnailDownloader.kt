package com.example.mobile_development_lab_07

import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.ConcurrentHashMap

private const val TAG = "ThumbnailDownloader"
private const val MESSAGE_DOWNLOAD = 0

class ThumbnailDownloader<in T>(
    private val responseHandler: Handler,
    private val onThumbnailDownloaded: (T, Bitmap) -> Unit
) : HandlerThread(TAG), LifecycleObserver {

    private var hasQuit = false
    private lateinit var requestHandler: Handler
    private val requestMap = ConcurrentHashMap<T, String>()
    private val flickrFetcher = FlickrFetcher()

    init {
        // Запускаем поток сразу в блоке init
        start()
        // Добавляем этот экземпляр как наблюдатель жизненного цикла
        (responseHandler as? LifecycleOwner)?.lifecycle?.addObserver(this)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onLooperPrepared() {
        Log.i(TAG, "Starting background thread")
        requestHandler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    val target = msg.obj as T
                    Log.i(TAG, "Got a request for URL: ${requestMap[target]}")
                    handleRequest(target)
                }
            }
        }
    }

    fun queueThumbnail(target: T, url: String) {
        Log.i(TAG, "Got a URL: $url")
        requestMap[target] = url

        // Проверяем инициализацию requestHandler перед отправкой сообщений
        if (::requestHandler.isInitialized) {
            requestHandler.obtainMessage(MESSAGE_DOWNLOAD, target).sendToTarget()
        } else {
            Log.e(TAG, "Request handler is not initialized.")
        }
    }

    private fun handleRequest(target: T) {
        val url = requestMap[target] ?: return

        // Асинхронно получаем фото и обрабатываем его в колбэке
        flickrFetcher.fetchPhoto(url).observeForever { bitmap ->
            if (bitmap != null) {
                responseHandler.post {
                    if (requestMap[target] != url || hasQuit) {
                        return@post
                    }
                    requestMap.remove(target)
                    onThumbnailDownloaded(target, bitmap)
                }
            }
        }
    }

    // Метод для обработки событий жизненного цикла вручную
    fun onLifecycleEvent(event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_CREATE -> setup()
            Lifecycle.Event.ON_DESTROY -> tearDown()
            else -> {}
        }
    }

    private fun setup() {
        Log.i(TAG, "Setting up background thread")
        start()
    }

    private fun tearDown() {
        Log.i(TAG, "Destroying background thread")
        quit()
    }

    override fun quit(): Boolean {
        hasQuit = true
        return super.quit()
    }

    // Очистка запросов из очереди при уничтожении жизненного цикла владельца
    fun clearQueue() {
        Log.i(TAG, "Clearing all requests from queue")
        requestHandler.removeMessages(MESSAGE_DOWNLOAD)
        requestMap.clear()
    }

    // Реализация fragmentLifecycleObserver для управления жизненным циклом фрагмента
    val fragmentLifecycleObserver: LifecycleObserver = object : LifecycleObserver {

        fun setup() {
            Log.i(TAG, "Setting up fragment lifecycle observer")
            start()
        }

        fun tearDown() {
            Log.i(TAG, "Destroying fragment lifecycle observer")
            quit()
        }

        fun clearQueue() {
            Log.i(TAG, "Clearing all requests from queue in fragment lifecycle observer")
            requestHandler.removeMessages(MESSAGE_DOWNLOAD)
            requestMap.clear()
        }
    }

    // Реализация viewLifecycleObserver для управления жизненным циклом представления фрагмента
    val viewLifecycleObserver: LifecycleObserver = object : LifecycleObserver {

        fun clearQueue() {
            Log.i(TAG, "Clearing all requests from queue in view lifecycle observer")
            requestHandler.removeMessages(MESSAGE_DOWNLOAD)
            requestMap.clear()
        }
    }
}
