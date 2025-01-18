package com.example.etiquetadodeimagenesml_kotlin

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeler
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var Imagen : ImageView
    private lateinit var BtnEtiquetarImagen : Button
    private lateinit var Resultados : TextView

    private lateinit var imageLabeler : ImageLabeler
    private lateinit var progressDialog: ProgressDialog

    var imageUri : Uri ?= null

    private lateinit var translatorOptions : TranslatorOptions
    private lateinit var translator : Translator

    private val codigo_idioma_origen = "en"
    private val codigo_idioma_destino = "es"

    private var Texto_etiquetas = ""

    @SuppressLint("MissingInflatedId")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.toolbar))

        InicializarVistas()
        imageLabeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS) //Estamos configuarando el etiquetado de imagenes

        //val bitmapDrawable = Imagen.drawable as BitmapDrawable
        //val bitmap = bitmapDrawable.bitmap

        BtnEtiquetarImagen.setOnClickListener {
          //  EtiquetarImagen(bitmap)
            if (imageUri!=null){
                EtiquetarImagenGaleria(imageUri!!)
            }else{
                Toast.makeText(applicationContext,"Por favor añada una imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun EtiquetarImagenGaleria(imageUri: Uri) {
        Resultados.text = "" //Esta linea nos sirve para evitar el duplicado de resultados
        progressDialog.setMessage("Reconociendo objetos de la imagen")
        progressDialog.show()

        var inputImage : InputImage ?= null

        try {
            inputImage = InputImage.fromFilePath(applicationContext,imageUri)
        }catch (e: IOException){
            e.printStackTrace()
        }

        if (inputImage!=null){
            imageLabeler.process(inputImage)

                .addOnSuccessListener {labels->
                    for (imageLabel in labels){
                        //Obtener la etiqueta
                        val etiqueta = imageLabel.text

                        //Obtener el porcentaje de confianza
                        val confianza = imageLabel.confidence

                        //Obtener el indice
                        val indice = imageLabel.index

                        Resultados.append("|||Name: $etiqueta \n - with a confidence of: $confianza \n - and it's index is: $indice \n \n")

                    }
                    progressDialog.dismiss()

                }
                .addOnFailureListener {e->

                    progressDialog.dismiss()
                    Toast.makeText(applicationContext,"No se pudo realizar el etiquetado de imagen debido a ${e.message}"
                        ,Toast.LENGTH_SHORT).show()

                }
        }
    }

    private fun EtiquetarImagen(bitmap: Bitmap) {
         progressDialog.setMessage("Reconociendo objetos de la imagen")
         progressDialog.show()

         val inputImage = InputImage.fromBitmap(bitmap,0)
        imageLabeler.process(inputImage)

            .addOnSuccessListener {labels->
                for (imageLabel in labels){
                    //Obtener la etiqueta
                    val etiqueta = imageLabel.text

                    //Obtener el porcentaje de confianza
                    val confianza = imageLabel.confidence

                    //Obtener el indice
                    val indice = imageLabel.index

                    Resultados.append("Etiqueta: $etiqueta \n Confianza: $confianza \n Indice: $indice \n \n")

                }
                progressDialog.dismiss()

            }
            .addOnFailureListener {e->

                progressDialog.dismiss()
                Toast.makeText(applicationContext,"No se pudo realizar el etiquetado de imagen debido a ${e.message}"
                ,Toast.LENGTH_SHORT).show()

            }
    }

    private fun TraducirTexto(){
        Texto_etiquetas = Resultados.text.toString().trim()
        progressDialog.setMessage("Procesando")
        progressDialog.show()

        translatorOptions = TranslatorOptions.Builder()
            .setSourceLanguage(codigo_idioma_origen)
            .setTargetLanguage(codigo_idioma_destino)
            .build()

        translator = Translation.getClient(translatorOptions)

        val downloadConditions = DownloadConditions.Builder()
            .requireWifi()
            .build()

        translator.downloadModelIfNeeded(downloadConditions)
            .addOnSuccessListener {
                progressDialog.setMessage("Traduciendo Etiquetas")

                //Este es el momento en el que se realiza la traducción
                translator.translate(Texto_etiquetas)
                    .addOnSuccessListener {etiquetasTraducidas->
                        progressDialog.dismiss()
                        Resultados.text = etiquetasTraducidas

                    }
                    .addOnFailureListener {e->
                        progressDialog.dismiss()
                        Toast.makeText(applicationContext,"${e.message}",Toast.LENGTH_SHORT).show()

                    }
            }.addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(applicationContext,"${e.message}",Toast.LENGTH_SHORT).show()
            }
    }

    private fun InicializarVistas(){
        Imagen = findViewById(R.id.Imagen)
        BtnEtiquetarImagen = findViewById(R.id.BtnEtiquetarImagen)
        Resultados = findViewById(R.id.Resultados)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.mi_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.MenuGaleria->{
                //Toast.makeText(applicationContext,"Abir galeria",Toast.LENGTH_SHORT).show()
                SeleccionarImagenGaleria()
                true
            }

            R.id.MenuTraducir->{
                Texto_etiquetas = Resultados.text.toString().trim()
                if (!Texto_etiquetas.isEmpty()){
                    TraducirTexto()
                }else{
                    Toast.makeText(applicationContext,"No hay etiquetas para traducir",Toast.LENGTH_SHORT).show()
                }
                true
            }
            else->super.onOptionsItemSelected(item)

        }
    }

    private fun SeleccionarImagenGaleria (){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galeriaARL.launch(intent)
    }

    private val galeriaARL = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback {result->
            if (result.resultCode == Activity.RESULT_OK){
                val data = result.data
                imageUri = data!!.data

                Imagen.setImageURI(imageUri)
                Resultados.text = "" //Para limpiar los datos al agregar una nueva imagen

            }else{
                Toast.makeText(applicationContext,"Cancelado por el usuario",Toast.LENGTH_SHORT).show()
            }

        }
    )

}