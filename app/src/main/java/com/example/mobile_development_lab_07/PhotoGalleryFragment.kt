package com.example.mobile_development_lab_07

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar // Импортируем ProgressBar для индикатора загрузки
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

private const val TAG = "PhotoGalleryFragment"

class PhotoGalleryFragment : Fragment() {

    private lateinit var photoGalleryViewModel: PhotoGalleryViewModel
    private lateinit var photoRecyclerView: RecyclerView
    private lateinit var loadingIndicator: ProgressBar // Индикатор загрузки

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        photoGalleryViewModel = ViewModelProvider(this)[PhotoGalleryViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_photo_gallery, container, false)

        photoRecyclerView = view.findViewById(R.id.photo_recycler_view)
        photoRecyclerView.layoutManager = GridLayoutManager(context, 3)

        loadingIndicator = view.findViewById(R.id.loading_indicator) // Инициализация индикатора загрузки

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.fragment_photo_gallery, menu)

                val searchItem: MenuItem = menu.findItem(R.id.menu_item_search)
                val searchView = searchItem.actionView as SearchView

                searchView.apply {
                    setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(queryText: String): Boolean {
                            Log.d(TAG, "QueryTextSubmit: $queryText")

                            // Скрываем клавиатуру и сворачиваем SearchView
                            searchView.clearFocus()
                            searchItem.collapseActionView()

                            // Показываем индикатор загрузки и очищаем RecyclerView
                            loadingIndicator.visibility = View.VISIBLE
                            photoRecyclerView.adapter = null

                            // Запускаем запрос на получение фотографий
                            photoGalleryViewModel.fetchPhotos(queryText)

                            return true
                        }

                        override fun onQueryTextChange(queryText: String): Boolean {
                            Log.d(TAG, "QueryTextChange: $queryText")
                            return false
                        }
                    })
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_item_clear -> {
                        photoGalleryViewModel.fetchPhotos("")
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner)

        // Наблюдение за LiveData из ViewModel
        photoGalleryViewModel.galleryItemLiveData.observe(viewLifecycleOwner) { galleryItems ->
            // Скрываем индикатор загрузки после получения данных
            loadingIndicator.visibility = View.GONE

            // Устанавливаем адаптер для RecyclerView с новыми данными
            photoRecyclerView.adapter = PhotoAdapter(galleryItems)
        }
    }

    private class PhotoHolder(private val itemImageView: ImageView) : RecyclerView.ViewHolder(itemImageView) {
        fun bindGalleryItem(galleryItem: GalleryItem) {
            Picasso.get()
                .load(galleryItem.url)
                .placeholder(R.drawable.bill_up_close)
                .into(itemImageView)
        }
    }

    private inner class PhotoAdapter(private val galleryItems: List<GalleryItem>) : RecyclerView.Adapter<PhotoHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_gallery, parent, false) as ImageView
            return PhotoHolder(view)
        }

        override fun getItemCount(): Int = galleryItems.size

        override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
            val galleryItem = galleryItems[position]
            holder.bindGalleryItem(galleryItem)
        }
    }

    companion object {
        fun newInstance() = PhotoGalleryFragment()
    }
}
