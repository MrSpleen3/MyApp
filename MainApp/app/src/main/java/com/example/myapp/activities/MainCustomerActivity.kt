package com.example.myapp.activities


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapp.R
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.example.myapp.fragments.MainGetInstructors
import com.example.myapp.fragments.MainGetPlaces
import com.example.myapp.models.MyBackgroundService

//Gestisce i Main fragments
class MainCustomerActivity : AppCompatActivity() {

    var fragmentManager : FragmentManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_customer)
        //quando chiudo l'app il servizio la fa ripartire parzialmente...
        //val serviceIntent = Intent(this,MyBackgroundService :: class.java)
        //startService(serviceIntent)
        this.fragmentManager = this.supportFragmentManager
        renderMainFrag(null)
    }

    fun renderMainFrag(place : String?) {
        val frag: Fragment
        if(place != null) {
            frag = MainGetInstructors.newInstance(place)
        }
        else{
            frag = MainGetPlaces()
        }
        fragmentManager!!.commit {
            setReorderingAllowed(true)
            this.replace(R.id.fragmentContainerMain, frag)
        }
    }
}