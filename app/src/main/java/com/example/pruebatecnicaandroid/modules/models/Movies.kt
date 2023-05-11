package com.example.pruebatecnicaandroid.modules.models

import com.google.gson.annotations.SerializedName

data class Movies(
    val title: String,
                  @SerializedName("vote_average")
                  val voteAverage: Double,
                  @SerializedName("poster_path")
                  val posterPath: String?)
