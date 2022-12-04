package com.example.myapp.fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.example.myapp.activities.MainCustomerActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.myapp.R
import com.example.myapp.models.FirebaseDbWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlacesFragment : Fragment() {

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
        val view : View = inflater.inflate(R.layout.fragment_main_get_places, container, false)
        val thiz = this
        val spinner: TextView = view.findViewById(R.id.search_spinner_cust)
        val firebaseDbWrapper : FirebaseDbWrapper = FirebaseDbWrapper(thiz.requireContext())
        GlobalScope.launch(Dispatchers.IO) {
            val arr : ArrayList<String> = firebaseDbWrapper.getPlaces()
            withContext(Dispatchers.Main) {
                spinner.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View?) {
                        val dialog = Dialog(thiz.requireContext())
                        dialog.setContentView(R.layout.dialog_searchable_spinner)
                        dialog.window!!.setLayout(800, 1000)
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
        val button: Button = view.findViewById(R.id.mainCustomerButton)
        button.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (!spinner.text.isEmpty()) {
                    val place = spinner.text.toString()
                    (thiz.requireActivity() as MainCustomerActivity).renderMainFrag(place, null)
                }
                else{
                    Toast.makeText(thiz.requireContext(),"Inserire una località!",Toast.LENGTH_SHORT)
                }
            }
        })
        return view
    }

/*    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PlacesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }*/
}