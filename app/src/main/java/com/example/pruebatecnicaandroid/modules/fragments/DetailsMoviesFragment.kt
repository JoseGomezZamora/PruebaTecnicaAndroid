package com.example.pruebatecnicaandroid.modules.fragments

import android.app.ProgressDialog
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.pruebatecnicaandroid.R
import com.example.pruebatecnicaandroid.modules.models.DetailMoviesDataClass
import com.example.pruebatecnicaandroid.retrofit.ApiServiceMovieDb
import com.example.pruebatecnicaandroid.retrofit.ApiServiceMovieDbImage
import com.example.pruebatecnicaandroid.retrofit.IMoviesDb
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DetailsMoviesFragment : Fragment() {

    private val API_KEY: String = "275744c07e808de0b52c0334bf1d6adc"
    private val language: String = "en-US";
    private var movieId: Int = 0;

    private lateinit var tvTitleMovieDetailMov: TextView
    private lateinit var tvVoteAverageDetailMov: TextView
    private lateinit var tvOverViewDetailMov: TextView
    private lateinit var ivBackdropPathDetMov: ImageView
    private lateinit var ivRegresarAtras: ImageView
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_details_movies, container, false)
        progressDialog = ProgressDialog(requireActivity())

        tvTitleMovieDetailMov = rootView.findViewById<TextView>(R.id.tvTitleMovieDetailMov)
        tvVoteAverageDetailMov = rootView.findViewById<TextView>(R.id.tvVoteAverageDetailMov)
        tvOverViewDetailMov = rootView.findViewById<TextView>(R.id.tvOverViewDetailMov)
        ivBackdropPathDetMov = rootView.findViewById<ImageView>(R.id.ivBackdropPathDetMov)
        ivRegresarAtras = rootView.findViewById<ImageView>(R.id.ivRegresarAtras)

        progresDialog()
        // Recupera los argumentos del fragmento
        arguments?.let {
            println(it.getInt("id", 0))
            movieId = it.getInt("id", 0)
        }

        ivRegresarAtras.setOnClickListener{
            println("clic")
            parentFragmentManager.popBackStack()
            requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
        }

        getMovies()

        return rootView
    }

    private fun getDetailMovie() {
        ApiServiceMovieDb.getInstance().create(IMoviesDb::class.java)
            .getMovieDetails(movieId, API_KEY, language)
            .enqueue(object : Callback<DetailMoviesDataClass> {
                override fun onResponse(
                    call: Call<DetailMoviesDataClass>,
                    response: Response<DetailMoviesDataClass>
                ) {
                    if (response.isSuccessful) {

                        val result = response.body()
                        if (result != null) {
                            Log.d("DetailsMoviesFragment", "GetTopMovies API response successful")
                            Log.d("DetailsMoviesFragment", result.title)
                            Log.d("DetailsMoviesFragment", result.backdrop_path)
                            Log.d("DetailsMoviesFragment", result.overview)
                            Log.d("DetailsMoviesFragment", result.vote_average.toString())

                            tvTitleMovieDetailMov.text = result.title
                            tvVoteAverageDetailMov.text = "Calificacion: ${result.vote_average.toString()}"
                            tvOverViewDetailMov.text = result.overview
                            getImageMovie(result.backdrop_path)

                        }
                    } else {
                        Log.e(
                            "DetailsMoviesFragment",
                            "GetTopMovies API response error: ${response.code()} - ${response.message()}"
                        )
                    }
                }

                override fun onFailure(call: Call<DetailMoviesDataClass>, t: Throwable) {
                    Log.e("DetailsMoviesFragment", "GetTopMovies API response error: ${t.message}")
                }
            })
    }

    private fun getImageMovie(imagen: String) {
        val string_sin_barra = imagen.replace("/", "")
        println(string_sin_barra)
        ApiServiceMovieDbImage.getInstance().create(IMoviesDb::class.java)
            .getImage(string_sin_barra)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        val inputStream = response.body()?.byteStream()
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        if (bitmap != null) {
                            ivBackdropPathDetMov.setImageBitmap(bitmap)
                            progressDialog?.dismiss()
                        } else {
                            Log.e("DetailsMoviesFragment", "No se pudo obtener la imagen")
                        }
                    } else {
                        Log.e("DetailsMoviesFragment", "Error en la respuesta del servidor: ${response.code()} - ${response.message()}")
                        progressDialog?.dismiss()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("DetailsMoviesFragment", "Error en la llamada al servidor: ${t.message}")
                    progressDialog?.dismiss()
                }
            })
    }

    // Ejecutar las dos solicitudes al mismo tiempo en diferentes hilos
    fun getMovies() = runBlocking {
        val job1 = async { getDetailMovie() }
        job1.await()
    }

    private fun progresDialog () {

        progressDialog?.setMessage("Cargando...")
        progressDialog?.setCancelable(false)
        progressDialog?.show()

    }

}