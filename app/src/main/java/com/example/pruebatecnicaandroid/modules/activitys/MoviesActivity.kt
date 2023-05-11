package com.example.pruebatecnicaandroid.modules.activitys

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pruebatecnicaandroid.R
import com.example.pruebatecnicaandroid.modules.fragments.MovieListFragment


class MoviesActivity : AppCompatActivity() {

    private val fragmentManager = supportFragmentManager;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movies)

        callFragment()

    }

    /*LLAMO A AL FRAGMENTO EN MI
    * ACTIVIDAD Y PARA PODER MOSTRAR
    * EL CONTENIDO DE MI FRAGMENT*/
    private fun callFragment () {

        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainerMoviesActivity, MovieListFragment())
        fragmentTransaction.commit()

    }

}