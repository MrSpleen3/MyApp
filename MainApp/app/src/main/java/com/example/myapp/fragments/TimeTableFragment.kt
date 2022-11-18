package com.example.myapp.fragments

import android.content.ContentValues
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
    private var instructorFlag : Boolean? = null
    var doc : ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

            day = it.getInt("day")
            month = it.getInt("month")
            year = it.getInt("year")
            id_istr = it.getString("id_istr")
            id_cust = it.getString("id_cust")
            instructorFlag=it.getBoolean("flag_istr")
        }
    }
    //TODO: aggiungere listener di eventi live! se no conflitti prenotazioni!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val thiz = this
        var flag = false
        val view : View = inflater.inflate(R.layout.fragment_timetable, container, false)
        val myListView: ListView = view.findViewById(R.id.bookingList)
        val firebaseDbWrapper : FirebaseDbWrapper = FirebaseDbWrapper(thiz.requireContext())
        GlobalScope.launch(Dispatchers.IO) {
            val myList =
                firebaseDbWrapper.getBookings(id_istr!!, day!!, month!!, year!!)
            var myAdapter : ArrayAdapter<BookingElement>
            if(!instructorFlag!!) {
                myAdapter = BookingListAdapter(thiz.requireContext(), 0, myList, id_cust!!,id_istr!!,day!!,month!!,year!!)
            }
            else {
                myAdapter = LessonListAdapter(thiz.requireContext(), 0, myList,id_istr!!,day!!,month!!,year!!)
            }
            withContext(Dispatchers.Main) {
                myListView.adapter = myAdapter!!
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
                    //prima chiamavo qui direttamente refresh frag e
                    //buggava l' app perchè il listener rimaneva attivo
                    //e il fragment rimaneva vivo staccato dall'attività
                    //non capisco perchè ora funziona se cambio il frag attraverso
                    //il datepickerdialog, che non chiude il listener
                    listenComplete()
                }
                //non interessa la prima chiamata, solo quando i dati cambiano
                flag = true

            }
        return view
    }
    fun listenComplete() {
        doc!!.remove()
        (this.requireActivity() as MainInstructorActivity).refreshFragment(day!!,month!!,year!!)
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
        fun newInstance(id_istr : String, id_cust : String?,flag : Boolean, day : Int, month : Int, year : Int) =
            TimeTableFragment().apply {
                arguments = Bundle().apply {
                    putString("id_istr",id_istr)
                    putString("id_cust",id_cust)
                    putBoolean("flag_istr",flag)
                    putInt("day",day)
                    putInt("month",month)
                    putInt("year",year)
                }
            }
    }
}