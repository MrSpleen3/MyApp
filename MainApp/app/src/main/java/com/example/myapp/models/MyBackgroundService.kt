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
import androidx.core.content.getSystemService
import com.example.myapp.R
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration

//non riesco a chiuderlo...
class MyBackgroundService : Service() {
    var id_not : Int = 0
    var doc : ListenerRegistration? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("wewe","serv")
        val fire : FirebaseDbWrapper = FirebaseDbWrapper(this.applicationContext)
        val id_cust : String = FirebaseAuthWrapper(this.applicationContext).getId()
        val docRef = fire.getCollection()
        this.doc=docRef.whereEqualTo("id_cust", id_cust)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Log.w(ContentValues.TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                for (dc in value!!.documentChanges) {
                    when(dc.type){
                        DocumentChange.Type.MODIFIED -> dataChanged((dc.document.get("day") as Long).toInt(),(dc.document.get("month") as Long).toInt(),true,id_cust)
                        DocumentChange.Type.REMOVED -> dataChanged((dc.document.get("day") as Long).toInt(),(dc.document.get("month") as Long).toInt(),false,id_cust)
                        DocumentChange.Type.ADDED -> continue
                    }
                }

            }
            return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    private fun dataChanged(day : Int, month : Int, flag : Boolean, id : String) {
        id_not++
        var notificationText : String? = null
        if(flag) notificationText = "la prenotazione del $day/$month è stata accettata"
        else notificationText = "la prenotazione del $day/$month è stata rifiutata"
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
    //doc mi appare null sempre però il listener si riattiva
    override fun onDestroy() {
        if(doc != null) {
            doc!!.remove()
        }
        super.onDestroy()
    }
}