package com.example.myapp.models

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

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
                    //updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(context, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    //updateUI(null)
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
                    //updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(context, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    //updateUI(null)
                }
            }
    }

    fun signIn(email: String, password: String) {
        this.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    //updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(context, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    //updateUI(null)
                }
            }
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

    fun isInstructor(id: String): Boolean {
        val docRef = db.collection("Instructors").document(id)
        var flag: Boolean =false
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    flag = true
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
        return flag
    }
}
