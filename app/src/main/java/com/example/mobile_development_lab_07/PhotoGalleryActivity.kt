package com.example.mobile_development_lab_07

import android.os.Bundle
import android.os.StrictMode
import androidx.appcompat.app.AppCompatActivity


class PhotoGalleryActivity :
    AppCompatActivity() {
    override fun onCreate(savedInstanceState:
                          Bundle?) {
        super.onCreate(savedInstanceState)
        StrictMode.enableDefaults()
        setContentView(R.layout.activity_photo_gallery)
        val isFragmentContainerEmpty =
            savedInstanceState == null
        if (isFragmentContainerEmpty) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragmentContainer,
                    PhotoGalleryFragment.newInstance())
                .commit()
        }
    }
}