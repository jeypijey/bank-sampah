package com.d3if0104.banksampah.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.d3if0104.banksampah.R
import com.d3if0104.banksampah.model.User

class AdapterPilihPelanggan (

    private val pelangganList: ArrayList<User>,
    private val listener: AdapterPilihPelanggan.OnItemClicklistener
) : RecyclerView.Adapter<AdapterPilihPelanggan.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_cari_pelanggan, parent, false)
        return MyViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val pelanggan: User = pelangganList[position]
        holder.nama.text = pelanggan.nama
        holder.bindData(pelanggan)

        holder.itemView.setOnClickListener { listener.onItemClick(pelanggan, it) }

    }

    fun setData(data: List<User>) {
        pelangganList.clear()
        pelangganList.addAll(data)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return pelangganList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nama: TextView = itemView.findViewById(R.id.namaPelanggan)

        fun bindData(pelanggan: User) {
            nama.text = pelanggan.nama
        }
    }

    interface OnItemClicklistener {
        fun onItemClick(pelanggan: User, v: View)
    }

}