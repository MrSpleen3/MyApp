package com.example.myapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.example.myapp.R
import com.example.myapp.models.BookingListAdapter
import com.example.myapp.models.FirebaseDbWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TimeTableFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TimeTableFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var day: Int? = null
    private var month: Int? = null
    private var year: Int? = null
    private var id_istr : String? = null
    private var id_cust : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

            day = it.getInt("day")
            month = it.getInt("month")
            year = it.getInt("year")
            id_istr = it.getString("id_istr")
            id_cust = it.getString("id_cust")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val thiz = this
        val view : View = inflater.inflate(R.layout.fragment_timetable, container, false)
        val myListView: ListView = view.findViewById(R.id.bookingList)
        GlobalScope.launch(Dispatchers.IO) {
            val myList =
                FirebaseDbWrapper(thiz.requireContext()).getBookings(id_istr!!, day!!, month!!, year!!)
            val myAdapter : BookingListAdapter = BookingListAdapter(thiz.requireContext(),0,myList,id_cust!!)
            withContext(Dispatchers.Main) {
                myListView.adapter = myAdapter
            }
        }
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TimeTableFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(id_istr : String, id_cust : String, day : Int, month : Int, year : Int) =
            TimeTableFragment().apply {
                arguments = Bundle().apply {
                    putString("id_istr",id_istr)
                    putString("id_cust",id_cust)
                    putInt("day",day)
                    putInt("month",month)
                    putInt("year",year)
                }
            }
    }
}