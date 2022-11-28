package com.example.myapp.models

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.myapp.R
import com.example.myapp.activities.YourLessonsActivity

class MyBookingListAdapter(context: Context, val resorce:Int, val list : List<ElementList>,val flag_istr : Boolean) :
    ArrayAdapter<ElementList>(context,resorce,list) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val element: ElementList = list.get(position)
        var vieww: View? = convertView
        if (vieww == null) {
            vieww =
                LayoutInflater.from(context).inflate(R.layout.notif_list, parent, false)
        }
        val msg: TextView = vieww!!.findViewById(R.id.textViewNotif)
        val lay : CardView = vieww!!.findViewById(R.id.list_cardview)
        val txt1 : String = "${element.day}/${element.month}/${element.year} ore ${element.timeSlot + 7} : "
        var txt2: String? = null
        msg.text = txt1
        if(!flag_istr) {
            if (element.flag) {
                txt2 = "lezione prenotata"
            } else txt2 = "in attesa di conferma"
        }
        else{
            if (element.flag) {
                txt2 = "lezione prenotata"
            } else {
                txt2 = "conferma la prenotazione"
                lay.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View?) {
                        val builder = AlertDialog.Builder(context)
                        builder.setMessage("Conferma o Annulla.")
                            .setPositiveButton("Conferma",
                                DialogInterface.OnClickListener { dialog, id ->
                                    FirebaseDbWrapper(context).confrimLesson(element.id)
                                    dialog.cancel()
                                    (context as YourLessonsActivity).refreshAdapter()
                                })
                            .setNegativeButton("Annulla",
                                DialogInterface.OnClickListener { dialog, id ->
                                    FirebaseDbWrapper(context).deleteLesson(element.id)
                                    dialog.cancel()
                                    (context as YourLessonsActivity).refreshAdapter()
                                })
                        builder.create()
                        builder.show()
                    }
                })
            }
        }
        msg.text = txt1+txt2
        return vieww
    }
}