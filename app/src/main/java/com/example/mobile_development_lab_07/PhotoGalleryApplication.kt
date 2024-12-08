// Указываем пакет, в котором находится наш класс
package com.example.mobile_development_lab_07

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

// Константа для идентификации канала уведомлений
const val NOTIFICATION_CHANNEL_ID = "flickr_poll"

// Класс PhotoGalleryApplication наследует от Application
class PhotoGalleryApplication : Application() {

    // Метод onCreate вызывается при создании приложения
    override fun onCreate() {
        super.onCreate() // Вызов метода родительского класса для выполнения стандартной инициализации

        // Проверяем, поддерживает ли версия Android создание каналов уведомлений
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Получаем имя канала из ресурсов строк
            val name = getString(R.string.notification_channel_name)

            // Устанавливаем уровень важности канала уведомлений
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            // Создаем новый канал уведомлений с заданным идентификатором, именем и важностью
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance)

            // Получаем экземпляр NotificationManager для управления уведомлениями
            val notificationManager: NotificationManager = getSystemService(NotificationManager::class.java)

            // Создаем канал уведомлений в системе
            notificationManager.createNotificationChannel(channel)
        }
    }
}
