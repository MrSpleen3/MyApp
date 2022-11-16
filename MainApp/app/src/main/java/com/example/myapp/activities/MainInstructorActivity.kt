package com.example.myapp.activities


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.example.myapp.R
import com.example.myapp.models.FirebaseDbWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.sql.Timestamp
import java.util.Calendar

class MainInstructorActivity : AppCompatActivity() {

    private var instructorFlag: Boolean? = null
    private var instructorId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_instructor)
        instructorFlag = intent.extras!!.getBoolean("flag")
        instructorId = intent.getStringExtra("id")
        val text1 : TextView = findViewById(R.id.textView12)
        text1.text = instructorFlag.toString()
        val lay : LinearLayout = findViewById(R.id.layswitch)
        lay.visibility= View.GONE
        var flagvis : Boolean = false
        text1.setOnClickListener(object : View.OnClickListener {
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
      /*  val firebaseDbWrapper =FirebaseDbWrapper(this)
        GlobalScope.launch(Dispatchers.IO) {
            firebaseDbWrapper.getBookings(instructorId!!, 23,11,2022)
        }*/
    }
}