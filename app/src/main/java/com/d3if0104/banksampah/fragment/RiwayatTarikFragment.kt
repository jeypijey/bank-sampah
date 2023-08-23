package com.d3if0104.banksampah.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
//import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.d3if0104.banksampah.R
import com.d3if0104.banksampah.adapter.AdapterRiwayatTarikSaldo
import com.d3if0104.banksampah.databinding.FragmentRiwayatTarikBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.d3if0104.banksampah.model.TarikSaldo
//import java.util.ArrayList

class RiwayatTarikFragment : Fragment() {
    private lateinit var binding: FragmentRiwayatTarikBinding
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var ref: DatabaseReference
    private lateinit var refUser: DatabaseReference
    private lateinit var refSaldo: DatabaseReference
//    private lateinit var listDatabase: DatabaseReference
    private lateinit var ref2: Query
    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdapterRiwayatTarikSaldo
    private val riwayatArrayList = arrayListOf<TarikSaldo>()
    private lateinit var idRiwayatPelanggan: String
    private lateinit var userId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRiwayatTarikBinding.inflate(layoutInflater)

        (activity as AppCompatActivity).supportActionBar?.title = "Riwayat Tarik Saldo"

        auth = FirebaseAuth.getInstance()
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        refUser = FirebaseDatabase.getInstance().getReference("User")
        refSaldo = FirebaseDatabase.getInstance().getReference("TarikSaldo")

        recyclerView = binding.recycleViewRiwayatTarikSaldo
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        adapter = AdapterRiwayatTarikSaldo(
            arrayListOf(),
            object : AdapterRiwayatTarikSaldo.OnAdapterListener {
                override fun deleteRiwayat(riwayat: TarikSaldo, v: View) {
                    AlertDialog.Builder(requireContext()).apply {
                        setMessage(R.string.pesan_hapus_riwayat)
                        setPositiveButton("HAPUS") { _, _ ->
                            val idRiwayatTarik = riwayat.idTarik
                            idRiwayatPelanggan = riwayat.idUser
                            val saldoRefund = riwayat.saldo
                            val numeriSaldo = saldoRefund.replace(Regex("[^0-9]"), "")
                            ref2 = FirebaseDatabase.getInstance().reference.child("TarikSaldo")
                                .child(idRiwayatPelanggan).orderByChild("idTarik")
                                .equalTo(idRiwayatTarik)
                            ref2.addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                        for (data in snapshot.children) {
                                            updateTotalSaldo(numeriSaldo.toInt())
                                            data.ref.removeValue()

                                            Toast.makeText(
                                                context,
                                                "Berhasil Refund Saldo",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            showListRiwayatTarik()
                                        }
                                    } else {
                                        showListRiwayatTarik()
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    // Handle onCancelled
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

        binding.buttonSearch.setOnClickListener {
            val keyword = binding.etSearch.text.toString()
            searchData(keyword)
        }

        recyclerView.adapter = adapter

        showListRiwayatTarik()

        return binding.root
    }

    private fun updateTotalSaldo(amount: Int) {
        refUser.child(idRiwayatPelanggan)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val currentTotalSaldo = snapshot.child("saldo").getValue(Int::class.java) ?: 0
                    val newTotalSaldo = currentTotalSaldo + amount

                    val saldoUpdate = HashMap<String, Any>()
                    saldoUpdate["saldo"] = newTotalSaldo
                    refUser.child(idRiwayatPelanggan).updateChildren(saldoUpdate)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle onCancelled if needed
                }
            })
    }

    private fun showListRiwayatTarik() {
        ref = FirebaseDatabase.getInstance().reference.child("TarikSaldo")
        ref.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                riwayatArrayList.clear()
                if (snapshot.exists()) {
                    for (userData in snapshot.children) {
                        userId = userData.key.toString()
                        if (userId != null) {
                            for (tarikSaldoData in userData.children) {
                                val item = tarikSaldoData.getValue(TarikSaldo::class.java)
                                if (item != null) {
                                    riwayatArrayList.add(item)
                                }
                            }
                        }
                    }
                    binding.kosong.visibility = View.GONE
                    adapter.setData(riwayatArrayList)

                    riwayatArrayList.sortByDescending { it.timestamp }

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

    private fun searchData(keyword: String) {
        val currentUserId = firebaseUser.uid
        ref = FirebaseDatabase.getInstance().reference.child("TarikSaldo")
        ref.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                val searchResults = mutableListOf<TarikSaldo>()
                for (userData in snapshot.children) {
                    userId = userData.key.toString()
                    if (userId != null) {
                        for (tarikSaldoData in userData.children) {
                            val item = tarikSaldoData.getValue(TarikSaldo::class.java)
                            if (item != null && item.nama.contains(keyword, true)) {
                                searchResults.add(item)
                            }
                        }
                    }
                }

                // Sort the search results by timestamp
                searchResults.sortByDescending { it.timestamp }

                if (searchResults.isNotEmpty()) {
                    binding.kosong.visibility = View.GONE
                    adapter.setData(searchResults)
                } else {
                    binding.kosong.visibility = View.VISIBLE
                    adapter.setData(searchResults)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled if needed
            }
        })
    }
}