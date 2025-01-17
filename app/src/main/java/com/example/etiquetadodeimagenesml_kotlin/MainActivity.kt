package com.example.etiquetadodeimagenesml_kotlin

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var Imagen : ImageView
    private lateinit var BtnEtiquetarImagen : Button
    private lateinit var Resultados : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        InicializarVistas()

        BtnEtiquetarImagen.setOnClickListener {
            Toast.makeText(applicationContext,"Etiquetando imagen",Toast.LENGTH_SHORT).show()
        }
    }

    private fun InicializarVistas(){
        Imagen = findViewById(R.id.Imagen)
        BtnEtiquetarImagen = findViewById(R.id.BtnEtiquetarImagen)
        Resultados = findViewById(R.id.Resultados)
    }
}