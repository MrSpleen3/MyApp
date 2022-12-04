package com.example.myapp.models

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.myapp.R

//adapter per TimeTable customer
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
        val lay : LinearLayout = vieww!!.findViewById(R.id.my_lay)
        val myBooking : TextView = vieww!!.findViewById(R.id.isFree)
        if(booking.id == null){
            myBooking.text="prenota"
            lay.setBackgroundColor(ContextCompat.getColor(context, R.color.green))
            lay.setOnClickListener(object : View.OnClickListener {
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
            if((booking.id == user_id)){
                if (booking.check!!) {
                    myBooking.text = "prenotata"
                    lay.setBackgroundColor(ContextCompat.getColor(context, R.color.light_blue))
                    //position 0 ha un bug, spiegazione a fine codice
                    if(position == 0){
                        lay.setOnClickListener(object : View.OnClickListener {
                            override fun onClick(v: View?) {
                            }
                        })
                    }

                }
                else {
                    myBooking.text = "In attesa di conferma"
                    lay.setBackgroundColor(ContextCompat.getColor(context, R.color.gray))
                    //position 0 ha un bug, spiegazione a fine codice
                    if(position == 0){
                        lay.setOnClickListener(object : View.OnClickListener {
                            override fun onClick(v: View?) {
                            }
                        })
                    }
                }
            }
            else {
                myBooking.text = "occupato"
                lay.setBackgroundColor(ContextCompat.getColor(context, R.color.red))
                //position 0 ha un bug, spiegazione a fine codice
                if(position == 0){
                    lay.setOnClickListener(object : View.OnClickListener {
                        override fun onClick(v: View?) {
                        }
                    })
                }
            }
        }
        return vieww
    }
}//position 0 se non ha listener si comporta all' onclick come se fosse ultima position
//avente un listener, solo all'onclick... colore e stringa sono corretti.
//listener vuoto risolve il problema
//YourLessonsListAdapter funziona simile ma non ha lo stesso problema :(
//LessonListAdapter invece si
