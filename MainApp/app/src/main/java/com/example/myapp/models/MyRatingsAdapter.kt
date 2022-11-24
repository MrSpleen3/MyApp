package com.example.myapp.models

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RatingBar
import android.widget.TextView
import com.example.myapp.R

class MyRatingsAdapter(context: Context, val resorce:Int, val ratings : List<InstructorListEl>) :
    ArrayAdapter<InstructorListEl>(context,resorce,ratings) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rating: InstructorListEl = ratings.get(position)
        var vieww: View? = convertView
        if (vieww == null) {
            vieww =
                LayoutInflater.from(context).inflate(R.layout.instructors_list, parent, false)
        }
        val rateDate : TextView = vieww!!.findViewById(R.id.nameInstr)
        rateDate.text = rating.name
        val instRate : RatingBar = vieww!!.findViewById(R.id.ratingInstr)
        instRate.rating = rating.rate.toFloat()
    return vieww!!
    }
}