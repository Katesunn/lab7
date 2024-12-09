package com.example.mobile_development_lab_07.api

import com.example.mobile_development_lab_07.GalleryItem
import com.example.mobile_development_lab_07.Tag
import com.google.gson.annotations.SerializedName

class PhotoResponse {
    @SerializedName("id") val id: String = ""
    @SerializedName("title") val title: Title? = null
    @SerializedName("owner") val owner: Owner? = null
    @SerializedName("tags") val tags: Tags? = null
    @SerializedName("dates") val dates: Dates? = null // Вложенный класс для дат
    @SerializedName("urls") val urlS: UrlS? = null // Вложенный класс для дат

    // Метод для преобразования в GalleryItem
    fun getGalleryItem(): GalleryItem {
        return GalleryItem(
            id = id,
            title = title?.content ?: "",
            tags = tags?.tag ?: emptyList(),
            ownerRealName = owner?.realname ?: "",
            dateTaken = dates?.taken ?: "",
            url = urlS?.urlList?.firstOrNull()?.content ?: "" // Получаем первый URL из списка
        )
    }
}


//// Вложенные классы для представления дополнительных данных
data class Owner(
    @SerializedName("realname") val realname: String,
)

data class Tags(
    @SerializedName("tag") val tag: List<Tag>
)

data class Title(
    @SerializedName("_content") val content: String
)

data class Dates(
    @SerializedName("taken") val taken: String
)

data class UrlS(
    @SerializedName("url") val urlList: List<Url>
)

data class Url(
    @SerializedName("_content") val content: String
)