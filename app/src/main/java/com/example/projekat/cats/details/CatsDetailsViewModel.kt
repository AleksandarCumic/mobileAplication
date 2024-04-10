package com.example.projekat.cats.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projekat.cats.api.model.CatsApiModel
import com.example.projekat.cats.domain.Cat
import com.example.projekat.cats.list.CatsListState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import com.example.projekat.cats.repository.CatsRepository
import java.io.IOException

// OVO JE ZA JEDNU MACKU

class CatsDetailsViewModel(
    private val catId: String,
    private val repository: CatsRepository = CatsRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(CatsDetailsState(catId = catId))
    val state = _state.asStateFlow()
    private fun setState(reducer: CatsDetailsState.() -> CatsDetailsState) =
        _state.getAndUpdate(reducer)

    init {
//        observeCatDetails()
        fetchCatDetails()
    }

    /**
     * Observes password details data from our local data
     * and updates the state.
     */
//    private fun observeCatDetails() {
//        viewModelScope.launch {
//            repository.observeCatDetails(catId = catId)
//                .filterNotNull()
//                .collect {
//                    setState { copy(data = it) }
//                }
//        }
//    }

    /**
     * Triggers updating local password details by calling "api"
     * to get latest data and update underlying local data we use.
     *
     * Note that we are not updating the state here. This is done
     * from observeCatDetails(). Both functions are using
     * the single source of truth (CatsRepository) so we can
     * do this. If we break this principle, the app will stop working.
     */
    private fun fetchCatDetails() {
        viewModelScope.launch {
            _state.value = _state.value.copy(fetching = true)
            try {
                val catDetails = repository.fetchAllCats().find { it.id == catId } // Pronalazi mačku sa datim catId
                catDetails?.let {
                    val cat = it.specificCat()
                    setState { copy(data = cat) } // Postavlja detalje mačke u stanje
                }
            } catch (error: IOException) {
                _state.value = _state.value.copy(error = CatsListState.ListError.ListUpdateFailed(cause = error))
            } finally {
                _state.value = _state.value.copy(fetching = false)
            }
        }
    }


    private fun CatsApiModel.specificCat() = Cat(
        id = this.id,
        name = this.name,
        alternativeNames = this.alternativeNames,
        description = this.description,
        temperament = this.temperament,
        origin = this.origin,
        lifeSpan = this.lifeSpan,
        adaptability = this.adaptability,
        affectionLevel = this.affectionLevel,
        imageUrl = this.image.url,
        rare = this.rare,
        weight = this.weight.metric,
        wikipediaURL = this.wikipediaURL
    )
}
