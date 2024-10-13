

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
        // Start the thread immediately in the init block
        start()
        // Add this instance as a lifecycle observer to the provided lifecycle owner
        (responseHandler as? LifecycleOwner)?.lifecycle?.addObserver(this)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onLooperPrepared() {
        Log.i(TAG, "Starting background thread")
        requestHandler = object : Handler(Looper.getMainLooper()) { // Specify the main looper
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

        // Ensure requestHandler is initialized before sending messages
        if (::requestHandler.isInitialized) {
            requestHandler.obtainMessage(MESSAGE_DOWNLOAD, target).sendToTarget()
        } else {
            Log.e(TAG, "Request handler is not initialized.")
        }
    }

    private fun handleRequest(target: T) {
        val url = requestMap[target] ?: return

        // Fetch photo asynchronously and handle it in the callback
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

    // Custom method to handle lifecycle events manually
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
}
