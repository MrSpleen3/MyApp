package com.example.myapp.models

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.myapp.activities.MainCustomerActivity
import com.example.myapp.activities.MainInstructorActivity
import com.example.myapp.activities.SplashActivity
import com.example.myapp.fragments.InstructorListEl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
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
    /*
    L'idea era di ritornare un bool e gestire l'intent da spash, ma la lettura
    è async quindi non funzionerebbe:

    fun isInstructor(id: String): Boolean {
        val docRef = db.collection("Instructors").document(id)
        var flag : Boolean = false
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    instructorSuccess(true)
                }
                else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
        return flag
    }
     */
    suspend fun isInstructor(id: String) {
        val docRef = db.collection("Instructors").document(id)
        val doc = docRef.get().await()
        var intent : Intent? = null
        if (doc.data!=null){
            intent = Intent(this.context, MainInstructorActivity::class.java)
            intent.putExtra("flag",true)
            intent.putExtra("id",id)
        }
        else{
            intent = Intent(this.context, MainCustomerActivity::class.java)
        }
        context.startActivity(intent!!)
    }

    suspend fun getPlaces() : ArrayList<String> {
        val docRef = db.collection("Cities").document("list")
        val doc = docRef.get().await()
        return doc.get("city_list") as ArrayList<String>
    }

  /*  private fun instructorSuccess(boolean: Boolean) {
        var intent : Intent? =null
        if(boolean) {
            intent = Intent(this.context, MainInstructorActivity::class.java)
            intent.putExtra("flag",true)
            intent.putExtra("id",)
        }
        else {
            intent = Intent(this.context, MainCustomerActivity::class.java)
        }
        context.startActivity(intent!!)
    }*/

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
        for (i in 1..7) {
            val res = doc.documents[j]
            if(i == (res.get("time_slot") as Int)){
                mylist.add(BookingElement((res.get("id_cust") as String),i,(res.get("check") as Boolean)))
                j++
            }
            else {
                mylist.add(BookingElement(null,i,null))
            }
        }
        return mylist
    }
    suspend fun getMainInfo(id : String) : Array<String>{
        val docRef = db.collection("Instructors").document(id)
        val doc = docRef.get().await()
        return arrayOf((doc.get("name") as String),(doc.get("place") as String))
    }

    fun BookLesson(id : String, timeSlot : Int) {
        TODO()
    }

}
