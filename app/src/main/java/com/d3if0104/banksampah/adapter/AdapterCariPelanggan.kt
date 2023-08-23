package com.d3if0104.banksampah.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.d3if0104.banksampah.R
import com.d3if0104.banksampah.model.User


class AdapterCariPelanggan (private val dataList: ArrayList<User>) : RecyclerView.Adapter<AdapterCariPelanggan.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_pelanggan_dialog, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data : User = dataList[position]
        holder.nama.text = data.nama
    }

    fun setData(data : List<User>){
        dataList.clear()
        dataList.addAll(data)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nama : TextView = itemView.findViewById(R.id.namaPelanggan)
    }
}