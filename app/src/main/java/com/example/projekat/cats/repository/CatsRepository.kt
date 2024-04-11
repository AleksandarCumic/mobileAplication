package com.example.projekat.cats.repository

import com.example.projekat.cats.api.CatsApi
import com.example.projekat.cats.api.model.CatsApiModel
import com.example.projekat.cats.api.model.ImageModel
import com.example.projekat.cats.domain.Cat
import com.example.projekat.networking.retrofit
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlin.time.Duration.Companion.seconds


/**
 * Single source of truth for passwords.
 */
object CatsRepository {

    private val cats = MutableStateFlow<List<CatsApiModel>?>(null)
    private val catsApi: CatsApi = retrofit.create(CatsApi::class.java)

    suspend fun saveCats(cat: CatsApiModel){
        cats.value = cats.value.orEmpty() + cat
    }
    suspend fun fetchAllCats(): List<CatsApiModel> = catsApi.getAllCats()

    /**
     * Simulates api network request which downloads sample data
     * from network and updates passwords in this repository.
     */

    suspend fun fetchCatDetails(catId: String): CatsApiModel = catsApi.getCat(catId = catId)

    suspend fun fetchImage(imageId: String): ImageModel = catsApi.getImage(imageId = imageId)

    suspend fun searchCatsByName(nameQuery: String): List<CatsApiModel> {
        return catsApi.getSearch(nameQuery)
    }


    // Ovo sve ispod je za editor ekran koji nemam
    

}
