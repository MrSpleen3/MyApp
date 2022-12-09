package com.example.myapp.fragments

import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ListView
import androidx.activity.OnBackPressedCallback
import com.example.myapp.R
import com.example.myapp.activities.MainCustomerActivity
import com.example.myapp.activities.MainInstructorActivity
import com.example.myapp.models.FirebaseDbWrapper
import com.example.myapp.models.YourLessonsListAdapter
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class YourLessonsFragment : Fragment() {

    private var myId: String? = null
    private var flag_istr: Boolean? = null
    var firebaseDbWrapper: FirebaseDbWrapper? = null
    var myListView : ListView? = null
    val thiz = this
    var year : Int? = null
    var month : Int? = null
    var day : Int? = null
    private var mycontext : Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {
            myId = it.getString("id")
            flag_istr = it.getBoolean("flag_istr")
        }
        this.requireActivity().onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d("bo","back")
                if (!flag_istr!!) {
                    (thiz.requireActivity() as MainCustomerActivity).renderMainFrag(null, null)
                }
                else {
                    (thiz.requireActivity() as MainInstructorActivity).renderMainFrag()
                }
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_your_lessons, container, false)
        mycontext = thiz.requireContext()
        myListView = view.findViewById(R.id.listaLez)
        val calendar: Calendar = Calendar.getInstance()
        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH) + 1
        day = calendar.get(Calendar.DAY_OF_MONTH)
        firebaseDbWrapper = FirebaseDbWrapper(thiz.requireContext())
        GlobalScope.launch(Dispatchers.IO) {
            val filteredList = firebaseDbWrapper!!.getYourBookings(myId!!,day!!,month!!,year!!,flag_istr!!)
            filteredList.sort()
            val adapter= YourLessonsListAdapter(thiz.requireContext(),0,filteredList,flag_istr!!)
            withContext(Dispatchers.Main){
                myListView!!.adapter=adapter
            }

        }
        val docRef = firebaseDbWrapper!!.getCollection()
        val doc : Query
        if(!flag_istr!!) doc=docRef.whereEqualTo("id_cust", myId)
        else doc=docRef.whereEqualTo("id_istr", myId)
        var first : Boolean = false
        doc!!.addSnapshotListener { value, e ->
            if (e != null) {
                Log.w(ContentValues.TAG, "Listen failed.", e)
                return@addSnapshotListener
            }
            if (!flag_istr!!) {
                for (dc in value!!.documentChanges) {
                    if (dc.type != DocumentChange.Type.ADDED) refreshAdapter()
                }
            } else {
                //se no ogni prima volta ritorna tutti i documenti con tale id
                if (first) refreshAdapter()
                else first=true
            }
        }
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.findItem(R.id.my_notify).isVisible= false
        super.onCreateOptionsMenu(menu, inflater)
    }
    //TODO : chiamare refreshadapter da snapshotlistener!
    fun refreshAdapter() {
        Log.d("ewe","refr")
        GlobalScope.launch(Dispatchers.IO) {
            firebaseDbWrapper = FirebaseDbWrapper(mycontext!!)
            val filteredList = firebaseDbWrapper!!.getYourBookings(myId!!,day!!,month!!,year!!,flag_istr!!)
            filteredList.sort()
            val adapter= YourLessonsListAdapter(mycontext!!,0,filteredList,flag_istr!!)
            withContext(Dispatchers.Main){
                myListView!!.adapter=adapter
            }

        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: Boolean) =
            YourLessonsFragment().apply {
                arguments = Bundle().apply {
                    putString("id", param1)
                    putBoolean("flag_istr", param2)
                }
            }
    }
}