package com.example.myapplication.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("HELP me", "here")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

    }
}