package com.example.mobile_development_lab_07.api

import com.example.mobile_development_lab_07.Tag
import com.google.gson.annotations.SerializedName

class PhotoResponse {
    @SerializedName("id") val id: String = ""
    @SerializedName("title") val title: Title? = null
    @SerializedName("owner") val owner: Owner? = null
    @SerializedName("tags") val tags: Tags? = null
    @SerializedName("dates") val dates: Dates? = null // Вложенный класс для дат
    @SerializedName("urls") val urlS: UrlS? = null // Вложенный класс для дат

//    var galleryItem: GalleryItem = GalleryItem(
//        id = id,
//        title = title,
//        ownerRealName = ownerRealName,
//        tags = tags,
//        dateTaken = dateTaken,
//        url = url
//    )


//    // Метод для преобразования в GalleryItem
//    var galleryItem: GalleryItem = GalleryItem(
//            id = id,
//            title = title,
//            description = description,
//            ownerName = owner?.username ?: "", // Получаем имя владельца
//            dateTaken = dates?.taken ?: "", // Получаем дату
//            url = url // Укажите здесь логику получения URL, если необходимо
//    )
}


//// Вложенные классы для представления дополнительных данных
data class Owner(
//    @SerializedName("nsid") val nsid: String,
//    @SerializedName("username") val username: String,
    @SerializedName("realname") val realname: String,
//    @SerializedName("location") val location: String
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


//
//data class Dates(
//    @SerializedName("posted") val posted: String,
//    @SerializedName("taken") val taken: String
//)