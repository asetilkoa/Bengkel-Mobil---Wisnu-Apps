package com.example.bookingservice.View.LoginRegister

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.Toast
import com.example.bookingservice.R
import com.example.bookingservice.View.Dashboard.ProfileActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_update_pass.*

class UpdatePassActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firebasefirestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_pass)

        auth = FirebaseAuth.getInstance()
        firebasefirestore = FirebaseFirestore.getInstance()
        val user = auth.currentUser

        updatebtn.setOnClickListener {
            val passwordcr  = passwordcrEt.text.toString().trim()
            val passwordnew  = passwordnewEt.text.toString().trim()

            if (passwordcr.isEmpty() || passwordcr.length < 6){
                passwordcrEt.error = "Password harus lebih dari 6 huruf"
                passwordcrEt.requestFocus()
                return@setOnClickListener
            }
            if (passwordnew.isEmpty() || passwordnew.length < 6){
                passwordnewEt.error = "Password harus lebih dari 6 huruf"
                passwordnewEt.requestFocus()
                return@setOnClickListener
            }

            user?.let {
                val userCredential= EmailAuthProvider.getCredential(it.email!!, passwordcr)
                it.reauthenticate(userCredential).addOnCompleteListener {
                    if (it.isSuccessful){
                        passwordcrEt.text?.clear()
                        passwordnewEt.text?.clear()

                        user.updatePassword(passwordnew).addOnCompleteListener {
                            if (it.isSuccessful){
                                val userid:String= auth.currentUser!!.uid
                                firebasefirestore.collection("user").document(userid).update("password", passwordnew)
                                succesDialog()
                            }else {
                                Toast.makeText(this, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }else if (it.exception is FirebaseAuthInvalidCredentialsException){
                        passwordcrEt.error = "Password salah"
                        passwordcrEt.requestFocus()
                    }else{
                        Toast.makeText(this, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun succesDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.succesup_dialog)

        val btntutup = dialog.findViewById<Button>(R.id.btntutup)
        btntutup.setOnClickListener {
            dialog.dismiss()
            Intent(this, ProfileActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }
        }
        dialog.show()
    }
}