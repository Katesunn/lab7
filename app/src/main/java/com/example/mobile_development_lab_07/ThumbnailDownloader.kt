package com.example.mobile_development_lab_07

import android.os.HandlerThread
import android.util.Log

private const val TAG = "ThumbnailDownloader"
class ThumbnailDownloader<in T> : HandlerThread(TAG) {
    private var hasQuit = false
    override fun quit(): Boolean {
        hasQuit = true
        return super.quit()
    }
    fun queueThumbnail(target: T, url: String)
    {
        Log.i(TAG, "Got a URL: $url")
    }
}