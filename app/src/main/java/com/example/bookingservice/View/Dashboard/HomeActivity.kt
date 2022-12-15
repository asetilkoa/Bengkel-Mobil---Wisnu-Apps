package com.example.bookingservice.View.Dashboard

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.example.bookingservice.R
import com.example.bookingservice.View.History.HistoryActivity
import com.example.bookingservice.View.InputBooking.BookingActivity
import com.example.bookingservice.View.LoginRegister.LoginActivity
import com.example.bookingservice.View.Main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var showname: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        showname = findViewById(R.id.usernametxt)
        auth = FirebaseAuth.getInstance()
        checkpremission()

        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("user").document(auth.getCurrentUser()!!.getUid())
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    showname.text = document.getString("nama")
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }

        logoutimg.setOnClickListener {
            auth.signOut()
            Intent(this@HomeActivity, MainActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }
        }

        bookingservis.setOnClickListener {
            Intent(this@HomeActivity, BookingActivity::class.java).also {
                startActivity(it)
            }
        }

        cekjadwal.setOnClickListener {
            Intent(this@HomeActivity, HistoryActivity::class.java).also {
                startActivity(it)
            }
        }

        lokasibkl.setOnClickListener {
            val gmmIntentUri = Uri.parse("geo:-7.728814, 110.364087?q=BMW Wisnu Auto Park")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }

        sosservice.setOnClickListener {
            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.data = Uri.parse("tel:082327704458")
            startActivity(callIntent)
        }

    }

    private fun checkpremission() {
//        untuk memberikan izin panggilan
        if (ActivityCompat.checkSelfPermission(this,android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CALL_PHONE),101)
        }
    }
}