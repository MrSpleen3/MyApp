package com.example.myapp.fragments

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
import com.example.myapp.models.MyBookingListAdapter
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
        myListView = view.findViewById(R.id.listaLez)
        val calendar: Calendar = Calendar.getInstance()
        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH) + 1
        day = calendar.get(Calendar.DAY_OF_MONTH)
        GlobalScope.launch(Dispatchers.IO) {
            firebaseDbWrapper = FirebaseDbWrapper(thiz.requireContext())
            val filteredList = firebaseDbWrapper!!.getYourBookings(myId!!,day!!,month!!,year!!,flag_istr!!)
            filteredList.sort()
            val adapter= MyBookingListAdapter(thiz.requireContext(),0,filteredList,flag_istr!!)
            withContext(Dispatchers.Main){
                myListView!!.adapter=adapter
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
        GlobalScope.launch(Dispatchers.IO) {
            firebaseDbWrapper = FirebaseDbWrapper(thiz.requireContext())
            val filteredList = firebaseDbWrapper!!.getYourBookings(myId!!,day!!,month!!,year!!,flag_istr!!)
            filteredList.sort()
            val adapter= MyBookingListAdapter(thiz.requireContext(),0,filteredList,flag_istr!!)
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