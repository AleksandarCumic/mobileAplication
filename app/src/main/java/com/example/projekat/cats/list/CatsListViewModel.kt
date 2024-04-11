package com.example.projekat.cats.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projekat.cats.api.model.CatsApiModel
import com.example.projekat.cats.domain.Cat
import com.example.projekat.cats.repository.CatsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class CatsListViewModel (
    private val repository: CatsRepository = CatsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CatsListState())
    val state = _state.asStateFlow()
    private fun setState(reducer: CatsListState.() -> CatsListState) = _state.getAndUpdate(reducer)


    init {
        fetchCats()
    }

    private fun fetchCats() {
        viewModelScope.launch {
            _state.value = _state.value.copy(fetching = true)
            try {
                val cats = withContext(Dispatchers.IO) {
                    repository.fetchAllCats().map {it.allCats()}
                }
                setState { copy(cats = cats) }
            } catch (error: IOException) {
                _state.value = _state.value.copy(error = CatsListState.ListError.ListUpdateFailed(cause = error))
            } finally {
                _state.value = _state.value.copy(fetching = false)
            }
        }
    }
    private fun CatsApiModel.allCats() = Cat(
        id = this.id,
        weight = this.weight,

        name = this.name,
        alternativeNames = this.alternativeNames,
        description = this.description,
        temperament = this.temperament,
        origin = this.origin,
        lifeSpan = this.lifeSpan,
        adaptability = this.adaptability,
        affectionLevel = this.affectionLevel,
        childFriendly = this.childFriendly,
        dogFriendly = this.dogFriendly,
        energyLevel = this.energyLevel,
        grooming = this.grooming,
        healthIssues = this.healthIssues,
        intelligence = this.intelligence,
        sheddingLevel = this.sheddingLevel,
        socialNeeds = this.socialNeeds,
        strangerFriendly = this.strangerFriendly,
        vocalisation = this.vocalisation,

        rare = this.rare,
        wikipediaURL = this.wikipediaURL,
        referenceImageId = this.referenceImageId)

    fun searchCatsByName(nameQuery: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(fetching = true)
            try {
                val filteredCats = withContext(Dispatchers.IO) {
                    repository.searchCatsByName(nameQuery).map { it.allCats() }
                }
                setState { copy(cats = filteredCats) }
            } catch (error: IOException) {
                _state.value =
                    _state.value.copy(error = CatsListState.ListError.ListUpdateFailed(cause = error))
            } finally {
                _state.value = _state.value.copy(fetching = false)
            }
        }
    }

    fun clearSearch(){
        viewModelScope.launch{
            fetchCats()
        }
    }
}
