package com.example.myapp.activities


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
import com.example.myapp.models.FirebaseDbWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.sql.Timestamp
import java.util.Calendar

class MainInstructorActivity : AppCompatActivity() {

    private var instructorFlag: Boolean? = null
    private var instructorId: String? = null
    var fragmentManager : FragmentManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_instructor)
        this.fragmentManager = this.supportFragmentManager
        val frag : Fragment = TimeTableFragment()
        fragmentManager!!.commit {
            setReorderingAllowed(true)
            this.replace(R.id.fragmentContainerTimeTable,frag)
        }
        instructorFlag = intent.extras!!.getBoolean("flag")
        instructorId = intent.getStringExtra("id")
        val textBook : TextView = findViewById(R.id.textViewBook)
        val lay : LinearLayout = findViewById(R.id.switchfrag)
        lay.visibility= View.GONE
        var flagvis : Boolean = false
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
        val calendar: Calendar= Calendar.getInstance()
        val today : Int = calendar.get(Calendar.DAY_OF_MONTH)
        val firstDay : Int = today - calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH) + 1
        val lastDay : Int = firstDay + 6
        val bo = true

      /*  val firebaseDbWrapper =FirebaseDbWrapper(this)
        GlobalScope.launch(Dispatchers.IO) {
            firebaseDbWrapper.getBookings(instructorId!!, 23,11,2022)
        }*/
    }
}