package com.example.myapp.fragments

import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.view.marginBottom
import androidx.fragment.app.commit
import com.example.myapp.R
import com.example.myapp.activities.MainCustomerActivity
import com.example.myapp.models.FirebaseDbWrapper
import com.example.myapp.models.MyRate
import com.google.firebase.Timestamp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MainCustInstructor.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainCustInstructor : Fragment() {
    // TODO: Rename and change types of parameters
    private var instructorId: String? = null
    private var custromerId : String? = null
    private var place : String? = null
    var firebaseDbWrapper : FirebaseDbWrapper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            instructorId = it.getString("id_istr")
            custromerId = it.getString("id_cust")
        }
        val thiz = this
        this.requireActivity().onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d("bo","back")
                (thiz.requireActivity() as MainCustomerActivity).renderMainFrag(place,null)

            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val context = this.requireContext()
        val vieww : View = inflater.inflate(R.layout.fragment_main_cust_instructor, container, false)
        firebaseDbWrapper =FirebaseDbWrapper(context)
        val textName : TextView = vieww.findViewById(R.id.textViewNameIstr)
        val textSurname : TextView = vieww.findViewById(R.id.textViewSurnameIstr)
        val textPlace : TextView = vieww.findViewById(R.id.textViewPlaceIstr)
        GlobalScope.launch(Dispatchers.IO) {
            val myarray : Array<String> =firebaseDbWrapper!!.getMainInfo(instructorId!!)
            withContext(Dispatchers.Main) {
                textName.text = myarray[0]
                textSurname.text = myarray[1]
                textPlace.text= myarray[2]
                place = myarray[2]
            }
        }
        val textBook : TextView = vieww.findViewById(R.id.textViewBookLes)
        val lay : LinearLayout = vieww.findViewById(R.id.switchfragCust)
        lay.visibility= View.GONE
        var flagvis : Boolean = false
        val calendar: Calendar = Calendar.getInstance()
        var year : Int = calendar.get(Calendar.YEAR)
        var month : Int = calendar.get(Calendar.MONTH)
        var day : Int = calendar.get(Calendar.DAY_OF_MONTH)
        val textSet : TextView = vieww.findViewById(R.id.textSetDateCust)
        val textRate : TextView = vieww.findViewById(R.id.textViewRateIst)
        var flagvisRate : Boolean = false
        val rateBar : RatingBar = vieww.findViewById(R.id.ratingBarIst)
        rateBar.numStars=5
        var id_rate : String? = null
        val layRate : LinearLayout = vieww.findViewById(R.id.switchVis)
        layRate.visibility= View.GONE
        val addRate : Button = vieww.findViewById(R.id.buttonAddRate)
        val layContainer : LinearLayout = vieww.findViewById(R.id.layContain)
        textBook.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                flagvis = !flagvis
                if (flagvis){
                    lay.visibility=View.VISIBLE
                    textRate.visibility=View.GONE
                    layRate.visibility = View.GONE
                    layContainer.gravity = Gravity.TOP
                    val param = textBook.layoutParams as ViewGroup.MarginLayoutParams
                    param.setMargins(0,0,0,5)
                    textBook.layoutParams = param
                }
                else {
                    lay.visibility=View.GONE
                    textRate.visibility=View.VISIBLE
                    layContainer.gravity = Gravity.CENTER
                    val param = textBook.layoutParams as ViewGroup.MarginLayoutParams
                    param.setMargins(0,0,0,25)
                    textBook.layoutParams = param
                }
            }
        })
        textSet.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {

                val dialog : DatePickerDialog = DatePickerDialog(context,android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    DatePickerDialog.OnDateSetListener { view, myear, mmonth, mdayOfMonth ->
                    day = mdayOfMonth
                    month= mmonth
                    year = myear
                    val frag : Fragment = TimeTableFragment.newInstance(instructorId!!,custromerId,false,day,(month + 1),year)
                    fragmentManager!!.commit {
                        setReorderingAllowed(true)
                        this.replace(R.id.fragmentContainerTimeTableCust,frag)
                    }
                },year,month,day)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.datePicker.minDate = calendar.timeInMillis
                dialog.show()
            }
        })
        textRate.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                flagvisRate = !flagvisRate
                if (flagvisRate){
                    layRate.visibility=View.VISIBLE
                    lay.visibility=View.GONE
                }
                else {
                    layRate.visibility=View.GONE
                }
            }
        })
        GlobalScope.launch(Dispatchers.IO) {
            val rate : MyRate? = firebaseDbWrapper!!.getMyRate(instructorId!!,custromerId!!)
            withContext(Dispatchers.Main) {
                if(rate != null){
                    rateBar.rating = rate.vote.toFloat()
                    id_rate = rate.id
                }
            }
        }
        addRate.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                firebaseDbWrapper!!
                    .addRating(id_rate,rateBar.rating.toDouble(),custromerId!!,instructorId!!,Timestamp(calendar.time))
            }
        })
        return vieww
    }

    //TODO: customizza onBackPressed

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MainCustInstructor.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainCustInstructor().apply {
                arguments = Bundle().apply {
                    putString("id_istr", param1)
                    putString("id_cust", param2)
                }
            }
    }
}