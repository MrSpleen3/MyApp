package com.example.myapp.models

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RatingBar
import android.widget.TextView
import com.example.myapp.R
import com.example.myapp.activities.MainInstructorActivity

class MyListAdapter(context: Context, val resorce:Int, val instructors : List<InstructorListEl>) :
    ArrayAdapter<InstructorListEl>(context,resorce,instructors){
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val instructor : InstructorListEl = instructors.get(position)
        var vieww : View? = convertView
        if (vieww == null) {
            vieww =
                LayoutInflater.from(context).inflate(R.layout.instructors_list,parent,false)
        }
        val instName : TextView = vieww!!.findViewById(R.id.nameInstr)
        instName.text = instructor.name
        val instRate : RatingBar = vieww!!.findViewById(R.id.ratingInstr)
        instRate.numStars = instructor.rate
        instName.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val myintent: Intent = Intent(context, MainInstructorActivity::class.java)
                myintent.putExtra("id",instructor.id)
                myintent.putExtra("flag",false)
                context.startActivity(myintent)
            }
        })
        return vieww
    }
}