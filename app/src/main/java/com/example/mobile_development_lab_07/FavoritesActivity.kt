package com.example.mobile_development_lab_07

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mobile_development_lab_07.ui.main.FavoritesFragment

class FavoritesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, FavoritesFragment.newInstance())
                .commitNow()
        }
    }
}