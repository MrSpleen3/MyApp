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

class BookingListAdapter
    (context: Context,
     val resorce:Int,
     val bookings : List<BookingElement>,
     val user_id :String,
     val istr_id :String,
     val day : Int,
     val month : Int,
     val year: Int) :
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
        if(booking.id == null){
            myBooking.text="prenota"
            myBooking.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    val builder = AlertDialog.Builder(context)
                    builder.setMessage("Sicuro di voler prenotare la lezione?")
                        .setPositiveButton("Prenota",
                            DialogInterface.OnClickListener { dialog, id ->
                                FirebaseDbWrapper(context).BookLesson(user_id,istr_id,booking.timeSlot,day,month,year)
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
            //TODO: in attesa di conferma/confermata
            if((booking.id == user_id)){
                myBooking.text="prenotata"
            }
            else {
                myBooking.text = "occupato"
            }
        }
        return vieww
    }
}
