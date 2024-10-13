package com.example.mobile_development_lab_07

import android.content.Context
import androidx.core.content.edit // Импортируем функцию edit из androidx.core.content
import androidx.preference.PreferenceManager

private const val PREF_SEARCH_QUERY = "searchQuery"

object QueryPreferences {
    fun getStoredQuery(context: Context): String {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getString(PREF_SEARCH_QUERY, "") ?: ""
    }

    fun setStoredQuery(context: Context, query: String) {
        PreferenceManager.getDefaultSharedPreferences(context).edit {
            putString(PREF_SEARCH_QUERY, query) // Используем лямбда-выражение для редактирования
        }
    }
}
