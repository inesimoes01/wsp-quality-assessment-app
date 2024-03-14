package com.example.myapplication.activities


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.Classes.UserDAO
import com.example.myapplication.R



class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        // save values
        var etUsername = findViewById<EditText>(R.id.username)
        var etPassword = findViewById<EditText>(R.id.password)
        var btnSubmit = findViewById<Button>(R.id.btn_submit)



        // submit button
        btnSubmit.setOnClickListener {
            val username = etUsername.text.toString();
            val password = etPassword.text.toString();
            Toast.makeText(this@LoginActivity, "Login Successful", Toast.LENGTH_LONG).show()

            // verify credentials
            checkLogin(username, password)

            // change page
            val cameraView = Intent(this, CameraViewActivity::class.java)

            startActivity(cameraView)
        }
    }



    private fun checkLogin(username:String, password: String){
        UserDAO.checkLogin(username, password)
    }


}
