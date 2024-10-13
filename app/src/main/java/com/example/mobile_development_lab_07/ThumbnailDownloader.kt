package com.example.mobile_development_lab_07

import android.annotation.SuppressLint
import android.os.HandlerThread
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.ConcurrentHashMap
import android.os.Handler
import android.os.Message


private const val TAG = "ThumbnailDownloader"
private const val MESSAGE_DOWNLOAD = 0

class ThumbnailDownloader<in T>(private val lifecycleOwner: LifecycleOwner) : HandlerThread(TAG), LifecycleObserver {

    private var hasQuit = false

    private lateinit var requestHandler: Handler

    private val requestMap = ConcurrentHashMap<T, String>()

    private val flickrFetcher = FlickrFetcher()

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    fun setup() {
        Log.i(TAG, "Starting background thread")
        start()
        looper
    }

    fun tearDown() {
        Log.i(TAG, "Destroying background thread")
        quit()
    }

    override fun quit(): Boolean {
        hasQuit = true
        return super.quit()
    }

    @Suppress("UNCHECKED_CAST")
    @SuppressLint("HandlerLeak")
    override fun onLooperPrepared() {
        requestHandler = object : Handler() {
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
        requestHandler.obtainMessage(MESSAGE_DOWNLOAD, target).sendToTarget()
    }

    private fun handleRequest(target: T) {
        val url = requestMap[target] ?: return
        val bitmap = flickrFetcher.fetchPhoto(url) ?: return
    }

    // Call this method when the LifecycleOwner's state changes
    fun onLifecycleEvent(event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_CREATE -> setup()
            Lifecycle.Event.ON_DESTROY -> tearDown()
            else -> {}
        }
    }
}
