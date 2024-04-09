package com.example.projekat.cats.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
//        observeCats()
        fetchCats()
    }

    /**
     * This will observe all passwords and update state whenever
     * underlying data changes. We are using viewModelScope which
     * will cancel the subscription when view model dies.
     */
//    private fun observeCats() {
//        viewModelScope.launch {
//            repository.observeCats().collect { cats ->
//                _state.value = cats?.let { _state.value.copy(cats = it) }!!
//            }
//        }
//    }

    /**
     * Fetches passwords from simulated api endpoint and
     * replaces existing passwords with "downloaded" passwords.
     */
    private fun fetchCats() {
        viewModelScope.launch {
            _state.value = _state.value.copy(fetching = true)
            try {
                val cats = withContext(Dispatchers.IO) {
                    repository.fetchAllCats().map {it.asCats()}
                }
                setState { copy(cats = cats) }
            } catch (error: IOException) {
                _state.value = _state.value.copy(error = CatsListState.ListError.ListUpdateFailed(cause = error))
            } finally {
                _state.value = _state.value.copy(fetching = false)
            }
        }
    }

    private fun CatsApiModel.asCats() = Cat(
        id = this.id,
        name = this.name,
//        alt_names = this.alt_names,
//        description = this.description,
//        descriptionShort = this.descriptionShort,
//        temperament = this.temperament,
//        temperamentThree = this.temperamentThree
    )
}
