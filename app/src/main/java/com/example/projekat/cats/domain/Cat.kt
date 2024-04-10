package com.example.projekat.cats.domain

data class Cat(
    val id: String,
    val name: String,
    val alternativeNames: String,
    var description: String,
//    var descriptionShort: String = description.substring(0, 250),
    var temperament: String,
//    var temperamentThree: List<String>,
//    var tmp: List<String>,
    // dodati sliku, saznati kako
    val origin: String,
    val lifeSpan: String,
    val imageUrl: String,
    val weight: String,
    val wikipediaURL: String,
    val rare: Int,

    val adaptability: Int,
    val affectionLevel: Int,

)
