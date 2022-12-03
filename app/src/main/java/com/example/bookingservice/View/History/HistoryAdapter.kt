package com.example.bookingservice.View.History

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bookingservice.Model.ModelDatabase
import com.example.bookingservice.R

class HistoryAdapter(private val modelDatabase: ArrayList<ModelDatabase>) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.history_item,
            parent,false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = modelDatabase[position]

        holder.platnomor.text = data.platnomor
        holder.nama.text = data.nama
        holder.nomorhp.text = data.nomorhape
        holder.tanggalservice.text = data.tanggalservices
    }

    override fun getItemCount(): Int {
        return modelDatabase.size
    }

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        val nomorhp : TextView = itemView.findViewById(R.id.tvnope)
        val platnomor : TextView = itemView.findViewById(R.id.tvPlatnmr)
        val nama : TextView = itemView.findViewById(R.id.tvNama)
        val tanggalservice : TextView = itemView.findViewById(R.id.tvTglservice)
    }
}