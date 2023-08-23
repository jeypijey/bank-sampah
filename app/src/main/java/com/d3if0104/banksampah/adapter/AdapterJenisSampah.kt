package com.d3if0104.banksampah.adapter

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.d3if0104.banksampah.R
import com.google.firebase.database.DatabaseReference
import com.d3if0104.banksampah.model.JenisSampah
import com.google.firebase.database.FirebaseDatabase
import java.text.NumberFormat

class AdapterJenisSampah(
    private val sampahList: ArrayList<JenisSampah>,
    private val listener: OnAdapterListener,
    private var ref: DatabaseReference
): RecyclerView.Adapter<AdapterJenisSampah.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_jenis, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val jenis : JenisSampah = sampahList[position]
        holder.jenis.text = jenis.jenisSampah
        val bHarga = jenis.harga
        val iHarga = bHarga.toInt()
        val formater = NumberFormat.getInstance().format(iHarga)
        val sFormater = formater.toString()
        val harga = "Rp $sFormater"
        holder.satuan.text = jenis.satuan
        holder.harga.text = harga

        holder.delete.setOnClickListener{
            listener.deleteSampah(jenis,it)
        }

        holder.itemView.setOnClickListener(){
            showEditDialog(holder.itemView.context, jenis)
        }

    }

    fun setData(data: List<JenisSampah>){
        sampahList.clear()
        sampahList.addAll(data)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return sampahList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val jenis : TextView = itemView.findViewById(R.id.jenis)
        val harga  : TextView = itemView.findViewById(R.id.harga)
        val delete : ImageButton = itemView.findViewById(R.id.delete)
        val satuan  : TextView = itemView.findViewById(R.id.satuan)
    }

    private fun showEditDialog(context: Context, data: JenisSampah) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog, null)

        val jenis : EditText = dialogView.findViewById(R.id.jenisSampah)
        jenis.setText(data.jenisSampah)
        val spinner : Spinner = dialogView.findViewById(R.id.spSatuan)
        val satuanData = data.satuan
        val arraySatuan = context.resources.getStringArray(R.array.satuan)

        val position = arraySatuan.indexOf(satuanData)
        if (position != -1) {
            spinner.setSelection(position)
        }

        val harga : EditText = dialogView.findViewById(R.id.harga)
        harga.setText(data.harga.toString())

        val alertDialogBuilder = AlertDialog.Builder(context)
            .setView(dialogView)
            .setPositiveButton("Simpan"){ dialog, _ ->
                val id = data.id
                val newJenis = jenis.text.toString()
                val newSatuan = spinner.selectedItem.toString()
                val newharga = harga.text.toString()
                editjenis(id, newJenis, newharga, newSatuan)
                Toast.makeText(context,"Data berhasil diedit", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun editjenis(id: String, newJenis: String, newHarga: String, newSatuan: String) {
        val edit = JenisSampah(id, newJenis,newHarga,newSatuan)

        ref = FirebaseDatabase.getInstance().getReference("JenisSampah").child(id)
        ref.setValue(edit).addOnCompleteListener {
            if (it.isSuccessful){
                Log.d("berhasil","Data berhasil diedit")
            }else{
                Log.d("gagal","Data gagal diedit")
            }
        }
    }

    interface OnAdapterListener {
        fun deleteSampah(jenis: JenisSampah, v : View)
    }

}