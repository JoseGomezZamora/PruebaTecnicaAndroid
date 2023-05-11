package com.example.pruebatecnicaandroid.retrofit

import android.graphics.Bitmap
import com.example.pruebatecnicaandroid.modules.models.DetailMoviesDataClass
import com.example.pruebatecnicaandroid.modules.models.MoviesNowPlayingDataClass
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface IMoviesDb {

    @GET("now_playing")
    fun getNowPlayingMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("page") page: Int
    ): Call<MoviesNowPlayingDataClass>

    @GET("top_rated")
    fun getTopPlayingMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("page") page: Int
    ): Call<MoviesNowPlayingDataClass>

    @GET("{movie_id}")
    fun getMovieDetails(@Path("movie_id") movieId: Int, @Query("api_key") apiKey: String, @Query("language") language: String): Call<DetailMoviesDataClass>

    @GET
    @Streaming
    fun getImage(@Url imageUrl: String): Call<ResponseBody>

}