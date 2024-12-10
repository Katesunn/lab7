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
    fun getGalleryItemInfo(): GalleryItemInfo = GalleryItemInfo(
        id = id,
        title = title?.content ?: "",
        tags = tags?.tag ?: emptyList(),
        ownerRealName = owner?.realname ?: "",
        dateTaken = dates?.taken ?: "",
        page_url = urlS?.urlList?.firstOrNull()?.content ?: "" // Получаем первый URL из списка
   )
}

data class GalleryItemInfo(
    var id: String = "",
    var title: String = "",
    var tags: List<Tag> = emptyList(),
    var ownerRealName: String = "",
    var dateTaken: String = "",
    var page_url: String = "") {
    fun getGalleryItem(): GalleryItem = GalleryItem(
        id=id,
        title=title,
        ownerRealName = ownerRealName,
        dateTaken = dateTaken,
        page_url = page_url
    )
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