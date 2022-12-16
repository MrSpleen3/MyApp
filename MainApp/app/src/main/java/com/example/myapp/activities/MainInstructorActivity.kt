package com.example.myapp.activities


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.example.myapp.R
import com.example.myapp.fragments.MainInstructorFragment
import com.example.myapp.fragments.YourLessonsFragment
import com.example.myapp.service.MyBackgroundService

class MainInstructorActivity : AppCompatActivity() {

    private var instructorId: String? = null
    var fragmentManager : FragmentManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_instructor)
        instructorId = intent.getStringExtra("id")
        val serviceIntent = Intent(this, MyBackgroundService :: class.java)
        serviceIntent.putExtra("id",instructorId)
        serviceIntent.putExtra("flag",true)
        startService(serviceIntent)
        this.fragmentManager = this.supportFragmentManager
        val frag: Fragment = MainInstructorFragment.newInstance(instructorId!!)
        fragmentManager!!.commit {
            setReorderingAllowed(true)
            this.replace(R.id.fragmentContainerMainIstr, frag)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.my_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val frag: Fragment = YourLessonsFragment.newInstance(instructorId!!,true,null,null)
        fragmentManager!!.commit {
            setReorderingAllowed(true)
            this.replace(R.id.fragmentContainerMainIstr, frag)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        val serviceIntent = Intent(this, MyBackgroundService :: class.java)
        stopService(serviceIntent)
        Log.d("wewe","serv stop")
        super.onDestroy()
    }

    fun renderMainFrag() {
        val frag: Fragment = MainInstructorFragment.newInstance(instructorId!!)
        fragmentManager!!.commit {
            setReorderingAllowed(true)
            this.replace(R.id.fragmentContainerMainIstr, frag)
        }
    }
}