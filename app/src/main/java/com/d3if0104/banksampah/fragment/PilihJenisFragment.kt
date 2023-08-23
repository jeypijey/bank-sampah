package com.d3if0104.banksampah.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.d3if0104.banksampah.adapter.AdapterPilihJenis
import com.d3if0104.banksampah.databinding.FragmentPilihJenisBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.d3if0104.banksampah.model.JenisSampah


class PilihJenisFragment : Fragment() {
    private lateinit var binding: FragmentPilihJenisBinding
    private lateinit var firebaseUser: FirebaseUser
    lateinit var ref: DatabaseReference
    private lateinit var ref2: Query
    lateinit var auth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdapterPilihJenis
    val jenisArrayList = arrayListOf<JenisSampah>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPilihJenisBinding.inflate(layoutInflater)

        (activity as AppCompatActivity).supportActionBar?.title = "Daftar Jenis Sampah "

        auth = FirebaseAuth.getInstance()
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        ref2 = FirebaseDatabase.getInstance().getReference("JenisSampah")

        recyclerView = binding.rvJenis
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        adapter = AdapterPilihJenis(arrayListOf(), object : AdapterPilihJenis.OnItemClicklistener {
            override fun onItemClick(jenis: JenisSampah, v: View) {
                val nama = jenis.jenisSampah
                setFragmentResult(
                    "namaSampah",
                    bundleOf("namaSampah" to nama)
                )
                val harga = jenis.harga
                setFragmentResult(
                    "harga",
                    bundleOf("harga" to harga)
                )

                findNavController().popBackStack()
            }
        })
        recyclerView.adapter = adapter

        binding.buttonSearch.setOnClickListener {
            val keyword = binding.etSearch.text.toString()
            searchData(keyword)
        }

        showJenisSampah()

        return binding.root
    }

    private fun showJenisSampah() {
//        val currentUserId = auth.currentUser!!.uid
        ref = FirebaseDatabase.getInstance().reference.child("JenisSampah")
        ref.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                jenisArrayList.clear()
                if (snapshot.exists()) {
                    for (data in snapshot.children) {
                        val item = data.getValue(JenisSampah::class.java)
                        jenisArrayList.add(item!!)
                    }
                    jenisArrayList.sortBy { it.jenisSampah }

                    binding.kosong.visibility = View.GONE
                    adapter.setData(jenisArrayList)
                } else {
                    binding.kosong.visibility = View.VISIBLE
                    adapter.setData(jenisArrayList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun searchData(keyword: String) {
        ref2 = FirebaseDatabase.getInstance().getReference("JenisSampah")
        ref2.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val searchResults = mutableListOf<JenisSampah>()

                for (data in snapshot.children) {
                    val jenis = data.getValue(JenisSampah::class.java)
                    jenis?.let {
                        val jenisSampah = it.jenisSampah
                        if (jenisSampah != null) {
                            if (jenisSampah.contains(keyword, ignoreCase = true)) {
                                searchResults.add(it)
                            }
                        }
                    }
                }

                adapter.setData(searchResults)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}