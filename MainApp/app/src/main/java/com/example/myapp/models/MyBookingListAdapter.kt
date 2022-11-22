package com.example.myapp.models

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.myapp.R

class MyBookingListAdapter(context: Context, val resorce:Int, val list : List<ElementList>) :
    ArrayAdapter<ElementList>(context,resorce,list) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val element: ElementList = list.get(position)
        var vieww: View? = convertView
        if (vieww == null) {
            vieww =
                LayoutInflater.from(context).inflate(R.layout.notif_list, parent, false)
        }
        val msg: TextView = vieww!!.findViewById(R.id.textViewNotif)
        var txt: String? = null
        if (element.flag) {
            txt = "${element.day}/${element.month}/${element.year} ore ${element.timeSlot + 8} : lezione prenotata"
        } else txt =
            "${element.day}/${element.month}/${element.year} ore ${element.timeSlot + 8} : in attesa di conferma"
        msg.text = txt
        return vieww
    }
}