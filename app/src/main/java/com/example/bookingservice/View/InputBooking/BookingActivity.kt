package com.example.bookingservice.View.InputBooking

import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.bookingservice.Model.ModelDatabase
import com.example.bookingservice.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_booking.*
import java.text.SimpleDateFormat
import java.util.*

class BookingActivity : AppCompatActivity() {

    private lateinit var platnomor: EditText
    private lateinit var tahunkendaraan: EditText
    private lateinit var serieskendaraan: EditText
    private lateinit var namapemilik: EditText
    private lateinit var alamat: EditText
    private lateinit var nomorhp: EditText
    private lateinit var tanggalbook: EditText
    private lateinit var keluhane: EditText
    private lateinit var submit: Button
    private lateinit var database: DocumentReference
    private lateinit var auth: FirebaseAuth

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            setInitView()
        }
        platnomor = findViewById(R.id.pltnomor)
        tahunkendaraan = findViewById(R.id.thnkendaraan)
        serieskendaraan = findViewById(R.id.srskendaraan)
        namapemilik = findViewById(R.id.namapmlk)
        alamat = findViewById(R.id.alamatpmlk)
        nomorhp = findViewById(R.id.nohpe)
        tanggalbook = findViewById(R.id.tglbooking)
        keluhane = findViewById(R.id.keluhankerusakan)
        submit = findViewById(R.id.submitbtn)

        auth = FirebaseAuth.getInstance()
//        sebagai sub collection firestore
        database = FirebaseFirestore.getInstance().collection("user").document(
            auth.getCurrentUser()!!.getUid())

        submitbtn.setOnClickListener {
            val platnomor = pltnomor.text.toString()
            val tahunkendaraan = thnkendaraan.text.toString()
            val serieskendaraan = srskendaraan.text.toString()
            val nama = namapmlk.text.toString()
            val alamat = alamatpmlk.text.toString()
            val nomorhp = nohpe.text.toString()
            val tanggalservice = tglbooking.text.toString()
            val keluhane = keluhankerusakan.text.toString()

//        jika tidak mengisi formulir
            if (platnomor.isEmpty()){
                pltnomor.error = "Mohon Masukan Plat Nomor"
                pltnomor.requestFocus()
                return@setOnClickListener
            }
            if (tahunkendaraan.isEmpty()){
                thnkendaraan.error = "Mohon Masukan Tahun Kendaraan"
                thnkendaraan.requestFocus()
                return@setOnClickListener
            }
            if (serieskendaraan.isEmpty()){
                srskendaraan.error = "Mohon Masukan Series Kendaraan"
                srskendaraan.requestFocus()
                return@setOnClickListener
            }
            if (nama.isEmpty()){
                namapmlk.error = "Mohon Masukan Nama"
                namapmlk.requestFocus()
                return@setOnClickListener
            }
            if (alamat.isEmpty()){
                alamatpmlk.error = "Mohon Masukan Alamat"
                alamatpmlk.requestFocus()
                return@setOnClickListener
            }
            if (tanggalservice.isNullOrEmpty()){
                tglbooking.error = "Mohon Masukan Tanggal"
                tglbooking.requestFocus()
                return@setOnClickListener
            }
            if (nomorhp.isEmpty() || nomorhp.length > 13){
                nohpe.error = "Nomor HP terlalu panjang"
                nohpe.requestFocus()
                return@setOnClickListener
            }
            if (keluhane.isEmpty()){
                keluhankerusakan.error = "Mohon Masukan Keluhan anda"
                keluhankerusakan.requestFocus()
                return@setOnClickListener
            }

//            upload data ke firebase
            val Modeldatabase = ModelDatabase(platnomor, tahunkendaraan, serieskendaraan, nama, alamat, nomorhp, tanggalservice, keluhane)
            database.collection("Data Booking").document(nama).set(Modeldatabase)
                .addOnSuccessListener {
                    pltnomor.text?.clear()
                    thnkendaraan.text?.clear()
                    srskendaraan.text?.clear()
                    namapmlk.text?.clear()
                    alamatpmlk.text?.clear()
                    nohpe.text?.clear()
                    tglbooking.text?.clear()
                    keluhankerusakan.text?.clear()
//                    Toast.makeText(this, "Booking Sukses!", Toast.LENGTH_SHORT).show()
                    succesdialog()
                }.addOnFailureListener{
//                    Toast.makeText(this, "Booking Gagal!", Toast.LENGTH_SHORT).show()
                    failedDialog()
                }
        }
    }

    private fun failedDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.failed_dialog)

        val btntutup = dialog.findViewById<Button>(R.id.btntutup)
        btntutup.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun succesdialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.succes_diaglog)

        val btntutup = dialog.findViewById<Button>(R.id.btntutup)
        btntutup.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    //    untuk input tanggal
    @RequiresApi(Build.VERSION_CODES.N)
    private fun setInitView() {
        tglbooking.setOnClickListener {
            val tanggalbok = Calendar.getInstance()
            val date =
                DatePickerDialog.OnDateSetListener { view, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                    tanggalbok[Calendar.YEAR] = year
                    tanggalbok[Calendar.MONTH] = monthOfYear
                    tanggalbok[Calendar.DAY_OF_MONTH] = dayOfMonth
                    val strFormatDefault = "d MMMM yyyy"
                    val simpleDateFormat = SimpleDateFormat(strFormatDefault, Locale.getDefault())
                    tglbooking.setText(simpleDateFormat.format(tanggalbok.time))
                }

            val dateP = DatePickerDialog(
                this@BookingActivity, R.style.Datetheme, date,
                tanggalbok[Calendar.YEAR],
                tanggalbok[Calendar.MONTH],
                tanggalbok[Calendar.DAY_OF_MONTH]
            )
//            disable past date untuk booking yang memungkinkan booking sehari kedepan
                tanggalbok.add(Calendar.DAY_OF_MONTH,1)
                dateP.datePicker.minDate = tanggalbok.timeInMillis
                dateP.show()
        }
    }
}