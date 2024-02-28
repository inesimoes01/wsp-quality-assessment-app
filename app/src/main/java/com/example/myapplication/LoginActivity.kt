package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.myapplication.ui.theme.MyApplicationTheme


class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cameraView = Intent(this, CameraViewActivity::class.java)
        setContent {
            MyApplicationTheme {

                Box( Modifier.fillMaxSize() ) {
                    Button(
                        onClick = {
                            startActivity(cameraView)
                        },

                        Modifier
                            .background(color = Color.Yellow)
                            .align(alignment = Alignment.Center)

                    ) {
                        Text("Login")
                    }
                }
            }
        }
    }

}







