package com.example.projekat.cats.domain

import com.example.projekat.cats.api.model.Weight

data class Cat(
    val id: String,
    val name: String,
    val alternativeNames: String,
    var description: String,
    var temperament: String,

    val origin: String,
    val lifeSpan: String,
    val weight: Weight,

    val adaptability: Int,
    val affectionLevel: Int,
    val childFriendly: Int,
    val dogFriendly: Int,
    val energyLevel: Int,
    val grooming: Int,
    val healthIssues: Int,
    val intelligence: Int,
    val sheddingLevel: Int,
    val socialNeeds: Int,
    val strangerFriendly: Int,
    val vocalisation: Int,

    val rare: Int,
    val wikipediaURL: String,

    val referenceImageId: String,

)
