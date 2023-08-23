package com.d3if0104.banksampah.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.d3if0104.banksampah.R
import com.d3if0104.banksampah.model.TarikSaldo


class AdapterRiwayatTarikSaldo (private val riwayatTarikList: ArrayList<TarikSaldo>, private val listener: AdapterRiwayatTarikSaldo.OnAdapterListener): RecyclerView.Adapter<AdapterRiwayatTarikSaldo.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_riwayat_tarik, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val sortedDataList = sortItemsByTimestampDescending(riwayatTarikList)
        val riwayat : TarikSaldo = sortedDataList[position]
        holder.nama.text = riwayat.nama
        holder.tanggal.text = riwayat.tanggal
        holder.saldo.text = riwayat.saldo

        holder.delete.setOnClickListener{
            listener.deleteRiwayat(riwayat,it)
        }
    }

    fun setData(data : List<TarikSaldo>){
        riwayatTarikList.clear()
        riwayatTarikList.addAll(data)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return riwayatTarikList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nama  : TextView = itemView.findViewById(R.id.namaPelanggan)
        val tanggal  : TextView = itemView.findViewById(R.id.tanggal)
        val saldo : TextView = itemView.findViewById(R.id.saldo)

        val delete : ImageButton = itemView.findViewById(R.id.delete)
    }

    private fun sortItemsByTimestampDescending(items: List<TarikSaldo>): List<TarikSaldo> {
        return items.sortedByDescending { it.timestamp }
    }

    interface OnAdapterListener {
        fun deleteRiwayat(riwayat: TarikSaldo, v: View)
    }
}