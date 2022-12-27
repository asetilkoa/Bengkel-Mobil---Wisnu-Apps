package com.example.bookingservice.View.Dashboard

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.bookingservice.R
import com.example.bookingservice.View.LoginRegister.UpdatePassActivity
import com.example.bookingservice.View.Main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.ByteArrayOutputStream

class ProfileActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firebasefirestore: FirebaseFirestore
    private var pickedPhoto: Uri? = null
    private var pickedBitmap: Bitmap? = null
    private lateinit var showname: TextView
    private lateinit var showemail: TextView
    private lateinit var showalamat: TextView
    private lateinit var shownope: TextView
    private lateinit var imageuri : Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        firebasefirestore = FirebaseFirestore.getInstance()
        showname = findViewById(R.id.tvusername)
        showemail = findViewById(R.id.tvemail)
        showalamat = findViewById(R.id.txtalamat)
        shownope = findViewById(R.id.txtnope)

//      lihat data email dan nama
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("user").document(auth.getCurrentUser()!!.getUid())
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(ContentValues.TAG, "DocumentSnapshot data: ${document.data}")
                    showname.text = document.getString("nama")
                    showemail.text = document.getString("email")
                    showalamat.text = document.getString("alamat")
                    shownope.text = document.getString("nomor hp")
                } else {
                    Log.d(ContentValues.TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "get failed with ", exception)
            }

        val user = auth.currentUser
        if (user != null){
            if (user.photoUrl != null){
                Picasso.get().load(user.photoUrl).into(imageprofile)
            }

            editbtn.setOnClickListener {
                val image = when{
                    ::imageuri.isInitialized -> imageuri
                    user?.photoUrl == null -> Uri.parse("https://picsum.photos/id/870/200/300?grayscale&blur=2")
                    else -> user.photoUrl
                }

                UserProfileChangeRequest.Builder()
                    .setPhotoUri(image)
                    .build().also {
                        user?.updateProfile(it)?.addOnCompleteListener {
                            if (it.isSuccessful){
                                Toast.makeText(this, "Update Sukses", Toast.LENGTH_SHORT).show()
                            }else{
                                Toast.makeText(this, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                Intent(this@ProfileActivity, HomeActivity::class.java).also {
                    startActivity(it)
                    this.finish()
                }
            }
        }

        btnpassword.setOnClickListener {
            Intent(this@ProfileActivity, UpdatePassActivity::class.java).also {
                startActivity(it)
            }
        }

//      logout
        logoutbtn.setOnClickListener {
            auth.signOut()
            Intent(this@ProfileActivity, MainActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }
        }
    }

    fun pickedfoto(view: android.view.View) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        }else{
            val galeriIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galeriIntent,2)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1){
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                val galeriIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntent,2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null){
            pickedPhoto = data.data
            if (pickedPhoto != null){
                if (Build.VERSION.SDK_INT >= 28){
                    val source = ImageDecoder.createSource(this.contentResolver,pickedPhoto!!)
                    pickedBitmap = ImageDecoder.decodeBitmap(source)
                    uploadimage(source)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun uploadimage(source: ImageDecoder.Source) {
        val baos = ByteArrayOutputStream()
        val ref = FirebaseStorage.getInstance().reference.child("img/${FirebaseAuth.getInstance().currentUser?.uid}")
        pickedBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val image = baos.toByteArray()

        ref.putBytes(image)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    ref.downloadUrl.addOnCompleteListener {
                        it.result.let {
                            imageuri = it
                            imageprofile.setImageBitmap(pickedBitmap)
                        }
                    }
                }
            }
    }
}