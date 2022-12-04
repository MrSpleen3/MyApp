package com.example.myapp.fragments


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import android.widget.ListView
import androidx.activity.OnBackPressedCallback
import com.example.myapp.R
import com.example.myapp.activities.MainCustomerActivity
import com.example.myapp.models.FirebaseDbWrapper
import com.example.myapp.models.InstructorListEl
import com.example.myapp.models.MyListAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//Prende il luogo d'interesse dall'altro frag
//e ritorna una lista d'istruttori relativa al luogo
class InstructorsInPlaceFragment() : Fragment() {
    // TODO: Rename and change types of parameters
    private var place: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            place = it.getString("place")
        }
        val thiz = this
        this.requireActivity().onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d("bo","back")
                (thiz.requireActivity() as MainCustomerActivity).renderMainFrag(null,null)

            }
        })
    }
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val thiz = this
        val view : View = inflater.inflate(R.layout.fragment_main_get_instructors, container, false)
        val istrList : ListView = view.findViewById(R.id.instructorList)
        val firebaseDbWrapper : FirebaseDbWrapper = FirebaseDbWrapper(thiz.requireContext())
        GlobalScope.launch(Dispatchers.IO) {
            val mylist : List <InstructorListEl> = firebaseDbWrapper.getInstructorList(place)
            val adapter : ListAdapter = MyListAdapter(thiz.requireActivity(),0,mylist) as ListAdapter
            withContext(Dispatchers.Main) {
                istrList.adapter = adapter
            }
        }
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(place: String) =
            InstructorsInPlaceFragment().apply {
                arguments = Bundle().apply {
                    putString("place", place)
                }
            }
    }
}
