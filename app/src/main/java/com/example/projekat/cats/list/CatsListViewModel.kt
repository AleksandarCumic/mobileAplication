package com.example.projekat.cats.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
        observeCats()
        fetchCats()
    }

    /**
     * This will observe all passwords and update state whenever
     * underlying data changes. We are using viewModelScope which
     * will cancel the subscription when view model dies.
     */
    private fun observeCats() {
        // We are launching a new coroutine
        viewModelScope.launch {
            // Which will observe all changes to our passwords
            repository.observeCats().collect {
                setState { copy(cats = it) }
            }
        }
    }

    /**
     * Fetches passwords from simulated api endpoint and
     * replaces existing passwords with "downloaded" passwords.
     */
    private fun fetchCats() {
        viewModelScope.launch {
            setState { copy(fetching = true) }
            try {
                withContext(Dispatchers.IO) {
                    repository.fetchCats()
                }
            } catch (error: IOException) {
                setState { copy(error = CatsListState.ListError.ListUpdateFailed(cause = error)) }
            } finally {
                setState { copy(fetching = false) }
            }
        }
    }
}
