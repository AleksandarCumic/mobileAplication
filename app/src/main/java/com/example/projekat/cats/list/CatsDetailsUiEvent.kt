package com.example.projekat.cats.list

sealed class CatsDetailsUiEvent {
    data class RequestCatDelete(val catId: String) : CatsDetailsUiEvent()
}