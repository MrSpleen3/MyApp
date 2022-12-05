package com.example.myapp.fragments

import android.app.DatePickerDialog
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.example.myapp.R
import com.example.myapp.models.FirebaseDbWrapper
import com.example.myapp.models.InstructorListEl
import com.example.myapp.models.MyRatingsAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class MainInstructorFragment : Fragment() {

    private var instructorId: String? = null
    var firebaseDbWrapper : FirebaseDbWrapper? = null
    val thiz = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            instructorId = it.getString("id_istr")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val context = this.requireContext()
        val vieww : View = inflater.inflate(R.layout.fragment_main_instructor, container, false)
        firebaseDbWrapper =FirebaseDbWrapper(context)
        val textName : TextView = vieww.findViewById(R.id.textViewName)
        val textSurname : TextView = vieww.findViewById(R.id.textViewSurname)
        val textPlace : TextView = vieww.findViewById(R.id.textViewPlace)
        GlobalScope.launch(Dispatchers.IO) {
            val myarray : Array<String> =firebaseDbWrapper!!.getMainInfo(instructorId!!)
            withContext(Dispatchers.Main) {
                textName.text = myarray[0]
                textSurname.text = myarray[1]
                textPlace.text= myarray[2]
            }
        }
        val textBook : TextView = vieww.findViewById(R.id.textViewBook)
        val lay : LinearLayout = vieww.findViewById(R.id.switchfrag)
        lay.visibility= View.GONE
        var flagvis : Boolean = false
        val calendar: Calendar = Calendar.getInstance()
        var year : Int = calendar.get(Calendar.YEAR)
        var month : Int = calendar.get(Calendar.MONTH)
        var day : Int = calendar.get(Calendar.DAY_OF_MONTH)
        val textSet : TextView = vieww.findViewById(R.id.textSetDate)
        val textRate : TextView = vieww.findViewById(R.id.textViewYourRate)
        val rateContainer : LinearLayout = vieww.findViewById(R.id.layRateIst)
        val bookContainer : LinearLayout = vieww.findViewById(R.id.layBookIst)
        val layRate : LinearLayout = vieww.findViewById(R.id.switchVisIst)
        var flagRate : Boolean = false
        layRate.visibility =View.GONE
        val listRate : ListView = vieww.findViewById(R.id.listRate)
        val layContainer : LinearLayout = vieww.findViewById(R.id.layContainIstr)
        val r : Resources = thiz.resources
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
                flagRate = !flagRate
                if (flagRate){
                    bookContainer.visibility=View.GONE
                    layRate.visibility=View.VISIBLE
                    lay.visibility=View.GONE
                    val param = textRate.layoutParams as ViewGroup.MarginLayoutParams
                    param.setMargins(marginBig,marginBig,marginBig,marginSmall)
                    textRate.layoutParams = param
                }
                else {
                    layRate.visibility=View.GONE
                    bookContainer.visibility=View.VISIBLE
                    val param = textRate.layoutParams as ViewGroup.MarginLayoutParams
                    param.setMargins(marginBig,marginBig,marginBig,marginBig)
                    textRate.layoutParams = param
                }
            }
        })
        GlobalScope.launch(Dispatchers.IO) {
            val mylist : MutableList <InstructorListEl>? = firebaseDbWrapper!!.getRatings(instructorId!!)
            if(mylist != null){
                val adapter = MyRatingsAdapter(context,0,mylist!!)
                withContext(Dispatchers.Main) {
                    listRate.adapter = adapter
                }
                firebaseDbWrapper!!.updateRating(instructorId!!,mylist)
            }
        }
        return vieww
    }

    private fun renderFrag(day : Int, month : Int, year : Int) {
        val frag : Fragment = TimeTableFragment.newInstance(instructorId!!,null,true,day,(month + 1),year)
        requireFragmentManager().commit {
            setReorderingAllowed(true)
            this.replace(R.id.fragmentContainerTimeTable,frag)
        }
    }

    private fun getPx(r : Resources,dp : Int) : Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp.toFloat(), r.getDisplayMetrics()).toInt()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            MainInstructorFragment().apply {
                arguments = Bundle().apply {
                    putString("id_istr", param1)
                }
            }
    }
}