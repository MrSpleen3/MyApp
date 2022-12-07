package com.example.myapp.models

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.myapp.R

//adapter per YourLessonsFragment
class YourLessonsListAdapter(context: Context, val resorce:Int, val list : List<ElementList>, val flag_istr : Boolean) :
    ArrayAdapter<ElementList>(context,resorce,list) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val element: ElementList = list.get(position)
        var vieww: View? = convertView
        if (vieww == null) {
            vieww =
                LayoutInflater.from(context).inflate(R.layout.notif_list, parent, false)
        }
        val msgFirst: TextView = vieww!!.findViewById(R.id.textViewNotif)
        val msgSecond: TextView = vieww!!.findViewById(R.id.textViewNotPlace)
        val lay : CardView = vieww!!.findViewById(R.id.list_cardview)
        val txt1 : String = "${element.day}/${element.month}/${element.year} ore ${element.timeSlot + 7} : "
        var txt2: String? = null
        msgFirst.text = txt1
        if(!flag_istr) {
            if (element.flag) {
                msgSecond.visibility = View.VISIBLE
                txt2 = "lezione prenotata, maestro: ${element.name_istr}"
                msgSecond.text = "Presso: ${element.place}"
            } else txt2 = "in attesa di conferma dal maestro ${element.name_istr}"
        }
        else{
            if (element.flag) {
                msgSecond.visibility = View.VISIBLE
                txt2 = "lezione prenotata, cliente : ${element.name}"
                msgSecond.text = "Presso: ${element.place}"
            } else {
                txt2 = "conferma la prenotazione di ${element.name}"
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
                                    FirebaseDbWrapper(context).confrimLesson(element.id,luogo.text.toString())
                                    dialog.dismiss()
                                }
                            }
                            })
                        del.setOnClickListener(object : View.OnClickListener {
                            override fun onClick(v: View?) {
                                FirebaseDbWrapper(context).deleteLesson(element.id)
                                dialog.dismiss()
                            }
                        })
                    }
                })
            }
        }
        msgFirst.text = txt1+txt2
        return vieww
    }
}