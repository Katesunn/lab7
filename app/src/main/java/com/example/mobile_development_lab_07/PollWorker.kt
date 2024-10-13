package com.example.mobile_development_lab_07

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

private const val TAG = "PollWorker"
class PollWorker(private val context: Context, workerParams: WorkerParameters)
    : Worker(context, workerParams) {
    override fun doWork(): Result {
        val query = QueryPreferences.getStoredQuery(context)
        val lastResultId = QueryPreferences.getLastResultId(context)
        val items: List<GalleryItem> =
            if (query.isEmpty()) {
                FlickrFetcher().fetchPhotosRequest()
                .execute()
                .body()
                ?.photos
                ?.galleryItems
            } else {
                FlickrFetcher().searchPhotosRequest(
                query)
                .execute()
                .body()
                ?.photos
                ?.galleryItems
            } ?: emptyList()

        if (items.isEmpty()) {
            return Result.success()
        }

        val resultId = items.first().id

        if (resultId == lastResultId) {
            Log.i(TAG, "Got an old result: $resultId")
        } else {
            Log.i(TAG, "Got a new result:$resultId")
            QueryPreferences.setLastResultId(context, resultId)
        }

        return Result.success()
    }
}