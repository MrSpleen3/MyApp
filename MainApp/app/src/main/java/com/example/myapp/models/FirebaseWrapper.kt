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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import javax.xml.transform.Source

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

    fun signUpInstructor(email: String, password: String, name : String, surname : String, licenceId: String) {
        this.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    val id: String = user!!.uid
                    FirebaseDbWrapper(context).addInstructor(id, name, surname, licenceId)
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

    fun addInstructor(id: String, name: String,surname: String,licenceId: String) {
        val user = hashMapOf(
            "name" to name,
            "surname" to surname,
            "licenceId" to licenceId
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
    fun isInstructor(id: String) {
        val docRef = db.collection("Instructors").document(id)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    instructorSuccess(true)
                }
                else {
                    Log.d(TAG, "No such document")
                    instructorSuccess(false)
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
                Toast.makeText(context, "Can't reach DB.",
                    Toast.LENGTH_SHORT).show()
            }
    }

    private fun instructorSuccess(boolean: Boolean) {
        var intent : Intent? =null
        if(boolean) {
            intent = Intent(this.context, MainInstructorActivity::class.java)
        }
        else {
            intent = Intent(this.context, MainCustomerActivity::class.java)
        }
        context.startActivity(intent!!)
    }
}
