package com.example.myapp.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.myapp.R
import com.example.myapp.models.FirebaseAuthWrapper

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        val thiz : Context=this
        val button: Button = findViewById(R.id.buttonSignIn)
        button.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val email: EditText = findViewById(R.id.emailAddressSignIn)
                val password: EditText = findViewById(R.id.passwordSignIn)
                val firebaseAuthWrapper: FirebaseAuthWrapper = FirebaseAuthWrapper(thiz)
                firebaseAuthWrapper.signIn(email.text.toString(), password.text.toString())
            }
        })
        val link : TextView = findViewById(R.id.toSignUp)
        link.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val intent : Intent = Intent(thiz, SignUpActivity::class.java)
                thiz.startActivity(intent)
            }
        })
    }
}