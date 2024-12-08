// Указываем пакет, в котором находится наш класс
package com.example.mobile_development_lab_07

import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

private const val TAG = "PollWorker" // Тег для логирования

// Класс PollWorker наследует от Worker для выполнения фоновой работы
class PollWorker(private val context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    // Метод doWork выполняется при запуске работы
    override fun doWork(): Result {
        // Получаем сохраненный поисковый запрос и последний идентификатор результата
        val query = QueryPreferences.getStoredQuery(context)
        val lastResultId = QueryPreferences.getLastResultId(context)

        // Выполняем запрос к Flickr API для получения фотографий
        val items: List<GalleryItem> = if (query.isEmpty()) {
            // Если поисковый запрос пустой, получаем интересные фотографии
            FlickrFetcher().fetchPhotosRequest()
                .execute() // Выполняем запрос синхронно
                .body()
                ?.photos
                ?.galleryItems ?: emptyList() // Возвращаем список фотографий или пустой список
        } else {
            // Если есть поисковый запрос, выполняем поиск по нему
            FlickrFetcher().searchPhotosRequest(query)
                .execute() // Выполняем запрос синхронно
                .body()
                ?.photos
                ?.galleryItems ?: emptyList() // Возвращаем список фотографий или пустой список
        }

        // Если нет новых фотографий, возвращаем успешный результат
        if (items.isEmpty()) {
            return Result.success()
        }

        val resultId = items.first().id // Получаем идентификатор первой фотографии

        // Проверяем, является ли новый результат старым (сравниваем с последним сохраненным идентификатором)
        if (resultId == lastResultId) {
            Log.i(TAG, "Got an old result: $resultId") // Логируем, что получили старый результат
        } else {
            Log.i(TAG, "Got a new result: $resultId") // Логируем новый результат
            QueryPreferences.setLastResultId(context, resultId) // Сохраняем новый идентификатор результата

            val intent = PhotoGalleryActivity.newIntent(context) // Создаем намерение для запуска активности галереи фотографий
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE // Указываем флаги мутабельности для PendingIntent
            )

            // Проверка разрешения на отправку уведомлений (требуется для Android 13 и выше)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                if (context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    return Result.failure() // Завершение работы, если разрешение не получено
                }
            }

            val resources = context.resources // Получаем ресурсы приложения для доступа к строкам и изображениям
            val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setTicker(resources.getString(R.string.new_pictures_title)) // Устанавливаем текст уведомления при появлении в области уведомлений
                .setSmallIcon(android.R.drawable.ic_menu_report_image) // Устанавливаем иконку уведомления
                .setContentTitle(resources.getString(R.string.new_pictures_title)) // Заголовок уведомления
                .setContentText(resources.getString(R.string.new_pictures_text)) // Текст уведомления
                .setContentIntent(pendingIntent) // Устанавливаем намерение для открытия активности при нажатии на уведомление
                .setAutoCancel(true) // Удаляем уведомление после нажатия на него
                .build() // Создаем уведомление

            val notificationManager = NotificationManagerCompat.from(context) // Получаем NotificationManager для управления уведомлениями
            notificationManager.notify(0, notification) // Отправляем уведомление с уникальным идентификатором 0
        }

        return Result.success() // Возвращаем успешный результат работы
    }
}
