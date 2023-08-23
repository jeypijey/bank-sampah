package com.d3if0104.banksampah.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.d3if0104.banksampah.R
import com.d3if0104.banksampah.model.JenisSampah

class AdapterPilihJenis (

    private val jenisList: ArrayList<JenisSampah>,
    private val listener: AdapterPilihJenis.OnItemClicklistener
) : RecyclerView.Adapter<AdapterPilihJenis.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_cari_sampah, parent, false)
        return MyViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data: JenisSampah = jenisList[position]
        holder.jenis.text = data.jenisSampah

        holder.itemView.setOnClickListener { listener.onItemClick(data, it) }

    }

    fun setData(data:List<JenisSampah>) {
        jenisList.clear()
        jenisList.addAll(data)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return jenisList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val jenis: TextView = itemView.findViewById(R.id.jenis)


    }

    interface OnItemClicklistener {
        fun onItemClick(jenis: JenisSampah, v: View)
    }

}