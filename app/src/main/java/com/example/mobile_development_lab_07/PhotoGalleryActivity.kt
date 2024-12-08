// Указываем пакет, в котором находится наш класс
package com.example.mobile_development_lab_07

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import androidx.appcompat.app.AppCompatActivity

// Класс PhotoGalleryActivity наследует от AppCompatActivity
class PhotoGalleryActivity : AppCompatActivity() {

    // Метод onCreate вызывается при создании активности
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Вызов метода родительского класса для выполнения стандартной инициализации

        // Включаем режим строгой политики (StrictMode) для выявления потенциальных проблем с производительностью
        StrictMode.enableDefaults()

        // Устанавливаем макет активности из ресурса XML
        setContentView(R.layout.activity_photo_gallery)

        // Проверяем, пуст ли контейнер для фрагмента (savedInstanceState будет null при первом создании активности)
        val isFragmentContainerEmpty = savedInstanceState == null

        // Если контейнер пуст, добавляем новый фрагмент PhotoGalleryFragment
        if (isFragmentContainerEmpty) {
            supportFragmentManager // Получаем экземпляр FragmentManager для управления фрагментами
                .beginTransaction() // Начинаем транзакцию фрагментов
                .add(R.id.fragmentContainer, PhotoGalleryFragment.newInstance()) // Добавляем новый экземпляр фрагмента в контейнер
                .commit() // Подтверждаем транзакцию
        }
    }

    // Статический метод для создания нового намерения (Intent) для запуска этой активности
    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, PhotoGalleryActivity::class.java) // Создаем и возвращаем новое намерение для запуска активности
        }
    }
}
