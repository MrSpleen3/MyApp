package com.example.myapp.models

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.myapp.activities.MainCustomerActivity
import com.example.myapp.activities.MainInstructorActivity
import com.example.myapp.activities.SplashActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirebaseAuthWrapper (private val context : Context){
    private var auth: FirebaseAuth = Firebase.auth

    fun isAuthenticated(): Boolean {
        return  auth.currentUser != null
    }
    //to call after isAut
    fun getId() : String {
        return auth.currentUser!!.uid
    }

    fun signUp(email: String,password: String, name : String) {
        this.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    val setName : UserProfileChangeRequest = userProfileChangeRequest {
                        displayName = name
                    }
                    user!!.updateProfile(setName)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d(TAG, "setName:success")
                                logSuccess()
                            }
                            else {
                                // If setName in fails, display a message to the user.
                                Log.w(TAG, "setName:failure", task.exception)
                                Toast.makeText(
                                    context, "Authentication pt2 failed.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(context, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun signUpInstructor(email: String, password: String, name : String, surname : String, licenceId: String, place : String) {
        this.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    val id: String = user!!.uid
                    FirebaseDbWrapper(context).addInstructor(id, name, surname, licenceId, place)
                    logSuccess()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(context, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun signIn(email: String, password: String) {
        this.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    logSuccess()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(context, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun logSuccess() {
        val intent : Intent = Intent(this.context, SplashActivity::class.java)
        context.startActivity(intent)

    }
}


class FirebaseDbWrapper (private val context: Context) {

    private val db = Firebase.firestore

    fun addInstructor(id: String, name: String,surname: String,licenceId: String, place : String) {
        val user = hashMapOf(
            "name" to name,
            "surname" to surname,
            "licenceId" to licenceId,
            "place" to place
        )
        db.collection("Instructors").document(id)
            .set(user)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot added with ID: $id") }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
                Toast.makeText(context, "Instructor creation failed.",
                    Toast.LENGTH_SHORT).show()
            }
    }

    suspend fun isInstructor(id: String) : Boolean {
        val docRef = db.collection("Instructors").document(id)
        val doc = docRef.get().await()
        return doc.data!=null
    }

    suspend fun getPlaces() : ArrayList<String> {
        val docRef = db.collection("Cities").document("list")
        val doc = docRef.get().await()
        return doc.get("city_list") as ArrayList<String>
    }

    suspend fun getInstructorList(place: String?): List<InstructorListEl> {
        val docRef = db.collection("Instructors")
            .whereEqualTo("place",place)
        val doc = docRef.get().await()
        val mylist : MutableList<InstructorListEl> = ArrayList<InstructorListEl>()
        for (document in doc.documents) {
            val id: String = document.id
            val istrName : String = document.get("name").toString()
            val istRate : Int= (document.get("rate") as Long).toInt()
            mylist.add(InstructorListEl(id,istrName,istRate))
        }
        return mylist
    }

    suspend fun getBookings(id : String,day : Int,month : Int, year : Int) : List<BookingElement> {
        val docRef = db.collection("Bookings")
            .whereEqualTo("id_istr",id)
            .whereEqualTo("day",day)
            .whereEqualTo("month",month)
            .whereEqualTo("year",year)
            .orderBy("time_slot")
        val doc = docRef.get().await()
        val mylist : MutableList<BookingElement> = ArrayList<BookingElement>()
        var j = 0
        val max = doc.documents.size
        for (i in 1..9) {
            if(j<max) {
                val res = doc.documents.get(j)
                if ((i == (res.get("time_slot") as Long).toInt())) {
                    mylist.add(BookingElement((res.get("id_cust") as String), i, (res.get("check") as Boolean),(res.id as String)))
                    j++
                } else {
                    mylist.add(BookingElement(null, i, null,null))
                }
            }
            else {
                mylist.add(BookingElement(null, i, null,null))
            }
        }
        return mylist
    }
    suspend fun getMainInfo(id : String) : Array<String>{
        val docRef = db.collection("Instructors").document(id)
        val doc = docRef.get().await()
        return arrayOf((doc.get("name") as String),(doc.get("place") as String))
    }

    fun BookLesson(id : String,istr_id: String, timeSlot : Int, day :Int, month: Int, year: Int ) {
        val lesson = hashMapOf(
            "time_slot" to timeSlot,
            "day" to day,
            "month" to month,
            "year" to year,
            "id_istr" to istr_id,
            "id_cust" to id,
            "check" to false
        )
        db.collection("Bookings").document()
            .set(lesson)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot added")
                Toast.makeText(context, "Lezione prenotata!",
                    Toast.LENGTH_SHORT).show()}
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
                Toast.makeText(context, "Prenotazione fallita.",
                    Toast.LENGTH_SHORT).show()
            }
    }

    fun confrimLesson(id : String) {
        val docRef = db.collection("Bookings").document(id)
        docRef.update("check",true)
    }
    fun deleteLesson(id : String) {
        val docRef = db.collection("Bookings").document(id)
        docRef.delete()
    }
    fun noLesson(id: String,day: Int,month: Int,year: Int,timeSlot: Int) {
        val lesson = hashMapOf(
            "time_slot" to timeSlot,
            "day" to day,
            "month" to month,
            "year" to year,
            "id_istr" to id,
            "id_cust" to id,
            "check" to true
        )
        db.collection("Bookings").document()
            .set(lesson)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot added")
                Toast.makeText(context, "Occupata!",
                    Toast.LENGTH_SHORT).show()}
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
                Toast.makeText(context, "Operazione fallita.",
                    Toast.LENGTH_SHORT).show()
            }
    }
    fun getCollection() : CollectionReference {
        return db.collection("Bookings")
    }
    suspend fun getYourBookings(id : String,day : Int,month: Int,year: Int, flag : Boolean): MutableList<ElementList> {
        var docRef : Query? = null
        var list : MutableList<ElementList> = ArrayList<ElementList>()
        if(flag) docRef = db.collection("Bookings").whereEqualTo("id_istr",id)
        else docRef = db.collection("Bookings").whereEqualTo("id_cust",id)
        val doc = docRef.orderBy("time_slot").get().await()
        for(document in doc.documents){
            if(((document.get("year") as Long).toInt() >= year)&&((document.get("month") as Long).toInt() >= month)&&((document.get("day") as Long).toInt() >= day)){
                list.add(ElementList((document.get("day") as Long).toInt(),(document.get("month") as Long).toInt(),(document.get("year") as Long).toInt(),(document.get("time_slot") as Long).toInt(),(document.get("check")as Boolean)))
            }
        }
        return list
    }

}
