package com.example.mobile_development_lab_07.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile_development_lab_07.GalleryItem
import com.example.mobile_development_lab_07.R
import com.squareup.picasso.Picasso

class FavoritesItemAdapter(
    var items: MutableList<GalleryItem>,
    private val context: Context, // Передаем контекст в адаптер
    private val onClickListener: (GalleryItem) -> Unit // Лямбда-функция для обработки кликов по элементам списка
) : RecyclerView.Adapter<FavoritesItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_favorites, parent, false)
        return FavoritesItemHolder(view, context, this)
    }

    override fun onBindViewHolder(holder: FavoritesItemHolder, position: Int) {
        val item = items[position]

        holder.bind(item)

        // Устанавливаем обработчик клика на элемент списка
        holder.itemView.setOnClickListener { onClickListener(item) }

    }

    override fun getItemCount(): Int = items.size

//    // Метод для обновления данных в адаптере
//    fun updateData(newItems: List<GalleryItem>) {
//        items.clear()
//        items.addAll(newItems)
//        notifyDataSetChanged() // Уведомляем адаптер о том, что данные изменились
//    }
}
