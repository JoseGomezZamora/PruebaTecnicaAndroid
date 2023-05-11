package com.example.pruebatecnicaandroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.pruebatecnicaandroid.modules.activitys.MoviesActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        nextScreenSplashView()

    }

    /*ESTA FUNCION ESPERA 3 SEGUNDOS
    * PARA MOSTRAR EL SCREENSPLASH Y
    * DESPUES SE VA A LA SIGUIENTE
    * ACTIVIDAD PERO SIN ANTES FINALIZAR
    * LA ACTIVIDAD ANTERIOR*/
    private fun nextScreenSplashView() {

        Handler().postDelayed({
            val intent = Intent(this, MoviesActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000) // Retraso de 3 segundos en milisegundos

    }

}