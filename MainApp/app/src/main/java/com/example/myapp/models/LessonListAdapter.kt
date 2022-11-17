package com.example.myapp.models

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.myapp.R

class LessonListAdapter(context: Context, val resorce:Int, val bookings : List<BookingElement>) :
    ArrayAdapter<BookingElement>(context,resorce,bookings){
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val booking: BookingElement = bookings[position]
        var vieww: View? = convertView
        if (vieww == null) {
            vieww =
                LayoutInflater.from(context).inflate(R.layout.bookings_list, parent, false)
        }
        val mystring : String = ((position + 8).toString()).plus("-").plus((position + 9).toString())
        val myTimeSlot : TextView = vieww!!.findViewById(R.id.timeSlot)
        myTimeSlot.text = mystring
        val myBooking : TextView = vieww!!.findViewById(R.id.isFree)
        if((booking.id == null)||(booking.check == null)) {
            myBooking.text = "libero"
        }
        else {
            if(booking.check) {
                myBooking.text = "occupato"
            }
            else {
                myBooking.text= "conferma"
                myBooking.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View?) {
                        val builder = AlertDialog.Builder(context)
                        builder.setMessage("Conferma o Annulla.")
                            .setPositiveButton("Conferma",
                                DialogInterface.OnClickListener { dialog, id ->
                                    FirebaseDbWrapper(context).confrimLesson(booking.id_doc!!)
                                    dialog.cancel()
                                })
                            .setNegativeButton("Annulla",
                                DialogInterface.OnClickListener { dialog, id ->
                                    FirebaseDbWrapper(context).deleteLesson(booking.id_doc!!)
                                    dialog.cancel()
                                })
                        builder.create()
                        builder.show()
                    }
                })
            }
        }
        return vieww
    }
}
