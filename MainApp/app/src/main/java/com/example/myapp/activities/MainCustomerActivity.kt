package com.example.myapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.example.myapp.R
import com.example.myapp.models.FirebaseDbWrapper
import com.example.myapp.models.Response

class MainCustomerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_customer)

        val spinner: Spinner = findViewById(R.id.spinnerPlaces)
        var arr : ArrayList<String> = ArrayList()
        val res : Response = Response(null,null)
        val firebaseDbWrapper : FirebaseDbWrapper = FirebaseDbWrapper(this)
        firebaseDbWrapper.getPlaces(res)
        arr = res.list!!
        val adapter: ArrayAdapter<String> = ArrayAdapter(this,android.R.layout.simple_spinner_item,arr)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }
}