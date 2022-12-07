package com.example.myapp.activities


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.example.myapp.R
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.example.myapp.fragments.YourInstructorFragment
import com.example.myapp.fragments.InstructorsInPlaceFragment
import com.example.myapp.fragments.PlacesFragment
import com.example.myapp.fragments.YourLessonsFragment
import com.example.myapp.service.MyBackgroundService

//Gestisce i Main fragments
class MainCustomerActivity : AppCompatActivity() {

    var fragmentManager : FragmentManager? = null
    var id : String? = null
    var name : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_customer)
        id = intent!!.extras!!.getString("id")
        name = intent!!.extras!!.getString("name")
       // val serviceIntent = Intent(this, MyBackgroundService :: class.java)
       // serviceIntent.putExtra("id",id)
       // serviceIntent.putExtra("flag",false)
       // startService(serviceIntent)
        this.fragmentManager = this.supportFragmentManager
        renderMainFrag(null,null)
    }

    fun renderMainFrag(place : String?,id_istr : String?) {
        val frag: Fragment
        if(place != null) {
            frag = InstructorsInPlaceFragment.newInstance(place)
        }
        else if (id_istr != null){
            frag = YourInstructorFragment.newInstance(id_istr,id!!,name!!)
        }
        else{
            frag = PlacesFragment()
        }
        fragmentManager!!.commit {
            setReorderingAllowed(true)
            this.replace(R.id.fragmentContainerMain, frag)
        }
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.my_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val frag: Fragment = YourLessonsFragment.newInstance(id!!,false)
        fragmentManager!!.commit {
            setReorderingAllowed(true)
            this.replace(R.id.fragmentContainerMain, frag)
        }
        return super.onOptionsItemSelected(item)
    }
    //Quando passo a un altra activity viene chiamato
    //quindi o lo restarto in onResume o lo chiamo direttam in onDestroy
    /*override fun onStop() {
        val serviceIntent = Intent(this, MyBackgroundService :: class.java)
        stopService(serviceIntent)
        Log.d("wewe","serv stop")
        super.onStop()
    }*/
    //il web dice che non viene sempre invocato, a me non sta dando problemi
    override fun onDestroy() {
        val serviceIntent = Intent(this, MyBackgroundService :: class.java)
        stopService(serviceIntent)
        Log.d("wewe","serv stop")
        super.onDestroy()
    }
}