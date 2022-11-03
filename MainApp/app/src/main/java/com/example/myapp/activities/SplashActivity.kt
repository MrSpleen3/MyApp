package com.example.myapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapp.R
import com.example.myapp.models.FirebaseWrapper

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val firebaseWrapper : FirebaseWrapper = FirebaseWrapper()
        if (!firebaseWrapper.isAuthenticated()){
            val intent = Intent(this,SignInActivity::class.java)
            this.startActivity(intent)
            finish()
        }
        //TODO: Check if is an instructor
    }
}