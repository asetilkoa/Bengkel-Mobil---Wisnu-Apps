package com.example.bookingservice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HistoryActivity : AppCompatActivity() {
    private lateinit var dataRecyclerview : RecyclerView
    private lateinit var dataarrayList : ArrayList<ModelDatabase>
    private lateinit var auth: FirebaseAuth
    private var database = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        dataRecyclerview = findViewById(R.id.rvHistory)
        dataRecyclerview.layoutManager = LinearLayoutManager(this)
        dataRecyclerview.setHasFixedSize(true)

        dataarrayList = arrayListOf<ModelDatabase>()
//        getDatabasedata()

        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()
        database.collection("user").document(auth.getCurrentUser()!!.getUid())
            .collection("Data Booking").get()
            .addOnSuccessListener {
                if (!it.isEmpty){
                    for (dataSnapshot in it.documents){
                        val data:ModelDatabase? = dataSnapshot.toObject(ModelDatabase::class.java)
                        dataarrayList.add(data!!)
                    }
                }
                dataRecyclerview.adapter = HistoryAdapter(dataarrayList)
            } .addOnFailureListener {
                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
            }
    }

//    private fun getDatabasedata() {
//        database = FirebaseDatabase.getInstance().getReference("DataBooking")
//        database.addValueEventListener(object : ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if (snapshot.exists()){
//                    for (dataSnapshot in snapshot.children){
//                        val  data = dataSnapshot.getValue(ModelDatabase::class.java)
//                        dataarrayList.add(data!!)
//                    }
//
//                    dataRecyclerview.adapter = HistoryAdapter(dataarrayList)
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//
//        })
//    }
}