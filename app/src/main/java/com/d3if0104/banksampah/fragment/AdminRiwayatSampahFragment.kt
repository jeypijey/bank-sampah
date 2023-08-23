package com.d3if0104.banksampah.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
//import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.d3if0104.banksampah.R
import com.d3if0104.banksampah.adapter.AdapterRiwayatSampah
import com.d3if0104.banksampah.databinding.FragmentAdminRiwayatSampahBinding
import com.d3if0104.banksampah.model.DataSampah
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class AdminRiwayatSampahFragment : Fragment() {
    private lateinit var binding: FragmentAdminRiwayatSampahBinding
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var ref: DatabaseReference
    private lateinit var ref2: Query
    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdapterRiwayatSampah
    private val riwayatArrayList = arrayListOf<DataSampah>()
//    private lateinit var spinner: Spinner
//    private val jenisArrayList = arrayListOf<String>()
//    private var selectedJenis: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminRiwayatSampahBinding.inflate(layoutInflater)

        auth = FirebaseAuth.getInstance()
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        ref = FirebaseDatabase.getInstance().getReference("DataSampah")

        recyclerView = binding.recycleViewAdmin
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

//        spinner = binding.spJenis
//        setupSpinner()

        adapter =
            AdapterRiwayatSampah(arrayListOf(), object : AdapterRiwayatSampah.OnAdapterListener {
                override fun deleteRiwayat(riwayat: DataSampah, v: View) {
                    AlertDialog.Builder(requireContext()).apply {
                        setMessage(R.string.pesan_hapus_riwayat)
                        setPositiveButton("HAPUS") { _, _ ->
                            val dataSampah = riwayat.id
                            ref2 = FirebaseDatabase.getInstance().reference.child("DataSampah")
                                .child(firebaseUser.uid).orderByChild("id").equalTo(dataSampah)
                            ref2.addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                        for (data in snapshot.children) {
                                            data.ref.removeValue()
                                            Toast.makeText(
                                                context,
                                                "Berhasil Menghapus",
                                                Toast.LENGTH_SHORT
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

        binding.buttonSearch.setOnClickListener {
            val keyword = binding.etSearch.text.toString()
            searchData(keyword)
        }

        binding.etJenis.setOnClickListener {
            findNavController().navigate(R.id.action_adminRiwayatSampahFragment_to_pilihJenisFragment)
        }
        recyclerView.adapter = adapter


        setJenis()
        showListRiwayat()

        binding.etJenis.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed in this case
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val jenis = s.toString()
                if (jenis.isEmpty()) {
                    showListRiwayat()
                } else {
                    showListRiwayatJenis(jenis)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Not needed in this case
            }
        })

        return binding.root
    }



    private fun showListRiwayat() {
        val currentUserId = auth.currentUser!!.uid
        ref = FirebaseDatabase.getInstance().reference.child("DataSampah").child(currentUserId)
        ref.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                riwayatArrayList.clear()

                if (snapshot.exists()) {
                    for (data in snapshot.children) {
                        val item = data.getValue(DataSampah::class.java)
                        item?.let {
                            riwayatArrayList.add(it)
                        }
                    }
                    // Sort riwayatArrayList by timestamp in descending order
                    riwayatArrayList.sortByDescending { it.timestamp }

                    binding.kosong.visibility = View.GONE
                    adapter.setData(riwayatArrayList)
                } else {
                    binding.kosong.visibility = View.VISIBLE
                    adapter.setData(riwayatArrayList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled if needed
            }
        })
    }


    private fun showListRiwayatJenis(jenis: String) {
        val currentUserId = auth.currentUser!!.uid
        ref = FirebaseDatabase.getInstance().reference.child("DataSampah").child(currentUserId)
        ref.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                riwayatArrayList.clear()

                if (snapshot.exists()) {
                    for (data in snapshot.children) {
                        val item = data.getValue(DataSampah::class.java)
                        val itemJenis = item?.namaSampah.toString()
                        item?.let {
                            if (itemJenis == jenis){
                                riwayatArrayList.add(it)
                            }
                        }
                    }
                    // Sort riwayatArrayList by timestamp in descending order
                    riwayatArrayList.sortByDescending { it.timestamp }

                    binding.kosong.visibility = View.GONE
                    adapter.setData(riwayatArrayList)
                } else {
                    binding.kosong.visibility = View.VISIBLE
                    adapter.setData(riwayatArrayList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled if needed
            }
        })
    }


    private fun searchData(keyword: String) {
        ref2 = FirebaseDatabase.getInstance().getReference("DataSampah").child(firebaseUser.uid)
        ref2.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val searchResults = mutableListOf<DataSampah>()

                for (data in snapshot.children) {
                    val sampah = data.getValue(DataSampah::class.java)
                    sampah?.let {
                        val nama = it.nama
                        if (nama.contains(keyword, ignoreCase = true)) {
                            searchResults.add(it)
                        }
                    }
                }

                adapter.setData(searchResults)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun setJenis() {
        setFragmentResultListener("namaSampah") { _, bundle ->
            val result = bundle.getString("namaSampah")
            binding.etJenis.setText(result)
        }

    }
}