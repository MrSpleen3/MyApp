package com.example.myapp.activities


import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.example.myapp.R
import com.example.myapp.fragments.TimeTableFragment
import com.example.myapp.models.FirebaseAuthWrapper
import com.example.myapp.models.FirebaseDbWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class MainInstructorActivity : AppCompatActivity() {

    private var instructorFlag: Boolean? = null
    private var instructorId: String? = null
    private var custromerId : String? = null
    var firebaseDbWrapper : FirebaseDbWrapper? = null
    var fragmentManager : FragmentManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_instructor)
        instructorFlag = intent.extras!!.getBoolean("flag")
        instructorId = intent.getStringExtra("id")
        firebaseDbWrapper =FirebaseDbWrapper(this)
        val firebaseAuthWrapper: FirebaseAuthWrapper = FirebaseAuthWrapper(this)
        custromerId = firebaseAuthWrapper.getId()
        val textName : TextView = findViewById(R.id.textViewName)
        val textPlace : TextView = findViewById(R.id.textViewPlace)
        GlobalScope.launch(Dispatchers.IO) {
            val myarray : Array<String> =firebaseDbWrapper!!.getMainInfo(instructorId!!)
            withContext(Dispatchers.Main) {
                textName.text = myarray[0]
                textPlace.text= myarray[1]
            }
        }
        fragmentManager = this.supportFragmentManager
        val thiz = this
        val textBook : TextView = findViewById(R.id.textViewBook)
        val lay : LinearLayout = findViewById(R.id.switchfrag)
        lay.visibility= View.GONE
        var flagvis : Boolean = false
        val calendar: Calendar= Calendar.getInstance()
        var year : Int = calendar.get(Calendar.YEAR)
        var month : Int = calendar.get(Calendar.MONTH)
        var day : Int = calendar.get(Calendar.DAY_OF_MONTH)
        val textSet : TextView = findViewById(R.id.textSetDate)
        if(instructorFlag!!) textBook.text = "Le tue lezioni"
        textBook.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                flagvis = !flagvis
                if (flagvis){
                    lay.visibility=View.VISIBLE
                }
                else {
                    lay.visibility=View.GONE
                }
            }
        })
        //TODO: need to implement the fragment
        textSet.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {

                val dialog : DatePickerDialog = DatePickerDialog(thiz,android.R.style.Theme_Holo_Light_Dialog_MinWidth,DatePickerDialog.OnDateSetListener { view , myear, mmonth, mdayOfMonth ->
                    day = mdayOfMonth
                    month= mmonth
                    year = myear
                    val frag : Fragment = TimeTableFragment.newInstance(instructorId!!,custromerId,instructorFlag!!,day,(month + 1),year)
                    fragmentManager!!.commit {
                        setReorderingAllowed(true)
                        this.replace(R.id.fragmentContainerTimeTable,frag)
                    }
                },year,month,day)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.datePicker.minDate = calendar.timeInMillis
                dialog.show()
            }
        })

    }
    fun refreshFragment(day : Int, month : Int, year : Int) {
        val frag: Fragment = TimeTableFragment.newInstance(
            instructorId!!,
            custromerId,
            instructorFlag!!,
            day,
            month,
            year
        )
        fragmentManager!!.commit {
            setReorderingAllowed(true)
            this.replace(R.id.fragmentContainerTimeTable, frag)
        }
    }
}