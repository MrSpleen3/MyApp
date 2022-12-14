package com.example.myapp.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapp.R
import com.example.myapp.models.FirebaseAuthWrapper
import com.example.myapp.models.FirebaseDbWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashActivity : AppCompatActivity() {

    private var permissionGranted : Boolean? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun hasPermission() : Boolean {
        if (permissionGranted == null) {
            permissionGranted =
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        }
        return permissionGranted!!
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val thiz = this
        if(this.hasPermission()) {
            val firebaseAuthWrapper: FirebaseAuthWrapper = FirebaseAuthWrapper(this)
            //if you arent logged send you to sign in
            if (!firebaseAuthWrapper.isAuthenticated()) {
                val intent = Intent(this, LogActivity::class.java)
                this.startActivity(intent)
                finish()
            } else {
                //check if customer or instructor and send you to your main
                val firebaseDbWrapper: FirebaseDbWrapper = FirebaseDbWrapper(this)
                val id: String = firebaseAuthWrapper.getId()
                GlobalScope.launch(Dispatchers.IO) {
                    val flag = firebaseDbWrapper.isInstructor(id)
                    withContext(Dispatchers.Main) {
                        var intent : Intent? = null
                        if (flag) {
                            intent = Intent(thiz, MainInstructorActivity::class.java)
                        } else {
                            intent = Intent(thiz, MainCustomerActivity::class.java)
                            val name = firebaseAuthWrapper.getName()
                            intent.putExtra("name",name)
                        }
                        intent.putExtra("id", id)
                        thiz.startActivity(intent!!)
                        finish()
                    }
                }
            }
        }
        else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS),123)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        this.permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
        val intent = Intent(this, SplashActivity::class.java)
        this.startActivity(intent)
        finish()
    }

}