package com.example.mobile_development_lab_07

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Включаем режим "край до края" для полноэкранного отображения
        setContentView(R.layout.activity_main) // Устанавливаем макет активности
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars()) // Получаем отступы системных панелей
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom) // Устанавливаем отступы для основного представления
            insets // Возвращаем обработанные отступы
        }
    }
}
