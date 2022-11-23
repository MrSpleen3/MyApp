package com.example.myapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.TextView
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
        else TODO() //se metto l'intent pure nella notifica
        myListView = findViewById(R.id.listaLez)
        val back : TextView = findViewById(R.id.titleNotif)
        back.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                finish()
            }
        })
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