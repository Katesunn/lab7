package com.example.mobile_development_lab_07

import android.os.HandlerThread
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer

private const val TAG = "ThumbnailDownloader"

class ThumbnailDownloader<in T>(private val lifecycleOwner: LifecycleOwner) : HandlerThread(TAG), LifecycleObserver {

    private var hasQuit = false

    override fun quit(): Boolean {
        hasQuit = true
        return super.quit()
    }

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    fun setup() {
        Log.i(TAG, "Starting background thread")
    }

    fun tearDown() {
        Log.i(TAG, "Destroying background thread")
        quit()
    }

    fun queueThumbnail(target: T, url: String) {
        Log.i(TAG, "Got a URL: $url")
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
