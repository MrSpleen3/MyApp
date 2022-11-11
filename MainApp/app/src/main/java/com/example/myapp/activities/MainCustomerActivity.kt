package com.example.myapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.example.myapp.R
import com.example.myapp.models.FirebaseDbWrapper
import com.example.myapp.models.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainCustomerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_customer)
        val thiz = this
        val spinner: Spinner = findViewById(R.id.spinnerPlaces)
        val firebaseDbWrapper : FirebaseDbWrapper = FirebaseDbWrapper(this)
        GlobalScope.launch(Dispatchers.IO) {
            val arr : ArrayList<String> = firebaseDbWrapper.getPlaces()
            val adapter: ArrayAdapter<String> = ArrayAdapter(thiz , android.R.layout.simple_spinner_item, arr)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            withContext(Dispatchers.Main) {
                spinner.adapter = adapter
            }
        }
    }
}