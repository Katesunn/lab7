package com.example.mobile_development_lab_07.ui.main

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile_development_lab_07.FavoritesActivity
import com.example.mobile_development_lab_07.GalleryItem
import com.example.mobile_development_lab_07.R
import com.example.mobile_development_lab_07.Tag
import com.example.mobile_development_lab_07.db.GalleryItemDatabase
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoritesItemHolder(itemView: View, private val context: Context, private val adapter: FavoritesItemAdapter) : RecyclerView.ViewHolder(itemView) {
    private val tag = "FavoritesItemHolder"
    private val imageViewThumbnail: ImageView = itemView.findViewById(R.id.image_view_thumbnail)
    private val textViewTitle: TextView = itemView.findViewById(R.id.text_view_title)
    private val textViewOwnerDate: TextView = itemView.findViewById(R.id.text_view_owner_date)
    private val textViewTags: TextView = itemView.findViewById(R.id.text_view_tags)
    private val buttonDelete: Button = itemView.findViewById(R.id.button_delete_item)

    fun bind(galleryItem: GalleryItem) {
        // Установка данных в элементы представления
        textViewTitle.text = galleryItem.title
        textViewOwnerDate.text = "${galleryItem.ownerRealName} - ${galleryItem.dateTaken}"

        // Загрузка изображения с помощью Picasso
        Picasso.get()  // Используем библиотеку Picasso для загрузки изображения
//            .load(galleryItem.url)  // Загружаем изображение по URL из объекта GalleryItem
            .load(galleryItem.url)  // Загружаем изображение по URL из объекта GalleryItem
            .placeholder(R.drawable.bill_up_close)  // Устанавливаем изображение-заполнитель во время загрузки
            .into(imageViewThumbnail)  // Загружаем изображение в ImageView

        // Получение тегов и установка их в текстовое поле
        // Предполагается, что у вас есть метод для получения тегов из базы данных или передача тегов в адаптер
        CoroutineScope(Dispatchers.IO).launch {
            val tags =
                getTagsForGalleryItem(galleryItem.id) // Метод для получения тегов (реализуйте его)
            withContext(Dispatchers.Main) {
                // Обновите UI или выполните другие действия с полученными тегами
                textViewTags.text =
                    tags.joinToString(", ") { it.title } // Преобразуем список тегов в строку через запятую
                Log.i(TAG, "Tags: $tags")
            }
        }

        // Установка обработчика клика на кнопку удаления
        buttonDelete.setOnClickListener {
            val db = GalleryItemDatabase.getDatabase(context = context)
            if (db != null) {
                val galleryItemDao = db.galleryItemDao()

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        galleryItemDao.deleteGalleryItem(galleryItem.id)
                        val updatedItems = galleryItemDao.getAllGalleryItems() // Получаем обновленный список
                        withContext(Dispatchers.Main) {
                            val position = adapter.items.indexOf(galleryItem) // Получаем позицию элемента
                            adapter.items.removeAt(position) // Удаляем элемент из списка
                            adapter.notifyItemRemoved(position) // Уведомляем адаптер об удалении элемента
                            Toast.makeText(context, "Запись удалена", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
//                        Toast.makeText(context, "Ошибка при сохранении записи", Toast.LENGTH_SHORT).show()
                            Log.e(tag, "${e.message}")
                        }
                    }
                }
            }
        // Логика удаления элемента из базы данных и обновления списка (например, вызов метода в адаптере)
            // ...
        }
    }

    private suspend fun getTagsForGalleryItem(galleryItemId: String): List<Tag> {
        val db = GalleryItemDatabase.getDatabase(context = context)
        return if (db != null) {
            val galleryItemDao = db.galleryItemDao()
            try {
                galleryItemDao.getTagsForGalleryItem(galleryItemId)
            } catch (e: Exception) {
                Log.e(tag, "${e.message}")
                emptyList() // Возвращаем пустой список в случае ошибки
            }
        } else {
            emptyList() // Возвращаем пустой список, если база данных не инициализирована
        }
    }
}
