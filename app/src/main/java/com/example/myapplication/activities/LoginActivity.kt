package com.example.myapplication.activities


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R



class LoginActivity : AppCompatActivity() {

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // save values
        var btnGoToCamera = findViewById<Button>(R.id.btn_go_camera)

        // submit button
        btnGoToCamera.setOnClickListener { goToCameraActivity() }

    }

    private fun goToCameraActivity(){
        val cameraView = Intent(this, CameraViewActivity::class.java)
        startActivity(cameraView)
        finish();
    }

}
