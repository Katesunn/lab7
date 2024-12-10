package com.example.mobile_development_lab_07.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile_development_lab_07.GalleryItem
import com.example.mobile_development_lab_07.R
import com.example.mobile_development_lab_07.db.GalleryItemDatabase
import com.example.mobile_development_lab_07.db.GalleryItemDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


const val TAG = "FavoritesFragment"

class FavoritesFragment : Fragment(R.layout.fragment_favorites) {

    private lateinit var recyclerViewFavorites: RecyclerView
    private lateinit var galleryItemDao: GalleryItemDao

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewFavorites = view.findViewById(R.id.recycler_view_favorites)

        // Получаем доступ к базе данных и DAO
        val db = GalleryItemDatabase.getDatabase(requireContext())
        galleryItemDao = db?.galleryItemDao() ?: throw IllegalStateException("Database not initialized")

        // Устанавливаем LayoutManager и адаптер для RecyclerView
        recyclerViewFavorites.layoutManager = LinearLayoutManager(context)

        // Загружаем данные из базы данных в фоновом потоке

        CoroutineScope(Dispatchers.IO).launch {
            val galleryItems = galleryItemDao.getAllGalleryItems() as MutableList<GalleryItem>
            withContext(Dispatchers.Main) {
                recyclerViewFavorites.adapter = FavoritesItemAdapter(galleryItems, context = requireContext()) { galleryItem ->
                    // Обработка нажатия на элемент списка (переход в браузер)
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(galleryItem.page_url))
                    startActivity(intent)
                }
            }
        }

        // Получаем ссылку на MenuHost для добавления меню в фрагменте
        val menuHost: MenuHost = requireActivity()
        // Добавляем MenuProvider для управления меню в этом фрагменте
        menuHost.addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.fragment_favorites, menu) // Инфляция меню из ресурса
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_delete_all -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            galleryItemDao.deleteAllGalleryItems()
                            // Перезагружаем данные после удаления
                            val updatedItems = galleryItemDao.getAllGalleryItems() as MutableList<GalleryItem>
                            withContext(Dispatchers.Main) {
                                recyclerViewFavorites.adapter = FavoritesItemAdapter(
                                    updatedItems,
                                    context = requireContext()
                                ) { galleryItem ->
                                    val intent =
                                        Intent(Intent.ACTION_VIEW, Uri.parse(galleryItem.page_url))
                                    startActivity(intent)
                                }
                            }
                        }
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner)
    }
    companion object {
        fun newInstance() = FavoritesFragment()  // Создание нового экземпляра фрагмента
    }
}
