package com.example.myapp.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import android.widget.ListView
import android.widget.RatingBar
import android.widget.TextView
import com.example.myapp.R
import com.example.myapp.activities.MainCustomerActivity
import com.example.myapp.activities.MainInstructorActivity
import com.example.myapp.models.FirebaseDbWrapper
import com.example.myapp.models.InstructorListEl
import com.example.myapp.models.MyListAdapter
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
 * Use the [MainGetInstructors.newInstance] factory method to
 * create an instance of this fragment.
 */
//Prende il luogo d'interesse dall'altro frag
//e ritorna una lista d'istruttori relativa al luogo
class MainGetInstructors() : Fragment() {
    // TODO: Rename and change types of parameters
    private var place: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            place = it.getString("place")
        }
    }
    //TODO: change ratingbar
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val thiz = this
        val view : View = inflater.inflate(R.layout.fragment_main_get_instructors, container, false)
        val istrList : ListView = view.findViewById(R.id.instructorList)
        val firebaseDbWrapper : FirebaseDbWrapper = FirebaseDbWrapper(thiz.requireContext())
        GlobalScope.launch(Dispatchers.IO) {
            val mylist : List <InstructorListEl> = firebaseDbWrapper.getInstructorList(place)
            val adapter : ListAdapter = MyListAdapter(thiz.requireActivity(),0,mylist) as ListAdapter
            withContext(Dispatchers.Main) {
                istrList.adapter = adapter
            }
        }
        val provaBack : TextView = view.findViewById(R.id.back)
        provaBack.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                (thiz.requireActivity() as MainCustomerActivity).renderMainFrag(null)
            }
        })
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MainGetInstructors.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(place: String) =
            MainGetInstructors().apply {
                arguments = Bundle().apply {
                    putString("place", place)
                }
            }
    }
}
