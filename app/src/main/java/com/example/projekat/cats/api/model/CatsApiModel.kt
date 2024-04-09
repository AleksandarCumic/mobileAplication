package com.example.projekat.cats.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CatsApiModel(
//    @SerialName("id")
    val id: String,
//    @SerialName("name")
    val name: String,
//    @SerialName("alt_names")
//    val alt_names: String,
//    @SerialName("description")
    var description: String,
//    var descriptionShort: String,
//    @SerialName("temperament")
    val temperament: String,
//    val temperamentThree: List<String>,
//    var tmp: List<String>
//    @SerialName("life_span") val averageAge: String,
)