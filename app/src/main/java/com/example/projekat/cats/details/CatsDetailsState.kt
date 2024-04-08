package com.example.projekat.cats.details

import com.example.projekat.cats.domain.Cat

data class CatsDetailsState(
    val catId: String,
    val fetching: Boolean = false,
    val data: Cat? = null,
    val error: DetailsError? = null,
) {
    sealed class DetailsError {
        data class DataUpdateFailed(val cause: Throwable? = null) : DetailsError()
    }
}
