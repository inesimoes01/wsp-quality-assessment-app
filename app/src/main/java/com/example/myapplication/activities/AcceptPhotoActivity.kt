package com.example.myapplication.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R


class AcceptPhotoActivity : AppCompatActivity() {
    private var imageSelected: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accept_photo)

        // get image uri sent from the camera view activity
        imageSelected = findViewById(R.id.image_preview);
        val imageUri = intent.getStringExtra("uri")
        imageSelected?.setImageURI(Uri.parse(imageUri))

        // listeners for the buttons
        var btnAccept = findViewById<ImageButton>(R.id.btn_accept)
        var btnReject = findViewById<ImageButton>(R.id.btn_refuse)

        btnAccept.setOnClickListener { goToResults() }
        btnReject.setOnClickListener { goToCameraView() }

    }

    private fun goToCameraView() {
        val cameraView = Intent(this, CameraViewActivity::class.java)
        startActivity(cameraView)
    }

    private fun goToResults(){
        val resultsView = Intent(this, ResultsActivity::class.java)
        startActivity(resultsView)
    }
}


