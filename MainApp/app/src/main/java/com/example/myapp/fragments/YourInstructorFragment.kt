package com.example.myapp.fragments

import android.app.DatePickerDialog
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
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
import androidx.fragment.app.FragmentManager
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


class YourInstructorFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var instructorId: String? = null
    private var custromerId : String? = null
    private var custromerName : String? = null
    private var instructorName : String? = null
    private var place : String? = null
    var firebaseDbWrapper : FirebaseDbWrapper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            instructorId = it.getString("id_istr")
            custromerId = it.getString("id_cust")
            custromerName = it.getString("name")
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
                instructorName=myarray[0]
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
        val rateContainer : LinearLayout = vieww.findViewById(R.id.layRate)
        val bookContainer : LinearLayout = vieww.findViewById(R.id.layBook)
        val layRate : LinearLayout = vieww.findViewById(R.id.switchVis)
        layRate.visibility= View.GONE
        val addRate : Button = vieww.findViewById(R.id.buttonAddRate)
        val r : Resources = this.resources
        val marginSmall : Int = getPx(r,10)
        val marginBig : Int = getPx(r,20)
        var isFirst = true
        textBook.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                flagvis = !flagvis
                if (flagvis){
                    rateContainer.visibility=View.GONE
                    lay.visibility=View.VISIBLE
                    layRate.visibility = View.GONE
                    val param = textBook.layoutParams as ViewGroup.MarginLayoutParams
                    param.setMargins(marginBig,marginBig,marginBig,marginSmall)
                    textBook.layoutParams = param
                }
                else {
                    lay.visibility=View.GONE
                    rateContainer.visibility=View.VISIBLE
                    val param = textBook.layoutParams as ViewGroup.MarginLayoutParams
                    param.setMargins(marginBig,marginBig,marginBig,marginBig)
                    textBook.layoutParams = param
                }
            }
        })
        textSet.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {

                val dialog : DatePickerDialog = DatePickerDialog(context,android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    DatePickerDialog.OnDateSetListener { view, myear, mmonth, mdayOfMonth ->
                        if(isFirst||mdayOfMonth!=day||mmonth!=month||myear!=year) {
                            day = mdayOfMonth
                            month = mmonth
                            year = myear
                            textSet.text = "$day/${month + 1}/$year"
                            textSet.textSize = 15.0F
                            renderFrag(day, month, year)
                            isFirst=false
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
                    bookContainer.visibility=View.GONE
                    layRate.visibility=View.VISIBLE
                    lay.visibility=View.GONE
                }
                else {
                    layRate.visibility=View.GONE
                    bookContainer.visibility=View.VISIBLE
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
    private fun getPx(r : Resources, dp : Int) : Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp.toFloat(), r.getDisplayMetrics()).toInt()
    }
    private fun renderFrag(day : Int, month : Int, year : Int) {
        val frag : Fragment = TimeTableFragment.newInstance(instructorId!!,custromerId,false,day,(month + 1),year,custromerName,instructorName)
        parentFragmentManager.commit {
            setReorderingAllowed(true)
            this.replace(R.id.fragmentContainerTimeTableCust,frag)
        }
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String,param3 : String) =
            YourInstructorFragment().apply {
                arguments = Bundle().apply {
                    putString("id_istr", param1)
                    putString("id_cust", param2)
                    putString("name", param3)

                }
            }
    }
}