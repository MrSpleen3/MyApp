package com.example.myapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapp.R
import com.example.myapp.models.FirebaseAuthWrapper
import com.example.myapp.models.FirebaseDbWrapper

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val firebaseAuthWrapper : FirebaseAuthWrapper = FirebaseAuthWrapper(this)
        //if you arent logged send you to sign in
        if (!firebaseAuthWrapper.isAuthenticated()){
            val intent = Intent(this,LogActivity::class.java)
            this.startActivity(intent)
            finish()
        }
        else {
            //check if customer or instructor and send you to your main
            val firebaseDbWrapper: FirebaseDbWrapper = FirebaseDbWrapper(this)
            val id: String = firebaseAuthWrapper.getId()
          /*  if (!firebaseDbWrapper.isInstructor(id)) {
                val intent = Intent(this,MainCustomerActivity::class.java)
                this.startActivity(intent)
                finish()
            }
            else {
                //TODO: customize intent to get you to YOUR main instructor activity
                val intent = Intent(this,MainInstructorActivity::class.java)
                this.startActivity(intent)
                finish()
            }
           */
            firebaseDbWrapper.isInstructor(id)

        }
    }
}