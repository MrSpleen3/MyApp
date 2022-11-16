package com.example.myapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myapp.R

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
    private var id : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

            day = it.getInt("day")
            month = it.getInt("month")
            year = it.getInt("year")
            id = it.getString("id")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //TODO: get lessons of the day, render the results in the list...
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timetable, container, false)
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
        fun newInstance(id : String,day : Int, month : Int, year : Int) =
            TimeTableFragment().apply {
                arguments = Bundle().apply {
                    putString("id",id)
                    putInt("day",day)
                    putInt("month",month)
                    putInt("year",year)
                }
            }
    }
}