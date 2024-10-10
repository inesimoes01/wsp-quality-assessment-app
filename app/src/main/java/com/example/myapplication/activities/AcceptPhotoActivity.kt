package com.example.myapplication.activities


import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.Classes.Settings
import com.example.myapplication.R


class AcceptPhotoActivity : AppCompatActivity() {
    private var imageSelected: ImageView? = null
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accept_photo)

        // get image uri sent from the camera view activity
        // uri is the path to the image inside the phone
        imageSelected = findViewById(R.id.image_preview);
        val imageUri = intent.getStringExtra("uri")
        imageSelected?.setImageURI(Uri.parse(imageUri))

        // listeners for the buttons
        val btnAccept = findViewById<Button>(R.id.btn_accept)
        val btnReject = findViewById<Button>(R.id.btn_refuse)
        val radioGrp = findViewById<RadioGroup>(R.id.radioGroup)

        Log.d("rest api", "back")
        btnAccept.setOnClickListener {
            Log.d("rest api", "checking values")

            // save image and settings
            val w = findViewById<EditText>(R.id.width_value)
            val h = findViewById<EditText>(R.id.height_value)
            val isAI = findViewById<RadioButton>(R.id.aiRB)
            val isCV = findViewById<RadioButton>(R.id.cvRB)
            Log.d("rest api", "checking values")


            if (w.text.toString() == "") {
                Log.d("rest api", "missing w")
                val msg = "Missing value for width"
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (h.text.toString() == "") {
                Log.d("rest api", "missing h")
                val msg = "Missing value for height"
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!isAI.isChecked and !isCV.isChecked) {
                Log.d("rest api", "missing m")
                val msg = "Missing value for segmentation model"
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Log.d("rest api", "this is the width" + w.text.toString())

            val wDouble = w.text.toString().replace(Regex(",(?=\\d)"), ".").toDouble()
            val hDouble = h.text.toString().replace(Regex(",(?=\\d)"), ".").toDouble()
            Settings.setReal_width(wDouble)
            Settings.setReal_height(hDouble)
            if (isAI.isChecked)
                Settings.setModel(0)
            else
                Settings.setModel(1)

            goToResults(imageUri)
        }


        btnReject.setOnClickListener { goToCameraView() }

    }

    private fun goToCameraView() {
        val cameraView = Intent(this, CameraViewActivity::class.java)
        startActivity(cameraView)
    }

    private fun goToResults(imageUri: String?){
        val resultsView = Intent(this, ResultsActivity::class.java)
        resultsView.putExtra("uri", imageUri)
        startActivity(resultsView)
    }
}



