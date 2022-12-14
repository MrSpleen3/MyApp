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

class SignUpInstructor : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_sign_up_instructor, container, false)
        val linkReg: TextView = view.findViewById(R.id.textViewInstr1)
        val linkLog : TextView = view.findViewById(R.id.textViewBackLogI)
        val email : EditText = view.findViewById(R.id.editTextTextInstrEmailAddress)
        val password : EditText = view.findViewById(R.id.editTextTextInstrPassword)
        val name : EditText = view.findViewById(R.id.editTextTextInstrName)
        val surname : EditText = view.findViewById(R.id.editTextTextInstrSurname)
        val licenceId : EditText = view.findViewById(R.id.editTextTextInstrID)
        val thiz = this
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
        val spinner: TextView = view.findViewById(R.id.search_spinner)
        val firebaseDbWrapper : FirebaseDbWrapper = FirebaseDbWrapper(thiz.requireContext())
        GlobalScope.launch(Dispatchers.IO) {
            val arr : ArrayList<String> = firebaseDbWrapper.getPlaces()
            withContext(Dispatchers.Main) {
                spinner.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View?) {
                        val dialog = Dialog(thiz.requireContext())
                        dialog.setContentView(R.layout.dialog_searchable_spinner)
                        dialog.window!!.setLayout(650, 800)
                        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        dialog.show()
                        val myList: ListView = dialog.findViewById(R.id.listViewSearch)
                        val searchText: EditText = dialog.findViewById(R.id.editTextSearch)
                        val adapter: ArrayAdapter<String> = ArrayAdapter(thiz.requireContext() , android.R.layout.simple_list_item_1, arr)
                        myList.adapter = adapter
                        searchText.addTextChangedListener(object : TextWatcher {
                            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                            }

                            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                                adapter.filter.filter(s)
                            }

                            override fun afterTextChanged(s: Editable?) {
                            }
                        })
                        myList.setOnItemClickListener( object  : AdapterView.OnItemClickListener{
                            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                spinner.text = adapter.getItem(position)
                                dialog.dismiss()
                            }

                        })
                    }
                })
            }
        }
        val button: Button = view.findViewById(R.id.buttonInstr)
        button.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val condition : Boolean = !email.text.isEmpty()&&!password.text.isEmpty()&&!spinner.text.isEmpty()&&!surname.text.isEmpty()&&!licenceId.text.isEmpty()&&!name.text.isEmpty()
                if(condition) {
                    val firebaseAuthWrapper: FirebaseAuthWrapper =
                        FirebaseAuthWrapper(thiz.requireContext())
                    firebaseAuthWrapper.signUpInstructor(
                        email.text.toString(),
                        password.text.toString(),
                        name.text.toString(),
                        surname.text.toString(),
                        licenceId.text.toString(),
                        spinner.text.toString()
                    )
                }
                else{
                    Toast.makeText(thiz.requireContext(),"Non lasciare campi vuoti!",Toast.LENGTH_SHORT)
                }
            }
        })
        return view
    }
}