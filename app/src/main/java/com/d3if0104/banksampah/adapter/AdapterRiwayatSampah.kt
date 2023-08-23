package com.d3if0104.banksampah.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.d3if0104.banksampah.R
import com.d3if0104.banksampah.model.DataSampah
import java.text.NumberFormat
import java.util.Locale

class AdapterRiwayatSampah (private val riwayatList: ArrayList<DataSampah>, private val listener: AdapterRiwayatSampah.OnAdapterListener): RecyclerView.Adapter<AdapterRiwayatSampah.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_riwayat, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val sortedDataList = sortItemsByTimestampDescending(riwayatList)
        val riwayat : DataSampah = sortedDataList[position]
        holder.nama.text = riwayat.nama
        holder.namaSampah.text = riwayat.namaSampah
        holder.tanggal.text = riwayat.tanggal
        holder.harga.text = riwayat.harga
        val berat = riwayat.berat
        val sBerat = "$berat Kg"
        val total = riwayat.total
        val formatter = NumberFormat.getCurrencyInstance(Locale("in","ID"))
        val saldoformatte = formatter.format(total.toString().toDouble())
        holder.berat.text = sBerat
        holder.total.text = saldoformatte.toString()


//        holder.delete.setOnClickListener{
//            listener.deleteRiwayat(riwayat,it)
//        }

    }

    fun setData(data : List<DataSampah>){
        riwayatList.clear()
        riwayatList.addAll(data)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return riwayatList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nama : TextView = itemView.findViewById(R.id.namaUser)
        val tanggal  : TextView = itemView.findViewById(R.id.tanggal)
        val namaSampah : TextView = itemView.findViewById(R.id.namaSampah)
        val harga : TextView = itemView.findViewById(R.id.harga)
        val berat : TextView = itemView.findViewById(R.id.berat)
        val total : TextView = itemView.findViewById(R.id.totalHarga)

//        val delete : ImageButton = itemView.findViewById(R.id.delete)
    }

    fun sortItemsByTimestampDescending(items: List<DataSampah>): List<DataSampah> {
        return items.sortedByDescending { it.timestamp }
    }

    interface OnAdapterListener {
        fun deleteRiwayat(riwayat : DataSampah, v: View)
    }
}