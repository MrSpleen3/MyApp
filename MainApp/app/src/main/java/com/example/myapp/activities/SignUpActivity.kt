package com.example.myapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.example.myapp.R
import com.example.myapp.fragments.SignUpCustomer
import com.example.myapp.fragments.SignUpInstructor

class SignUpActivity : AppCompatActivity() {
    var instFlag : Boolean = false
    var fragmentManager : FragmentManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        //render fragment
        this.fragmentManager = this.supportFragmentManager
        renderFragment()
    }

    private fun renderFragment() {
        val frag : Fragment
        if(instFlag){
            frag = SignUpInstructor()
        }
        else {
            frag = SignUpCustomer()
        }
        fragmentManager!!.commit {
            setReorderingAllowed(true)
            this.replace(R.id.fragmentContainerSign,frag)
        }
    }

    fun switchFragment() {
        instFlag = !instFlag
        renderFragment()
    }
}