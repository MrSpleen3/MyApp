package com.example.myapp.fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.myapp.R
import com.example.myapp.activities.LogActivity
import com.example.myapp.models.FirebaseAuthWrapper
import com.example.myapp.models.FirebaseDbWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SignUpInstructor.newInstance] factory method to
 * create an instance of this fragment.
 */
class SignUpInstructor : Fragment() {
    // TODO: Rename and change types of parameters
 //   private var param1: String? = null
  //  private var param2: String? = null

   /* override fun onCreate(savedInstanceState: Bundle?) {
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
        val view: View = inflater.inflate(R.layout.fragment_sign_up_instructor, container, false)
        val link: TextView = view.findViewById(R.id.textViewInstr1)
        val thiz = this
        link.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                (thiz.requireActivity() as LogActivity).switchSignUpFragment()
            }
        })
        val spinner: TextView = view.findViewById(R.id.search_spinner)
        val firebaseDbWrapper : FirebaseDbWrapper = FirebaseDbWrapper(thiz.requireContext())
        GlobalScope.launch(Dispatchers.IO) {
            val arr : ArrayList<String> = firebaseDbWrapper.getPlaces()
            val adapter: ArrayAdapter<String> = ArrayAdapter(thiz.requireContext() , android.R.layout.simple_list_item_1, arr)
          //  val adapter: ArrayAdapter<String> = ArrayAdapter(thiz.requireContext() , android.R.layout.simple_spinner_item, arr)
           // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            withContext(Dispatchers.Main) {
                myList.adapter = adapter
            }
        }
        val button: Button = view.findViewById(R.id.buttonInstr)
        button.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val email : EditText = view.findViewById(R.id.editTextTextInstrEmailAddress)
                val password : EditText = view.findViewById(R.id.editTextTextInstrPassword)
                val name : EditText = view.findViewById(R.id.editTextTextInstrName)
                val surname : EditText = view.findViewById(R.id.editTextTextInstrSurname)
                val licenceId : EditText = view.findViewById(R.id.editTextTextInstrID)
                val firebaseAuthWrapper : FirebaseAuthWrapper = FirebaseAuthWrapper(thiz.requireContext())
                firebaseAuthWrapper.signUpInstructor(email.text.toString(),password.text.toString(),name.text.toString(),surname.text.toString(),licenceId.text.toString(),spinner.selectedItem.toString())
            }
        })
        return view
    }

  /*  companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SignUpInstructor.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SignUpInstructor().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    } */
}