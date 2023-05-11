package com.example.pruebatecnicaandroid.retrofit

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiServiceMovieDb {

    val baseUrl = "https://api.themoviedb.org/3/movie/"

    fun getInstance(): Retrofit {
        return Retrofit.Builder().baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

}

object ApiServiceMovieDbImage {

    val baseUrl = "https://image.tmdb.org/t/p/w500/"
    val gson = GsonBuilder().setLenient().create()

    fun getInstance(): Retrofit {
        return Retrofit.Builder().baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

}