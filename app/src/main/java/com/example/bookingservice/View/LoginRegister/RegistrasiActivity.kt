package com.example.bookingservice.View.LoginRegister

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.example.bookingservice.R
import com.example.bookingservice.View.Dashboard.HomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_registrasi.*
import kotlin.collections.HashMap

class RegistrasiActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firebasefirestore: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrasi)

        auth = FirebaseAuth.getInstance()
        firebasefirestore = FirebaseFirestore.getInstance()

        Registrasibtn.setOnClickListener {
            val namausr  = namaEt.text.toString().trim()
            val alamat  = alamatEt.text.toString().trim()
            val nope  = nopeEt.text.toString().trim()
            val email = emailEt.text.toString().trim()
            val password = passwordEt.text.toString().trim()

            if (namausr.isEmpty() || namausr.length > 9){
                namaEt.error = "Nama terlalu panjang"
                namaEt.requestFocus()
                return@setOnClickListener
            }
            if (alamat.isEmpty()){
                alamatEt.error = "Alamat tidak boleh kosong!!!!"
                alamatEt.requestFocus()
                return@setOnClickListener
            }
            if (nope.isEmpty() || nope.length > 13){
                nopeEt.error = "Nomor Hp tidak boleh kosong!!!!"
                nopeEt.requestFocus()
                return@setOnClickListener
            }
            if (email.isEmpty()){
                emailEt.error = "Email tidak boleh kosong!!!!"
                emailEt.requestFocus()
                return@setOnClickListener
            }
//            validasi email
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                emailEt.error = "Email tidak tidak valid"
                emailEt.requestFocus()
                return@setOnClickListener
            }
            if (password.isEmpty() || password.length < 6){
                passwordEt.error = "Password harus lebih dari 6 huruf"
                passwordEt.requestFocus()
                return@setOnClickListener
            }

            registeruser(email, password, namausr, alamat, nope)
        }

        Login_lagibtn.setOnClickListener {
            Intent(this@RegistrasiActivity, LoginActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    private fun registeruser(email: String, password: String, namausr: String, alamat: String, nope: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this){
            if (it.isSuccessful()){
                Toast.makeText(this,"User" +
                        "Created.", Toast.LENGTH_SHORT).show()
                val userid:String= auth.currentUser!!.uid
                firebasefirestore.collection("user").document(userid).get()
                val db = Firebase.firestore
                val user = HashMap<String, String>()
                user.put("nama",namausr)
                user.put("alamat",alamat)
                user.put("nomor hp",nope)
                user.put("email",email)
                user.put("password",password)
                val userRef = db.collection("user")
                userRef.document(userid).set(user)
                    .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                    .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
                Intent(this@RegistrasiActivity, HomeActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(it)
                }
            }
        }
    }


}