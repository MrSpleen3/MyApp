package com.example.myapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import com.example.myapp.R
import com.example.myapp.models.FirebaseDbWrapper
import com.example.myapp.models.MyBookingListAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class YourLessonsActivity : AppCompatActivity() {

    var myId : String? = null
    var flag_istr: Boolean? = null
    var firebaseDbWrapper: FirebaseDbWrapper? = null
    var myListView : ListView? = null
    val thiz = this
    var year : Int? = null
    var month : Int? = null
    var day : Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        if(intent.extras!=null) {
            myId = intent!!.getStringExtra("id")
            flag_istr = intent!!.extras!!.getBoolean("flag_istr")
        }
        else {
            val intent = Intent(this, SplashActivity :: class.java)
            Toast.makeText(this.applicationContext, "Qualcosa è andato storto!", Toast.LENGTH_SHORT).show()
            startActivity(intent)
            finish()
        }
        myListView = findViewById(R.id.listaLez)
        val calendar: Calendar = Calendar.getInstance()
        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH) + 1
        day = calendar.get(Calendar.DAY_OF_MONTH)
        GlobalScope.launch(Dispatchers.IO) {
            firebaseDbWrapper = FirebaseDbWrapper(thiz)
            val filteredList = firebaseDbWrapper!!.getYourBookings(myId!!,day!!,month!!,year!!,flag_istr!!)
            filteredList.sort()
            val adapter= MyBookingListAdapter(thiz,0,filteredList,flag_istr!!)
            withContext(Dispatchers.Main){
                myListView!!.adapter=adapter
            }

        }

    }
//TODO:add live listener calling refresh
    fun refreshAdapter() {
        GlobalScope.launch(Dispatchers.IO) {
            firebaseDbWrapper = FirebaseDbWrapper(thiz)
            val filteredList = firebaseDbWrapper!!.getYourBookings(myId!!,day!!,month!!,year!!,flag_istr!!)
            filteredList.sort()
            val adapter= MyBookingListAdapter(thiz,0,filteredList,flag_istr!!)
            withContext(Dispatchers.Main){
                myListView!!.adapter=adapter
            }

        }
    }

}