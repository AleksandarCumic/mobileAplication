package com.example.projekat.cats.api

import com.example.projekat.cats.api.model.CatsApiModel
import com.example.projekat.cats.api.model.ImageModel
import com.example.projekat.cats.domain.Cat
import com.example.projekat.networking.retrofit
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path

interface CatsApi {

    @GET("breeds")
    suspend fun getAllCats(): List<CatsApiModel>

    @GET("breeds/{id}")
    suspend fun getCat(
        @Path("id") catId: String,
    ): CatsApiModel

    @GET("images/{id}")
    suspend fun getImage(
        @Path("id") imageId: String,
    ): ImageModel


//
//    @GET("users/{id}/albums")
//    suspend fun getUserAlbums(
//        @Path("id") userId: Int,
//    ): List<CatsApiModel>
//
//    @DELETE("users/{id}")
//    suspend fun deleteUser()

}