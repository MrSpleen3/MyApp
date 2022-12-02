package com.example.myapp.activities


import android.app.DatePickerDialog
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.example.myapp.R
import com.example.myapp.fragments.TimeTableFragment
import com.example.myapp.models.MyBackgroundService
import com.example.myapp.models.FirebaseDbWrapper
import com.example.myapp.models.MyRatingsAdapter
import com.example.myapp.models.InstructorListEl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class MainInstructorActivity : AppCompatActivity() {

    private var instructorId: String? = null
    var firebaseDbWrapper : FirebaseDbWrapper? = null
    var fragmentManager : FragmentManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_instructor)
        instructorId = intent.getStringExtra("id")
        val serviceIntent = Intent(this, MyBackgroundService :: class.java)
        serviceIntent.putExtra("id",instructorId)
        serviceIntent.putExtra("flag",true)
        startService(serviceIntent)
        firebaseDbWrapper =FirebaseDbWrapper(this)
        val textName : TextView = findViewById(R.id.textViewName)
        val textSurname : TextView = findViewById(R.id.textViewSurname)
        val textPlace : TextView = findViewById(R.id.textViewPlace)
        GlobalScope.launch(Dispatchers.IO) {
            val myarray : Array<String> =firebaseDbWrapper!!.getMainInfo(instructorId!!)
            withContext(Dispatchers.Main) {
                textName.text = myarray[0]
                textSurname.text = myarray[1]
                textPlace.text= myarray[2]
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
        val textRate : TextView = findViewById(R.id.textViewYourRate)
        val layRate : LinearLayout = findViewById(R.id.switchVisIst)
        var flagRate : Boolean = false
        layRate.visibility =View.GONE
        val listRate : ListView = findViewById(R.id.listRate)
        val layContainer : LinearLayout = findViewById(R.id.layContainIstr)
        val r : Resources = thiz.resources
        val marginSmall : Int = getPx(r,5)
        val marginBig : Int = getPx(r,25)
        var isFirst = true
        textBook.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                flagvis = !flagvis
                if (flagvis){
                    lay.visibility=View.VISIBLE
                    textRate.visibility=View.GONE
                    layRate.visibility = View.GONE
                    layContainer.gravity = Gravity.TOP
                    val param = textBook.layoutParams as ViewGroup.MarginLayoutParams
                    param.setMargins(0,0,0,marginSmall)
                    textBook.layoutParams = param
                }
                else {
                    lay.visibility=View.GONE
                    textRate.visibility=View.VISIBLE
                    layContainer.gravity = Gravity.CENTER
                    val param = textBook.layoutParams as ViewGroup.MarginLayoutParams
                    param.setMargins(0,0,0,marginBig)
                    textBook.layoutParams = param
                }
            }
        })
        textSet.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val dialog : DatePickerDialog = DatePickerDialog(thiz,android.R.style.Theme_Holo_Light_Dialog_MinWidth,DatePickerDialog.OnDateSetListener { view , myear, mmonth, mdayOfMonth ->
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
                    layRate.visibility=View.VISIBLE
                    lay.visibility=View.GONE
                    textBook.visibility= View.GONE
                    layContainer.gravity = Gravity.TOP
                    val param = textRate.layoutParams as ViewGroup.MarginLayoutParams
                    param.setMargins(0,0,0,marginSmall)
                    textRate.layoutParams = param
                }
                else {
                    layRate.visibility=View.GONE
                    textBook.visibility= View.VISIBLE
                    layContainer.gravity = Gravity.CENTER
                    val param = textRate.layoutParams as ViewGroup.MarginLayoutParams
                    param.setMargins(0,marginBig,0,0)
                    textRate.layoutParams = param
                }
            }
        })
        GlobalScope.launch(Dispatchers.IO) {
            val mylist : MutableList <InstructorListEl>? = firebaseDbWrapper!!.getRatings(instructorId!!)
            if(mylist != null){
                val adapter = MyRatingsAdapter(thiz,0,mylist!!)
                withContext(Dispatchers.Main) {
                    listRate.adapter = adapter
                }
                firebaseDbWrapper!!.updateRating(instructorId!!,mylist)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.my_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val notifyintent : Intent = Intent(this,YourLessonsActivity::class.java)
        notifyintent.putExtra("flag_istr",true)
        notifyintent.putExtra("id",instructorId)
        this.startActivity(notifyintent)
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        val serviceIntent = Intent(this, MyBackgroundService :: class.java)
        stopService(serviceIntent)
        Log.d("wewe","serv stop")
        super.onDestroy()
    }

    private fun getPx(r : Resources,dp : Int) : Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp.toFloat(), r.getDisplayMetrics()).toInt()
    }

    private fun renderFrag(day : Int, month : Int, year : Int) {
        val frag : Fragment = TimeTableFragment.newInstance(instructorId!!,null,true,day,(month + 1),year)
        fragmentManager!!.commit {
            setReorderingAllowed(true)
            this.replace(R.id.fragmentContainerTimeTable,frag)
        }
    }
}