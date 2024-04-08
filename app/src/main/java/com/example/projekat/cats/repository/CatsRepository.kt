package com.example.projekat.cats.repository

import android.adservices.adid.AdId
import com.example.projekat.cats.domain.Cat
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlin.time.Duration.Companion.seconds


/**
 * Single source of truth for passwords.
 */
object CatsRepository {

    //    private var mutablePasswords = SampleData.toMutableList()
    private val cats = MutableStateFlow(listOf<Cat>())

    fun allCats(): List<Cat> = cats.value

    /**
     * Simulates api network request which downloads sample data
     * from network and updates passwords in this repository.
     */
    suspend fun fetchCats() {
        delay(2.seconds)
        cats.update { SampleData.toMutableList() }
    }

    /**
     * Simulates api network request which updates single password.
     * It does nothing. Just waits for 1 second.
     */
    suspend fun fetchCatDetails(catId: String) {
        delay(1.seconds)
    }

    /**
     * Returns StateFlow which holds all passwords.
     */
    fun observeCats(): Flow<List<Cat>> = cats.asStateFlow()

    /**
     * Returns regular flow with Cat with given catId.
     */
    fun observeCatDetails(catId: String): Flow<Cat?> {
        return observeCats()
            .map { passwords -> passwords.find { it.id == catId } }
            .distinctUntilChanged()
    }

    // Ovo sve ispod je za editor ekran koji nemam
    
    fun getCatById(id: String): Cat? {
        return cats.value.find { it.id == id }
    }

    fun deleteCat(id: String) {
        cats.update { list ->
            val index = list.indexOfFirst { it.id == id }
            if (index != -1) {
                list.toMutableList().apply { removeAt(index) }
            } else {
                list
            }
        }
    }

    fun updateOrInsertCat(id: String, data: Cat) {
        cats.update { list ->
            val index = list.indexOfFirst { it.id == id }
            if (index != -1) {
                list.toMutableList().apply {
                    this[index] = data
                }
            } else {
                list.toMutableList().apply {
                    add(data)
                }
            }
        }
    }
}
