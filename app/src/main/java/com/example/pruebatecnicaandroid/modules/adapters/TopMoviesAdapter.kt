package com.example.pruebatecnicaandroid.modules.adapters

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.pruebatecnicaandroid.R
import com.example.pruebatecnicaandroid.modules.fragments.DetailsMoviesFragment
import com.example.pruebatecnicaandroid.modules.models.Movies
import com.example.pruebatecnicaandroid.retrofit.ApiServiceMovieDbImage
import com.example.pruebatecnicaandroid.retrofit.IMoviesDb
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.function.Predicate
import java.util.stream.Collectors

class TopMoviesAdapter(private  val topMoviesList: ArrayList<Movies>): RecyclerView.Adapter<TopMoviesAdapter.vh>(),
    Filterable {

    private var items: Movies? = null;
    private var cont: Context? = null
    private var dataAux: ArrayList<Movies> = ArrayList(topMoviesList)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopMoviesAdapter.vh {

        cont = parent.context//contexto de mi actividad donde esta el contenedor fragment
        val layoutInflater = LayoutInflater.from(parent.context)
        return TopMoviesAdapter.vh(layoutInflater.inflate(R.layout.item_cards_movies, parent, false))
    }

    override fun onBindViewHolder(holder: TopMoviesAdapter.vh, position: Int) {

        items = topMoviesList[holder.adapterPosition]
        //holder.clContenedorPrincipal
        holder.textViewTitle.text = items!!.title
        holder.tvRating.text = "Puntaje: ${items!!.voteAverage.toString()}"
        getMovies(items!!.posterPath.toString(), holder)

        holder.cvContenedorItemCards.setOnClickListener {

            val fragmentManager = (cont as FragmentActivity).supportFragmentManager

            // Crea una instancia del nuevo Fragment
            val fragmentB = DetailsMoviesFragment()

            // Inicia la transacción para reemplazar el Fragment actual con el nuevo Fragment
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainerMoviesActivity, fragmentB)
            transaction.addToBackStack(null)
            transaction.commit()

        }


    }

    override fun getItemCount(): Int = topMoviesList.size


    class vh(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cvContenedorItemCards: CardView
        var imageViewPoster: ImageView
        var textViewTitle: TextView
        var tvRating: TextView

        init {

            imageViewPoster = itemView.findViewById(R.id.imageViewPoster);
            cvContenedorItemCards = itemView.findViewById(R.id.cvContenedorItemCards);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            tvRating = itemView.findViewById(R.id.tvRating);
        }
    }


    private fun getImageMovie(imagen: String, holder: vh) {
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
                            holder.imageViewPoster.setImageBitmap(bitmap)
                        } else {
                            Log.e("MovieListFragment", "No se pudo obtener la imagen")
                        }
                    } else {
                        Log.e("MovieListFragment", "Error en la respuesta del servidor: ${response.code()} - ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("MovieListFragment", "Error en la llamada al servidor: ${t.message}")
                }
            })
    }

    // Ejecutar las dos solicitudes al mismo tiempo en diferentes hilos
    fun getMovies(imagen: String, holder: vh) = runBlocking {
        val job1 = async { getImageMovie(imagen,holder) }
        job1.await()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredResults = mutableListOf<Movies>()

                if (constraint == null || constraint.isEmpty()) {
                    // Si no hay texto de búsqueda, devuelve la copia del conjunto de datos original
                    filteredResults.addAll(dataAux)
                } else {
                    val filterPattern = constraint.toString().toLowerCase(Locale.getDefault()).trim()

                    // Filtro personalizado para encontrar elementos que coincidan con el texto de búsqueda
                    topMoviesList.forEach { item ->
                        if (item.title.toLowerCase(Locale.getDefault()).contains(filterPattern)) {
                            filteredResults.add(item)
                        }
                    }
                }

                val results = FilterResults()
                results.values = filteredResults
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                dataAux = (results?.values as? ArrayList<Movies> ?: emptyList()) as ArrayList<Movies>
                notifyDataSetChanged()
            }
        }
    }

    fun buscar(titleRcp: String) {
        println(titleRcp)
        val longitud = titleRcp.length
        if (longitud == 0) {
            topMoviesList.clear()
            topMoviesList.addAll(dataAux!!)
        } else {
            val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                topMoviesList.stream()
                    .filter { i -> i.title.toLowerCase().contains(titleRcp.toLowerCase()) }
                    .collect(Collectors.toList())
            } else {
                val filteredList = ArrayList<Movies>()
                for (c in topMoviesList) {
                    if (c.title.toLowerCase().contains(titleRcp.toLowerCase())) {
                        filteredList.add(c)
                    }
                }
                filteredList
            }
            topMoviesList.clear()
            topMoviesList.addAll(collection)
        }
        notifyDataSetChanged()


        /*val longitud = titleRcp.length
        if (longitud == 0) {
            topMoviesList.clear()
            topMoviesList.addAll(dataAux)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val collection: List<Movies> = topMoviesList.stream()
                    .filter(Predicate<Movies> { i: Movies ->
                        i.title.toLowerCase()
                            .contains(titleRcp.lowercase(Locale.getDefault()))
                    })
                    .collect(Collectors.toList())
                topMoviesList.clear()
                topMoviesList.addAll(collection)
            } else {
                for (c in topMoviesList) {
                    if (c.title.toLowerCase()
                            .contains(titleRcp.lowercase(Locale.getDefault()))
                    ) {
                        topMoviesList.add(c)
                    }
                }
            }
        }
        notifyDataSetChanged()*/
    }




}


