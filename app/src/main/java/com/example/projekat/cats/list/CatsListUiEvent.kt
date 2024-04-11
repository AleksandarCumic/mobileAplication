package com.example.projekat.cats.list

sealed class CatsListUiEvent {

    data class SearchQueryChanged(val query: String) : CatsListUiEvent()
    object ClearSearch : CatsListUiEvent()

}