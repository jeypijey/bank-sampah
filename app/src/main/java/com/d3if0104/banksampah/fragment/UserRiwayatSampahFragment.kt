package com.d3if0104.banksampah.fragment

import android.app.ProgressDialog
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.d3if0104.banksampah.R
import com.d3if0104.banksampah.adapter.AdapterRiwayatSampah
import com.d3if0104.banksampah.databinding.FragmentUserRiwayatSampahBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.d3if0104.banksampah.model.DataSampah

class UserRiwayatSampahFragment : Fragment(), AdapterView.OnItemSelectedListener {
    private lateinit var binding: FragmentUserRiwayatSampahBinding
    private lateinit var firebaseUser: com.google.firebase.auth.FirebaseUser
    private lateinit var ref: com.google.firebase.database.DatabaseReference
    private lateinit var listDatabase: com.google.firebase.database.DatabaseReference
    private lateinit var ref2: com.google.firebase.database.Query
    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var adapter: AdapterRiwayatSampah
    private val riwayatArrayList = arrayListOf<DataSampah>()

    private lateinit var progressDialog: android.app.ProgressDialog

    override fun onCreateView(
        inflater: android.view.LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserRiwayatSampahBinding.inflate(layoutInflater)

        (activity as AppCompatActivity).supportActionBar?.title = "Riwayat Sampah"

        auth = FirebaseAuth.getInstance()
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        ref = FirebaseDatabase.getInstance().getReference("DataSampah")
        listDatabase =
            FirebaseDatabase.getInstance().getReference("JenisSampah").child(firebaseUser.uid)

        recyclerView = binding.recycleViewRiwayat
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        progressDialog = ProgressDialog(requireContext())
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Loading ...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        adapter = AdapterRiwayatSampah(
            arrayListOf(),
            object : AdapterRiwayatSampah.OnAdapterListener {
                override fun deleteRiwayat(riwayat: DataSampah, v: View) {
                    AlertDialog.Builder(requireContext()).apply {
                        setMessage(R.string.pesan_hapus_riwayat)
                        setPositiveButton("HAPUS") { _, _ ->
                            val dataSampah = riwayat.id
                            ref2 =
                                FirebaseDatabase.getInstance().reference.child(
                                    "DataSampah"
                                ).child(firebaseUser.uid).orderByChild("id").equalTo(dataSampah)
                            ref2.addValueEventListener(object :
                                ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                        for (data in snapshot.children) {
                                            data.ref.removeValue()
                                            android.widget.Toast.makeText(
                                                context,
                                                "Berhasil Menghapus",
                                                android.widget.Toast.LENGTH_SHORT
                                            ).show()
                                            showListRiwayat()
                                        }
                                    } else {
                                        showListRiwayat()
                                    }

                                }

                                override fun onCancelled(error: DatabaseError) {

                                }

                            })

                        }
                        setNegativeButton("Batal") { dialog, _ ->
                            dialog.cancel()
                        }
                        show()
                    }
                }


            })
        recyclerView.adapter = adapter

        showListRiwayat()

        return binding.root
    }

    private fun showListRiwayat() {
        val currentUserId = auth.currentUser!!.uid
        val idAdmin = "6I9rENqt63hm22uIyzrNiVOmqVt2"
        ref.child(idAdmin)
            .orderByChild("id")
            .equalTo(currentUserId)
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    riwayatArrayList.clear()
                    if (snapshot.exists()) {
                        progressDialog.hide()
                        for (data in snapshot.children) {
                            val item = data.getValue(DataSampah::class.java)
                            riwayatArrayList.add(item!!)
                        }
                        binding.kosong.visibility = View.GONE
                        adapter.setData(riwayatArrayList)

                        riwayatArrayList.sortByDescending { it.timestamp }
                    } else {
                        progressDialog.hide()
                        binding.kosong.visibility = View.VISIBLE
                        adapter.setData(riwayatArrayList)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    progressDialog.hide()
                    // Handle the error here
                }
            })
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        val jenis = p0?.getItemAtPosition(p2).toString()

        if (jenis == "Semua") {
            showListRiwayat()
        } else {
            val currentUserId = auth.currentUser!!.uid
            ref2 = FirebaseDatabase.getInstance().reference.child("DataSampah").child(currentUserId)
                .orderByChild("namaSampah").equalTo(jenis)
            ref2.addValueEventListener(object : ValueEventListener {
                @android.annotation.SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    riwayatArrayList.clear()
                    if (snapshot.exists()) {
                        for (data in snapshot.children) {
                            val item = data.getValue(DataSampah::class.java)
                            riwayatArrayList.add(item!!)
                        }
                        binding.kosong.visibility = View.GONE
                        adapter.setData(riwayatArrayList)
                    } else {
                        binding.kosong.visibility = View.VISIBLE
                        adapter.setData(riwayatArrayList)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}