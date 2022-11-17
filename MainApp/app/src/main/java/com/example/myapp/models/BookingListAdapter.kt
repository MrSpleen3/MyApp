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

class BookingListAdapter(context: Context, val resorce:Int, val bookings : List<BookingElement>,val user_id :String) :
    ArrayAdapter<BookingElement>(context,resorce,bookings){
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val booking: BookingElement = bookings[position]
        var vieww: View? = convertView
        if (vieww == null) {
            vieww =
                LayoutInflater.from(context).inflate(R.layout.bookings_list, parent, false)
        }
        val mystring : String = ((position + 7).toString()).plus("-").plus((position + 8).toString())
        val myTimeSlot : TextView = vieww!!.findViewById(R.id.timeSlot)
        myTimeSlot.text = mystring
        val myBooking : TextView = vieww!!.findViewById(R.id.isFree)
        if((booking.id == null)||(booking.check == null)){
            myBooking.text="prenota"
            myBooking.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    val builder = AlertDialog.Builder(context)
                    builder.setMessage("Sicuro di voler prenotare la lezione?")
                        .setPositiveButton("Prenota",
                            DialogInterface.OnClickListener { dialog, id ->
                                FirebaseDbWrapper(context).BookLesson(user_id,booking.timeSlot)
                                dialog.cancel()
                            })
                        .setNegativeButton("Indietro",
                            DialogInterface.OnClickListener { dialog, id ->
                                dialog.cancel()
                            })
                    builder.create()
                    builder.show()
                }
            })
        }
        else {
            myBooking.text="occupato"
        }
        return vieww
    }
}
