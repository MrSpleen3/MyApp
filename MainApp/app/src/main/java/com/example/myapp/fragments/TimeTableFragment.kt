package com.example.myapp.fragments

import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import com.example.myapp.R
import com.example.myapp.activities.MainInstructorActivity
import com.example.myapp.models.BookingElement
import com.example.myapp.models.BookingListAdapter
import com.example.myapp.models.FirebaseDbWrapper
import com.example.myapp.models.LessonListAdapter
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TimeTableFragment : Fragment() {

    private var day: Int? = null
    private var month: Int? = null
    private var year: Int? = null
    private var id_istr : String? = null
    private var id_cust : String? = null
    private var custName : String? = null
    private var instName : String? = null
    private var instructorFlag : Boolean? = null
    var doc : ListenerRegistration? = null
    var myAdapter : ArrayAdapter<BookingElement>? = null
    var myListView: ListView? = null
    var firebaseDbWrapper : FirebaseDbWrapper? = null
    val thiz = this
    var mycontext : Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

            day = it.getInt("day")
            month = it.getInt("month")
            year = it.getInt("year")
            id_istr = it.getString("id_istr")
            id_cust = it.getString("id_cust")
            instructorFlag=it.getBoolean("flag_istr")
            custName = it.getString("name")
            instName = it.getString("name_istr")

        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mycontext = thiz.requireContext()
        var flag = false
        val view : View = inflater.inflate(R.layout.fragment_timetable, container, false)
        myListView = view.findViewById(R.id.bookingList)
        firebaseDbWrapper = FirebaseDbWrapper(thiz.requireContext())
        GlobalScope.launch(Dispatchers.IO) {
            val myList =
                firebaseDbWrapper!!.getBookings(id_istr!!, day!!, month!!, year!!)
            if(!instructorFlag!!) {
                myAdapter = BookingListAdapter(mycontext!!, 0, myList, id_cust!!,id_istr!!,day!!,month!!,year!!,custName!!,instName!!)
            }
            else {
                myAdapter = LessonListAdapter(mycontext!!, 0, myList,id_istr!!,day!!,month!!,year!!)
            }
            withContext(Dispatchers.Main) {
                myListView!!.adapter = myAdapter!!
            }
        }
        val fire : FirebaseDbWrapper = FirebaseDbWrapper(thiz.requireContext())
        val docRef = fire.getCollection()
        doc = docRef.whereEqualTo("id_istr", id_istr!!)
            .whereEqualTo("day", day!!)
            .whereEqualTo("month", (month!!))
            .whereEqualTo("year", year!!)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Log.w(ContentValues.TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                if ((value!!.documents.size > 0)&&flag) {
                    listenComplete()
                }
                //non interessa la prima chiamata, solo quando i dati cambiano
                flag = true

            }
        return view
    }
    fun listenComplete() {
        GlobalScope.launch(Dispatchers.IO) {
            val myList =
                firebaseDbWrapper!!.getBookings(id_istr!!, day!!, month!!, year!!)
            if(!instructorFlag!!) {
                myAdapter = BookingListAdapter(mycontext!!, 0, myList, id_cust!!,id_istr!!,day!!,month!!,year!!,custName!!,instName!!)
            }
            else {
                myAdapter = LessonListAdapter(mycontext!!, 0, myList,id_istr!!,day!!,month!!,year!!)
            }
            withContext(Dispatchers.Main) {
                myListView!!.adapter = myAdapter!!
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(id_istr : String, id_cust : String?,flag : Boolean, day : Int, month : Int, year : Int, custName : String?,instName : String?) =
            TimeTableFragment().apply {
                arguments = Bundle().apply {
                    putString("id_istr",id_istr)
                    putString("id_cust",id_cust)
                    putBoolean("flag_istr",flag)
                    putInt("day",day)
                    putInt("month",month)
                    putInt("year",year)
                    putString("name",custName)
                    putString("name_istr",instName)
                }
            }
    }
}