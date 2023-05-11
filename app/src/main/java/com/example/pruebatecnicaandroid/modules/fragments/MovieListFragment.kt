package com.example.pruebatecnicaandroid.modules.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pruebatecnicaandroid.R
import com.example.pruebatecnicaandroid.modules.adapters.TopMoviesAdapter
import com.example.pruebatecnicaandroid.modules.models.Movies
import com.example.pruebatecnicaandroid.modules.models.MoviesNowPlayingDataClass
import com.example.pruebatecnicaandroid.retrofit.ApiServiceMovieDb
import com.example.pruebatecnicaandroid.retrofit.IMoviesDb
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException


class MovieListFragment : Fragment() {

    private val API_KEY: String = "275744c07e808de0b52c0334bf1d6adc"
    private val language: String = "en-US";
    private val page: Int = 1;
    private var context = null
    private lateinit var svSearchMovieMoviesListFrag: SearchView
    private lateinit var topMoviesAdapter: TopMoviesAdapter
    private lateinit var topMoviesAdapter2: TopMoviesAdapter
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_movie_list, container, false)
        progressDialog = ProgressDialog(requireActivity())

        svSearchMovieMoviesListFrag = rootView.findViewById(R.id.svSearchMovieMoviesListFrag)

        progresDialog()

        getMovies()

        svSearchMovieMoviesListFrag.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                println(newText)
                topMoviesAdapter.buscar(newText.toString())
                topMoviesAdapter2.buscar(newText.toString())
                return false
            }
        })

        return rootView
    }


    private fun getTopMovies() {
        ApiServiceMovieDb.getInstance().create(IMoviesDb::class.java)
            .getTopPlayingMovies(API_KEY, language, page)
            .enqueue(object : Callback<MoviesNowPlayingDataClass> {
                override fun onResponse(
                    call: Call<MoviesNowPlayingDataClass>,
                    response: Response<MoviesNowPlayingDataClass>
                ) {
                    if (response.isSuccessful) {

                        val result = response.body()
                        if (result != null) {
                            Log.d("MovieListFragment", "GetTopMovies API response successful")
                            val arrayList = ArrayList(result.results)
                            initRecyclerViewTopMovies(arrayList)
                            for (movie in result.results) {
                                Log.d("MovieListFragment", "id1: ${movie.id}")
                                Log.d("MovieListFragment", "Title1: ${movie.title}")
                                Log.d("MovieListFragment", "raitin1: ${movie.voteAverage}")
                                Log.d("MovieListFragment", "imag1: ${movie.posterPath}")
                            }
                        }
                    } else {
                        Log.e(
                            "MovieListFragment",
                            "GetTopMovies API response error: ${response.code()} - ${response.message()}"
                        )
                    }
                }

                override fun onFailure(call: Call<MoviesNowPlayingDataClass>, t: Throwable) {
                    Log.e("MovieListFragment", "GetTopMovies API response error: ${t.message}")
                }
            })
    }

    private fun getNowMovies() {
        ApiServiceMovieDb.getInstance().create(IMoviesDb::class.java)
            .getNowPlayingMovies(API_KEY, language, page)
            .enqueue(object : Callback<MoviesNowPlayingDataClass> {
                override fun onResponse(
                    call: Call<MoviesNowPlayingDataClass>,
                    response: Response<MoviesNowPlayingDataClass>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result != null) {
                            Log.d("MovieListFragment", "GetNowMovies API response successful")
                            val arrayList = ArrayList(result.results)
                            initRecyclerViewCartelera(arrayList)
                            for (movie in result.results) {
                                Log.d("MovieListFragment", "id2: ${movie.id}")
                                Log.d("MovieListFragment", "Title2: ${movie.title}")
                                Log.d("MovieListFragment", "raitin2: ${movie.voteAverage}")
                                Log.d("MovieListFragment", "imag2: ${movie.posterPath}")
                            }
                        }
                    } else {
                        Log.e(
                            "MovieListFragment",
                            "GetNowMovies API response error: ${response.code()} - ${response.message()}"
                        )
                    }
                }

                override fun onFailure(call: Call<MoviesNowPlayingDataClass>, t: Throwable) {
                    Log.e("MovieListFragment", "GetNowMovies API response error: ${t.message}")
                }
            })
    }

    private fun initRecyclerViewTopMovies(movie: ArrayList<Movies>){

        var recyclerView = view?.findViewById<RecyclerView>(R.id.rvMoviesPopulares)
        // Inicializar el adaptador
        topMoviesAdapter = TopMoviesAdapter(movie)

        recyclerView?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.adapter = topMoviesAdapter
        progressDialog?.dismiss()

    }

    private fun initRecyclerViewCartelera(movie: ArrayList<Movies>){

        var recyclerView = view?.findViewById<RecyclerView>(R.id.rvMoviesCartelera)
        // Inicializar el adaptador
        topMoviesAdapter2 = TopMoviesAdapter(movie)

        recyclerView?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.adapter = topMoviesAdapter2

    }

    // Ejecutar las dos solicitudes al mismo tiempo en diferentes hilos
    fun getMovies() = runBlocking {
        val job1 = async { getTopMovies() }
        val job2 = async { getNowMovies() }
        job1.await()
        job2.await()
    }

    private fun progresDialog () {

        progressDialog?.setMessage("Cargando...")
        progressDialog?.setCancelable(false)
        progressDialog?.show()

    }

    companion object {

    }
}