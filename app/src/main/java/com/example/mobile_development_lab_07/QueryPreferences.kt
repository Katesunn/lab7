// Указываем пакет, в котором находится наш класс
package com.example.mobile_development_lab_07

import android.content.Context
import androidx.core.content.edit // Импортируем функцию edit из androidx.core.content
import androidx.preference.PreferenceManager // Импортируем PreferenceManager для работы с SharedPreferences

// Константы для ключей настроек
private const val PREF_SEARCH_QUERY = "searchQuery" // Ключ для сохранения поискового запроса
private const val PREF_LAST_RESULT_ID = "lastResultId" // Ключ для сохранения последнего идентификатора результата
private const val PREF_IS_POLLING = "isPolling" // Ключ для хранения состояния опроса

// Объект QueryPreferences используется для работы с настройками приложения
object QueryPreferences {

    // Получение сохраненного поискового запроса
    fun getStoredQuery(context: Context): String {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context) // Получаем экземпляр SharedPreferences
        return prefs.getString(PREF_SEARCH_QUERY, "") ?: "" // Возвращаем сохраненный запрос или пустую строку
    }

    // Сохранение поискового запроса
    fun setStoredQuery(context: Context, query: String) {
        PreferenceManager.getDefaultSharedPreferences(context).edit { // Открываем редактор для редактирования настроек
            putString(PREF_SEARCH_QUERY, query) // Сохраняем поисковый запрос
        }
    }

    // Получение последнего идентификатора результата
    fun getLastResultId(context: Context): String {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getString(PREF_LAST_RESULT_ID, "")!! // Возвращаем последний идентификатор результата или пустую строку
    }

    // Сохранение последнего идентификатора результата
    fun setLastResultId(context: Context, lastResultId: String) {
        PreferenceManager.getDefaultSharedPreferences(context).edit { // Открываем редактор для редактирования настроек
            putString(PREF_LAST_RESULT_ID, lastResultId) // Сохраняем последний идентификатор результата
        }
    }

    // Проверка состояния опроса (включен/выключен)
    fun isPolling(context: Context): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(PREF_IS_POLLING, false) // Возвращаем состояние опроса или false по умолчанию
    }

    // Установка состояния опроса (включить/выключить)
    fun setPolling(context: Context, isOn: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context).edit { // Открываем редактор для редактирования настроек
            putBoolean(PREF_IS_POLLING, isOn) // Сохраняем состояние опроса
        }
    }
}
