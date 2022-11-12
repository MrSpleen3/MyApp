package com.example.myapp.fragments

import com.example.myapp.activities.MainCustomerActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
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
        val spinner: Spinner = view.findViewById(R.id.spinnerPlaces)
        val firebaseDbWrapper : FirebaseDbWrapper = FirebaseDbWrapper(thiz.requireContext())
        GlobalScope.launch(Dispatchers.IO) {
            val arr : ArrayList<String> = firebaseDbWrapper.getPlaces()
            val adapter: ArrayAdapter<String> = ArrayAdapter(thiz.requireContext() , android.R.layout.simple_spinner_item, arr)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            withContext(Dispatchers.Main) {
                spinner.adapter = adapter
            }
        }
        val button: Button = view.findViewById(R.id.mainCustomerButton)
        button.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val place = spinner.selectedItem.toString()
                (thiz.requireActivity() as MainCustomerActivity).renderMainFrag(place)
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