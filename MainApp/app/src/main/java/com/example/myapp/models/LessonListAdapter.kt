package com.example.myapp.models

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import com.example.myapp.R

class LessonListAdapter(context: Context,
                        val resorce:Int,
                        val bookings : List<BookingElement>,
                        val id_istr : String,
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
        val lay : LinearLayout = vieww!!.findViewById(R.id.my_lay)
        myTimeSlot.text = mystring
        val myBooking : TextView = vieww!!.findViewById(R.id.isFree)
        if((booking.id == null)||(booking.check == null)) {
            myBooking.text = "Libero"
            lay.setBackgroundColor(ContextCompat.getColor(context, R.color.green))
            lay.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    val builder = AlertDialog.Builder(context)
                    builder.setMessage("Sei Occupato?")
                        .setPositiveButton("Occupato",
                            DialogInterface.OnClickListener { dialog, id ->
                                FirebaseDbWrapper(context).noLesson(id_istr,day,month,year,(position + 1))
                                dialog.cancel()
                            })
                        .setNegativeButton("Annulla",
                            DialogInterface.OnClickListener { dialog, id ->
                                dialog.cancel()
                            })
                    builder.create()
                    builder.show()
                }
            })
        }
        else {
            if(id_istr == booking.id){
                myBooking.text = "Occupato"
                lay.setBackgroundColor(ContextCompat.getColor(context, R.color.red))
                //position 0 ha un bug, spiegazione a fine codice
                if(position == 0){
                    lay.setOnClickListener(object : View.OnClickListener {
                        override fun onClick(v: View?) {
                        }
                    })
                }
            }
            else {
                if (booking.check) {
                    myBooking.text = "Lezione"
                    lay.setBackgroundColor(ContextCompat.getColor(context, R.color.light_blue))
                    //position 0 ha un bug, spiegazione a fine codice
                    if(position == 0){
                        lay.setOnClickListener(object : View.OnClickListener {
                            override fun onClick(v: View?) {
                            }
                        })
                    }
                } else {
                    myBooking.text = "Conferma Lezione"
                    lay.setBackgroundColor(ContextCompat.getColor(context, R.color.gray))
                    lay.setOnClickListener(object : View.OnClickListener {
                        override fun onClick(v: View?) {
                            val dialog = Dialog(context)
                            dialog.setContentView(R.layout.dialog_confrim_les)
                            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                            dialog.show()
                            val luogo: EditText = dialog.findViewById(R.id.editTextLuogo)
                            val confrim : Button = dialog.findViewById(R.id.buttonConferma)
                            val del : Button = dialog.findViewById(R.id.buttonAnnulla)
                            confrim.setOnClickListener(object : View.OnClickListener {
                                override fun onClick(v: View?) {
                                    if(luogo.text.toString() != ""){
                                        FirebaseDbWrapper(context).confrimLesson(booking.id_doc!!,luogo.text.toString())
                                        dialog.dismiss()
                                    }
                                }
                            })
                            del.setOnClickListener(object : View.OnClickListener {
                                override fun onClick(v: View?) {
                                    FirebaseDbWrapper(context).deleteLesson(booking.id_doc!!)
                                    dialog.dismiss()
                                }
                            })
                        }
                    })
                }
            }
        }
        return vieww
    }
}
//position 0 se non ha listener si comporta all' onclick come se fosse ultima position
//avente un listener, solo all'onclick... colore e stringa sono corretti.
//listener vuoto risolve il problema
//MyBookingListAdapter funziona simile ma non ha lo stesso problema :(
//BookinglistAdapter invece si