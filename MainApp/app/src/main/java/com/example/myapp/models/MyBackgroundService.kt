package com.example.myapp.models

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.myapp.R
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.Query

class MyBackgroundService : Service() {
    var id_not : Int = 0
    var id_cust : String? = null
    var doc : Query? = null
    var flag : Boolean? = null

    //TODO:aggiungi intent nella notifica
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("wewe","serv")
        val fire : FirebaseDbWrapper = FirebaseDbWrapper(this.applicationContext)
        id_cust = intent!!.getStringExtra("id")
        flag = intent!!.getBooleanExtra("flag",false)
        val docRef = fire.getCollection()
        if(!flag!!)this.doc=docRef.whereEqualTo("id_cust", id_cust)
        else this.doc=docRef.whereEqualTo("id_istr", id_cust)
        var first : Boolean = false
        this.doc!!.addSnapshotListener { value, e ->
            if (e != null) {
                Log.w(ContentValues.TAG, "Listen failed.", e)
                return@addSnapshotListener
            }
            if (!flag!!) {
                for (dc in value!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.MODIFIED -> dataChanged(
                            (dc.document.get("day") as Long).toInt(),
                            (dc.document.get("month") as Long).toInt(),
                            true,
                            id_cust!!,
                            true
                        )
                        DocumentChange.Type.REMOVED -> dataChanged(
                            (dc.document.get("day") as Long).toInt(),
                            (dc.document.get("month") as Long).toInt(),
                            false,
                            id_cust!!,
                            true
                        )
                        DocumentChange.Type.ADDED -> continue
                    }
                }
            } else {
                //se no ogni prima volta ritorna tutti i documenti con tale id
                if (first) {
                    for (dc in value!!.documentChanges) {
                        if ((dc.document.get("id_cust") as String) != id_cust) {
                            when (dc.type) {
                                DocumentChange.Type.MODIFIED -> continue
                                DocumentChange.Type.REMOVED -> continue
                                DocumentChange.Type.ADDED -> dataChanged(
                                    (dc.document.get("day") as Long).toInt(),
                                    (dc.document.get("month") as Long).toInt(),
                                    false,
                                    id_cust!!,
                                    false
                                )
                            }
                        }
                    }
                }
                else first=true
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    private fun dataChanged(day : Int, month : Int, flag_act : Boolean, id : String,flag_role : Boolean) {
        id_not++
        var notificationText : String? = null
        if(flag_role) {
            if (flag_act) notificationText = "la prenotazione del $day/$month è stata accettata"
            else notificationText = "la prenotazione del $day/$month è stata rifiutata"
        }
        else notificationText = "Prenotazione per il $day/$month ricevuta"
        val builder = NotificationCompat.Builder(this,"prova")
            .setContentTitle("Booking notification")
            .setSmallIcon(R.drawable.ic_notification_my)
            .setContentText(notificationText)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(notificationText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            //.setContentIntent()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("prova","name",NotificationManager.IMPORTANCE_DEFAULT).apply { description="Booking" }
            val notificationManager : NotificationManager =
                this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        with(NotificationManagerCompat.from(this)) {
            notify(id_not,builder.build())
        }

    }

}