package com.example.projekat.cats.domain

data class Cat(
    val id: String,
    val name: String,
//    val alt_names: String,
    var description: String,
//    var descriptionShort: String = description.substring(0, 250),
    var temperament: String,
//    var temperamentThree: List<String>,
//    var tmp: List<String>,
    // dodati sliku, saznati kako
//    var countriesOfOrigin: List<String>,
//    val averageAge: String,
//    val averageWeight: String,
//    val averageHeight: String,
//    val rarity: Boolean,
//    val link: String
) {
//    init {
//
//         tmp = temperament.split(",")
//
//        if (tmp.size > 3) {
//            val shuffledTemperaments = tmp.toMutableList().apply { shuffle() }
//            temperamentThree = shuffledTemperaments.subList(0, 3)
//        }
//    }
}
