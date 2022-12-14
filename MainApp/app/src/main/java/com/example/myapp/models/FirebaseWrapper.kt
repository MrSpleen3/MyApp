package com.example.myapp.models

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.myapp.activities.SplashActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat

class FirebaseAuthWrapper (private val context : Context){
    private var auth: FirebaseAuth = Firebase.auth

    fun isAuthenticated(): Boolean {
        return  auth.currentUser != null
    }
    //to call after isAut
    fun getId() : String {
        return auth.currentUser!!.uid
    }
    fun getName() : String {
        return auth.currentUser!!.displayName!!
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
            "place" to place,
            "rate" to (0.0 as Double)
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
        val myList : ArrayList<String> =  doc.get("city_list") as ArrayList<String>
        myList.sort()
        return myList
    }

    suspend fun getInstructorList(place: String?): List<InstructorListEl> {
        val docRef = db.collection("Instructors")
            .whereEqualTo("place",place).orderBy("name")
        val doc = docRef.get().await()
        val mylist : MutableList<InstructorListEl> = ArrayList<InstructorListEl>()
        for (document in doc.documents) {
            val id: String = document.id
            val istrName : String = (document.get("name").toString())+" "+(document.get("surname").toString().get(0))+"."
            val istRate : Double= document.get("rate") as Double
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
                    mylist.add(BookingElement((res.get("id_cust") as String), i, (res.get("check") as Boolean),(res.id as String),(res.get("name") as String)))
                    j++
                } else {
                    mylist.add(BookingElement(null, i, null,null,null))
                }
            }
            else {
                mylist.add(BookingElement(null, i, null,null,null))
            }
        }
        return mylist
    }
    suspend fun getMainInfo(id : String) : Array<String>{
        val docRef = db.collection("Instructors").document(id)
        val doc = docRef.get().await()
        return arrayOf((doc.get("name") as String),(doc.get("surname") as String),(doc.get("place") as String))
    }

    fun BookLesson(id : String,istr_id: String, timeSlot : Int, day :Int, month: Int, year: Int, name : String, nameIstr : String ) {
        val lesson = hashMapOf(
            "time_slot" to timeSlot,
            "day" to day,
            "month" to month,
            "year" to year,
            "id_istr" to istr_id,
            "id_cust" to id,
            "name" to name,
            "name_istr" to nameIstr,
            "check" to false
        )
        var flag : Boolean = false
        val id_doc = istr_id + day.toString() + month.toString() + year.toString() + timeSlot.toString()
        val docRef = db.collection("Bookings").document(id_doc)
        //transazione evita scrittura concorrente su risorsa condivisa
        db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            //documento non deve esistere se ?? la prima scittura
            //se esiste qualcun altro ha gi?? prenotato nel frattempo
            if(!snapshot.exists()) {
                transaction.set(docRef,lesson)
                null
            }
            else {
                throw FirebaseFirestoreException("too late",
                    FirebaseFirestoreException.Code.ALREADY_EXISTS)
            }
        }.addOnSuccessListener { result ->
            Log.d(TAG, "Transaction success: $result")
            Toast.makeText(context,"lezione prenoata",Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { e ->
            Log.w(TAG, "Transaction failure.", e)
            Toast.makeText(context,"prenotazione fallita",Toast.LENGTH_SHORT).show()
        }
    }

    fun confrimLesson(id : String, place : String) {
        val data = hashMapOf(
            "check" to true,
            "place" to place
        )
        val docRef = db.collection("Bookings").document(id)
        docRef.set(data, SetOptions.merge())
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
            "name" to " ",
            "name_istr" to " ",
            "check" to true
        )
        val id_doc = id + day.toString() + month.toString() + year.toString() + timeSlot.toString()
        val docRef = db.collection("Bookings").document(id_doc)
        db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            //documento non deve esistere se ?? la prima scittura
            //se esiste qualcun altro ha gi?? prenotato nel frattempo
            if(!snapshot.exists()) {
                transaction.set(docRef,lesson)
                null
            }
            else {
                throw FirebaseFirestoreException("too late",
                    FirebaseFirestoreException.Code.ALREADY_EXISTS)
            }
        }.addOnSuccessListener { result ->
            Log.d(TAG, "Transaction success: $result")
            Toast.makeText(context,"Sei occupato!",Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { e ->
            Log.w(TAG, "Transaction failure.", e)
            Toast.makeText(context,"operazione fallita",Toast.LENGTH_SHORT).show()
        }
    }
    fun getCollection() : CollectionReference {
        return db.collection("Bookings")
    }
    suspend fun getYourBookings(id : String,day : Int,month: Int,year: Int, flag : Boolean): MutableList<ElementList> {
        var docRef : Query? = null
        var list : MutableList<ElementList> = ArrayList<ElementList>()
        val sdf : SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
        val today = sdf.parse("$day/$month/$year")
        if(flag) docRef = db.collection("Bookings").whereEqualTo("id_istr",id)
        else docRef = db.collection("Bookings").whereEqualTo("id_cust",id)
        val doc = docRef.orderBy("time_slot").get().await()
        for(document in doc.documents){
            val bookDay = sdf.parse("${document.get("day")}/${document.get("month")}/${document.get("year")}")
            if((bookDay.compareTo(today) >= 0)&&((document.get("id_cust")as String)!=(document.get("id_istr")as String))){
                list.add(ElementList((document.get("day") as Long).toInt(),(document.get("month") as Long).toInt(),(document.get("year") as Long).toInt(),(document.get("time_slot") as Long).toInt(),(document.get("check")as Boolean),document.id,document.get("place").toString(),document.get("name").toString(),document.get("name_istr").toString()))
            }
        }
        return list
    }

    suspend fun getMyRate(id_istr : String, id_cust : String) : MyRate? {
        val docRef = db.collection("Ratings")
            .whereEqualTo("id_istr",id_istr)
            .whereEqualTo("id_cust",id_cust)
        val doc = docRef.get().await()
        if(doc.documents.isEmpty()) return null
        else return MyRate(doc.documents[0].id,doc.documents[0].get("vote") as Double)
    }

    //si pu?? valutare solo se si ha gi?? fatto una lezione
    suspend fun canRate( id : String,id_istr: String, day : Int, month: Int, year: Int) : Boolean {
        val sdf : SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
        val today = sdf.parse("$day/$month/$year")
        val docRef = db.collection("Bookings")
            .whereEqualTo("id_cust", id)
            .whereEqualTo("id_istr", id_istr)
        val doc = docRef.get().await()
        if(!doc.documents.isEmpty()) {
            for (document in doc.documents) {
                val bookDay =
                    sdf.parse("${document.get("day")}/${document.get("month")}/${document.get("year")}")
                if (bookDay.compareTo(today) <= 0) {
                    return true
                }
            }
        }
        return false
    }

    fun addRating(id_doc : String?, vote : Double, id_cust : String,id_istr: String,date : Timestamp){
        val rate = hashMapOf(
            "id_cust" to id_cust,
            "id_istr" to id_istr,
            "date" to date,
            "vote" to vote
        )
        var docRef : DocumentReference? = null
        if (id_doc != null) docRef = db.collection("Ratings").document(id_doc)
        else docRef = db.collection("Ratings").document()
        docRef.set(rate).addOnSuccessListener {
            Log.d(TAG, "DocumentSnapshot added")
            Toast.makeText(context, "Valutazione effettuata!",
                Toast.LENGTH_SHORT).show()}
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
                Toast.makeText(context, "Operazione fallita.",
                    Toast.LENGTH_SHORT).show()
            }

    }
    suspend fun getRatings(id: String) : MutableList<InstructorListEl>? {
        val docRef = db.collection("Ratings")
            .whereEqualTo("id_istr",id)
            .orderBy("date",Query.Direction.DESCENDING)
        val doc = docRef.get().await()
        val myList : MutableList<InstructorListEl> = ArrayList()
        if(doc.documents.isEmpty()) return null
        else {
            for(document in doc.documents){
                val date = (document.get("date") as Timestamp).toDate()
                val string = "${date.date}/${date.month + 1}/${date.year + 1900}"
                myList.add(InstructorListEl(null,string,document.get("vote") as Double))
            }
        }
        return myList
    }

    fun updateRating(id : String, list : List<InstructorListEl>) {
        var rate : Double = 0.0
        for (element in list){
            rate += element.rate
        }
        rate /= list.size
        val newRate = hashMapOf("rate" to rate)
        val docRef = db.collection("Instructors").document(id)
        docRef.set(newRate, SetOptions.merge())
    }
}
