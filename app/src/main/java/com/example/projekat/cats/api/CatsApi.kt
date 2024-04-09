package com.example.projekat.cats.api

import com.example.projekat.cats.api.model.CatsApiModel
import com.example.projekat.cats.domain.Cat
import com.example.projekat.networking.retrofit
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path

interface CatsApi {

    @GET("breeds")
    suspend fun getAllCats(): List<CatsApiModel>

//    @GET("users/{id}")
//    suspend fun getUser(
//        @Path("id") userId: Int,
//    ): CatsApiModel
//
//    @GET("users/{id}/albums")
//    suspend fun getUserAlbums(
//        @Path("id") userId: Int,
//    ): List<CatsApiModel>
//
//    @DELETE("users/{id}")
//    suspend fun deleteUser()

}