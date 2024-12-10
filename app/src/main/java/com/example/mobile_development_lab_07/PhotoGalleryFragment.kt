// Указываем пакет, в котором находится наш класс
package com.example.mobile_development_lab_07

import android.content.Intent
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
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.mobile_development_lab_07.db.GalleryItemDatabase
import com.squareup.picasso.Picasso // Библиотека для загрузки изображений
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

private const val TAG = "PhotoGalleryFragment" // Тег для логирования
private const val POLL_WORK = "POLL_WORK" // Идентификатор для работы опроса

class PhotoGalleryFragment : Fragment() {

    private lateinit var photoGalleryViewModel: PhotoGalleryViewModel // ViewModel для управления данными галереи
    private lateinit var photoRecyclerView: RecyclerView // RecyclerView для отображения фотографий
    private lateinit var loadingIndicator: ProgressBar // Индикатор загрузки

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Вызов метода родительского класса для выполнения стандартной инициализации

        // Инициализация ViewModel, которая будет использоваться для получения данных о фотографиях
        photoGalleryViewModel = ViewModelProvider(this)[PhotoGalleryViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Инфляция макета фрагмента из XML-файла
        val view = inflater.inflate(R.layout.fragment_photo_gallery, container, false)

        // Инициализация RecyclerView и установка менеджера компоновки в виде сетки с 3 столбцами
        photoRecyclerView = view.findViewById(R.id.photo_recycler_view)
        photoRecyclerView.layoutManager = GridLayoutManager(context, 3)

        // Инициализация индикатора загрузки из макета фрагмента
        loadingIndicator = view.findViewById(R.id.loading_indicator)

        return view // Возвращаем инфлированный вид фрагмента
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState) // Вызов метода родительского класса

        // Получаем ссылку на MenuHost для добавления меню в фрагменте
        val menuHost: MenuHost = requireActivity()

        // Добавляем MenuProvider для управления меню в этом фрагменте
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.fragment_photo_gallery, menu) // Инфляция меню из ресурса

                val searchItem: MenuItem = menu.findItem(R.id.menu_item_search) // Поиск элемента меню по ID
                val searchView = searchItem.actionView as SearchView // Получаем SearchView из элемента меню

                searchView.apply {
                    setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(queryText: String): Boolean {
                            Log.d(TAG, "QueryTextSubmit: $queryText") // Логируем текст запроса

                            // Скрываем клавиатуру и сворачиваем SearchView после отправки запроса
                            searchView.clearFocus()
                            searchItem.collapseActionView()

                            // Показываем индикатор загрузки и очищаем RecyclerView перед новым запросом
                            loadingIndicator.visibility = View.VISIBLE
                            photoRecyclerView.adapter = null

                            // Запускаем запрос на получение фотографий с введенным текстом запроса
                            photoGalleryViewModel.fetchPhotos(queryText)

                            return true // Указываем, что событие обработано успешно
                        }

                        override fun onQueryTextChange(queryText: String): Boolean {
                            Log.d(TAG, "QueryTextChange: $queryText") // Логируем изменения текста запроса
                            return false // Возвращаем false, если не обрабатываем изменения текста здесь
                        }
                    })
                }

                val toggleItem = menu.findItem(R.id.menu_item_toggle_polling) // Получаем элемент меню для переключения опроса
                val isPolling = QueryPreferences.isPolling(requireContext()) // Проверяем текущее состояние опроса

                // Устанавливаем заголовок элемента меню в зависимости от состояния опроса (включен/выключен)
                val toggleItemTitle = if (isPolling) {
                    R.string.stop_polling
                } else {
                    R.string.start_polling
                }
                toggleItem.setTitle(toggleItemTitle) // Устанавливаем заголовок элемента меню
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_item_clear -> {
                        photoGalleryViewModel.fetchPhotos("") // Запрос на получение всех фотографий (очистка поиска)
                        true
                    }
                    R.id.menu_item_toggle_polling -> {
                        val isPolling = QueryPreferences.isPolling(requireContext())
                        if (isPolling) {
                            WorkManager.getInstance(requireContext()).cancelUniqueWork(POLL_WORK) // Отменяем работу опроса
                            QueryPreferences.setPolling(requireContext(), false)
                        } else {
                            val constraints = Constraints.Builder()
                                .setRequiredNetworkType(NetworkType.UNMETERED)
                                .build()

                            val periodicRequest = PeriodicWorkRequest
                                .Builder(PollWorker::class.java, 15, TimeUnit.MINUTES)
                                .setConstraints(constraints)
                                .build()

                            WorkManager.getInstance(requireContext())
                                .enqueueUniquePeriodicWork(POLL_WORK, ExistingPeriodicWorkPolicy.KEEP, periodicRequest)

                            QueryPreferences.setPolling(requireContext(), true)
                        }
                        activity?.invalidateOptionsMenu() // Обновляем меню активности после изменения состояния опроса
                        true
                    }
                    R.id.menu_item_favorites -> {
                        Log.d(TAG, "Favorites clicked") // Логируем текст запроса
                        // Переход в FavoritesActivity
                        val intent = Intent(requireContext(), FavoritesActivity::class.java)
                        startActivity(intent)
                        true
                    }
                    else -> false // Возвращаем false для элементов меню, которые не обрабатываются
                }
            }
        }, viewLifecycleOwner)

        // Наблюдение за LiveData из ViewModel для обновления UI при изменении данных о фотографиях
        photoGalleryViewModel.galleryItemLiveData.observe(viewLifecycleOwner) { galleryItems ->
            loadingIndicator.visibility = View.GONE // Скрываем индикатор загрузки после получения данных

            photoRecyclerView.adapter = PhotoAdapter(galleryItems) // Устанавливаем адаптер для RecyclerView с новыми данными
        }
    }

    private class PhotoHolder(private val itemImageView: ImageView) : RecyclerView.ViewHolder(itemImageView) {
        fun bindGalleryItem(galleryItem: GalleryItem) {
            Picasso.get()  // Используем библиотеку Picasso для загрузки изображения
                .load(galleryItem.url)  // Загружаем изображение по URL из объекта GalleryItem
                .placeholder(R.drawable.bill_up_close)  // Устанавливаем изображение-заполнитель во время загрузки
                .into(itemImageView)  // Загружаем изображение в ImageView
        }
    }

    // Здесь происходит наполнение карточки изображения (list_item_gallery)
    private inner class PhotoAdapter(private val galleryItems: List<GalleryItem>) : RecyclerView.Adapter<PhotoHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_gallery, parent, false) as ImageView  // Инфляция макета элемента списка и преобразование в ImageView
            return PhotoHolder(view)  // Возвращаем новый экземпляр PhotoHolder с инфлированным представлением
        }

        override fun getItemCount(): Int = galleryItems.size  // Возвращаем количество элементов в списке

        override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
            val galleryItem = galleryItems[position]  // Получаем элемент галереи по позиции
            holder.bindGalleryItem(galleryItem)  // Привязываем элемент к держателю представления
            // устанавливаем обработчик нажатия
            holder.itemView.setOnClickListener {
                // Обрабатываем нажатие

                val photoId = galleryItem.id // Получаем ID фотографии
                Log.i(TAG, galleryItem.url)
                photoGalleryViewModel.fetchPhotoInfo(photoId).observe(viewLifecycleOwner) { galleryItemInfo ->
                    // Обновите UI с полученной информацией о фотографии
                    if (galleryItemInfo != null) {
                        Log.d(TAG, "Clicked: ${galleryItemInfo.id}") // Логируем текст запроса

                        val db = GalleryItemDatabase.getDatabase(context = context)
                        if (db != null){
                            val galleryItemDao = db.galleryItemDao()

                            CoroutineScope(Dispatchers.IO).launch {
                                try{
                                    // Сохраняем GalleryItem
                                    val fetchedGalleryItem = galleryItemInfo.getGalleryItem()
                                    fetchedGalleryItem.url = galleryItem.url
                                    galleryItemDao.insertGalleryItem(fetchedGalleryItem)
                                    val tagsList = galleryItemInfo.tags
                                    // Сохраняем теги
                                    galleryItemDao.insertTags(tagsList)

                                    // Создаем связи между GalleryItem и Tags
                                    val crossRefs = tagsList.map { tag ->
                                        GalleryItemTagCrossRef(
                                            galleryItemId = galleryItem.id,
                                            tagId = tag.id
                                        )
                                    }
                                    crossRefs.forEach { crossRef ->
                                        galleryItemDao.insertGalleryItemTagCrossRef(crossRef)
                                    }

                                    // Уведомляем об успешном сохранении
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(
                                            context,
                                            "Запись успешно сохранена!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
//                                val galleryItems : List<GalleryItem> = galleryItemDao.getAllGalleryItems()
//                                Log.i(TAG, "$galleryItems")
                                }
                                catch (e: Exception){
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(context, "Ошибка при сохранении записи", Toast.LENGTH_SHORT).show()
                                        Log.e(TAG, "${e.message}")
                                    }
                                }
                            }

                        }
                    }
                    else{
                        Log.e(TAG, "Bruh")
                    }
                }
            }
            // устанавливаем высоту ImageView равной его ширине
            holder.itemView.post {
                val width = holder.itemView.width // Получаем ширину ImageView
                holder.itemView.layoutParams.height = width // Устанавливаем высоту равной ширине
                holder.itemView.requestLayout() // Запрашиваем перерисовку
            }
        }
    }

    companion object {
        fun newInstance() = PhotoGalleryFragment()  // Создание нового экземпляра фрагмента
    }
}
