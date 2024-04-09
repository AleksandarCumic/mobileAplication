package com.example.projekat.cats.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CatsApiModel(
    //@SerialName("id")
    val id: String,
    @SerialName("name")
    val nameofTheBreed: String,
//    //@SerialName("alt_names")
//    val alternateName: String,
//    //@SerialName("description")
//    var descriptionLong: String,
//    var descriptionShort: String,
    //@SerialName("temperament") 
//    val temperamentList: List<String>,
//    val temperamentThree: List<String> = temperamentList.take(3),
    @SerialName("life_span") val averageAge: String,
)

