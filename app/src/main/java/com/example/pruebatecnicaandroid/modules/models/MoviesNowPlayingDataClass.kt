package com.example.pruebatecnicaandroid.modules.models

import com.google.gson.annotations.SerializedName

data class MoviesNowPlayingDataClass(

    val page: Int,
    val results: List<Movies>,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_results")
    val totalResults: Int

)

