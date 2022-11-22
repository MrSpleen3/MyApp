package com.example.myapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import com.example.myapp.R
import com.example.myapp.models.FirebaseDbWrapper
import com.example.myapp.models.MyBookingListAdapter
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class NotificationActivity : AppCompatActivity() {

    var myId : String? = null
    var flag_istr: Boolean? = null
    var firebaseDbWrapper: FirebaseDbWrapper? = null
    var doc : ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        if(intent.extras!=null) {
            myId = intent!!.getStringExtra("id")
            flag_istr = intent!!.extras!!.getBoolean("flag_istr")
        }
        else TODO() //se metto l'intent pure nella notifica
        val thiz = this
        val myListView : ListView = findViewById(R.id.listaLez)
        val calendar: Calendar = Calendar.getInstance()
        var year : Int = calendar.get(Calendar.YEAR)
        var month : Int = calendar.get(Calendar.MONTH) + 1
        var day : Int = calendar.get(Calendar.DAY_OF_MONTH)
        GlobalScope.launch(Dispatchers.IO) {
            firebaseDbWrapper = FirebaseDbWrapper(thiz)
            val filteredList = firebaseDbWrapper!!.getYourBookings(myId!!,day,month,year,flag_istr!!)
            filteredList.sort()
            val adapter= MyBookingListAdapter(thiz,0,filteredList)
            withContext(Dispatchers.Main){
                myListView.adapter=adapter
            }

        }

    }

    override fun onDestroy() {
        if(doc!=null){
            doc!!.remove()
        }
        super.onDestroy()
    }

}