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

class SignIn : Fragment() {

  /*  override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view : View = inflater.inflate(R.layout.fragment_sign_in, container, false)
        val thiz  =this
        val button: Button = view.findViewById(R.id.buttonSignIn)
        val email: EditText = view.findViewById(R.id.emailAddressSignIn)
        val password: EditText = view.findViewById(R.id.passwordSignIn)
        button.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if(!email.text.isEmpty()&&!password.text.isEmpty()) {
                    val firebaseAuthWrapper: FirebaseAuthWrapper =
                        FirebaseAuthWrapper(thiz.requireContext())
                    firebaseAuthWrapper.signIn(email.text.toString(), password.text.toString())
                }
                else{
                    Toast.makeText(thiz.requireContext(),"Non lasciare campi vuoti!",Toast.LENGTH_SHORT)
                }
            }
        })
        val link : TextView = view.findViewById(R.id.toSignUp)
        link.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                (thiz.requireActivity() as LogActivity).switchLogFragment()
            }
        })
        return view
    }

  /*  companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SignIn().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }*/
}