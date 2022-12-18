package com.example.myapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.myapp.R
import com.example.myapp.activities.LogActivity
import com.example.myapp.models.FirebaseAuthWrapper

class SignUpCustomer : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_sign_up_customer, container, false)
        val linkReg: TextView = view.findViewById(R.id.textViewCustomer)
        val linkLog : TextView = view.findViewById(R.id.textViewBackLogC)
        val thiz = this
        val email : EditText = view.findViewById(R.id.editTextTextEmailAddress)
        val password : EditText = view.findViewById(R.id.editTextTextPassword)
        val name : EditText = view.findViewById(R.id.editTextTextPersonName)
        val surname : EditText = view.findViewById(R.id.editTextTextPersonSurname)
        linkLog.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                (thiz.requireActivity() as LogActivity).switchLogFragment()
            }
        })
        linkReg.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                (thiz.requireActivity() as LogActivity).switchSignUpFragment()
            }
        })
        val button: Button = view.findViewById(R.id.buttonSignUpCustomer)
        button.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if(!email.text.isEmpty()&&!password.text.isEmpty()&&!name.text.isEmpty()&&!surname.text.isEmpty()) {
                    val firebaseAuthWrapper: FirebaseAuthWrapper =
                        FirebaseAuthWrapper(thiz.requireContext())
                    firebaseAuthWrapper.signUp(
                        email.text.toString(),
                        password.text.toString(),
                        (name.text.toString() + " " + surname.text.toString())
                    )
                }
                else{
                    Toast.makeText(thiz.requireContext(),"Non lasciare campi vuoti!", Toast.LENGTH_SHORT)
                }
            }
        })
        return view
    }
}