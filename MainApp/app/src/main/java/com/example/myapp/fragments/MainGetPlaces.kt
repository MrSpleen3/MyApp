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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MainGetPlaces.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainGetPlaces : Fragment() {
    // TODO: Rename and change types of parameters
   // private var param1: String? = null
   // private var param2: String? = null

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
                val place = spinner.text.toString()
                (thiz.requireActivity() as MainCustomerActivity).renderMainFrag(place,null)
            }
        })
        return view
    }

/*    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MainGetPlaces.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainGetPlaces().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }*/
}