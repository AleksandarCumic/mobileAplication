package com.example.projekat.cats.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projekat.cats.list.CatsDetailsUiEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.projekat.cats.repository.CatsRepository
import java.io.IOException

class CatsDetailsViewModel(
    private val catId: String,
    private val repository: CatsRepository = CatsRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(CatsDetailsState(catId = catId))
    val state = _state.asStateFlow()
    private fun setState(reducer: CatsDetailsState.() -> CatsDetailsState) =
        _state.getAndUpdate(reducer)


    private val events = MutableSharedFlow<CatsDetailsUiEvent>()
    fun setEvent(event: CatsDetailsUiEvent) {
        viewModelScope.launch {
            events.emit(event)
        }
    }

    init {
        observeCatDetails()
        fetchCatDetails()
    }

    /**
     * Observes password details data from our local data
     * and updates the state.
     */
    private fun observeCatDetails() {
        viewModelScope.launch {
            repository.observeCatDetails(catId = catId)
                .filterNotNull()
                .collect {
                    setState { copy(data = it) }
                }
        }
    }

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
            setState { copy(fetching = true) }
            try {
                withContext(Dispatchers.IO) {
                    repository.fetchCatDetails(catId = catId)
                }
            } catch (error: IOException) {
                setState {
                    copy(error = CatsDetailsState.DetailsError.DataUpdateFailed(cause = error))
                }
            } finally {
                setState { copy(fetching = false) }
            }
        }
    }
}
