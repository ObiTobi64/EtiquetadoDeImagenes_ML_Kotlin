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
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeler
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions

class MainActivity : AppCompatActivity() {

    private lateinit var Imagen : ImageView
    private lateinit var BtnEtiquetarImagen : Button
    private lateinit var Resultados : TextView

    private lateinit var imageLabeler : ImageLabeler
    private lateinit var progressDialog: ProgressDialog

    var imageUri : Uri ?= null

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
            //Toast.makeText(applicationContext,"Etiquetando imagen",Toast.LENGTH_SHORT).show()

          //  EtiquetarImagen(bitmap)
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
            }else{
                Toast.makeText(applicationContext,"Cancelado por el usuario",Toast.LENGTH_SHORT).show()
            }

        }
    )

}