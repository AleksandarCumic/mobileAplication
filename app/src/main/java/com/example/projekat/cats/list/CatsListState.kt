package com.example.projekat.cats.list

import com.example.projekat.cats.domain.Cat

data class CatsListState(
    val fetching: Boolean = false,
    val cats: List<Cat> = emptyList(),
    val error: ListError? = null
) {
    sealed class ListError {
        data class ListUpdateFailed(val cause: Throwable? = null) : ListError()
    }
}
