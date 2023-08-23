package com.d3if0104.banksampah.fragment

import android.annotation.SuppressLint
import android.app.ProgressDialog
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
import com.d3if0104.banksampah.R
import com.d3if0104.banksampah.adapter.AdapterPelanggan
import com.d3if0104.banksampah.databinding.FragmentPelangganBinding
import com.d3if0104.banksampah.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener


class PelangganFragment : Fragment() {
    private lateinit var binding: FragmentPelangganBinding
    private lateinit var firebaseUser: FirebaseUser
    lateinit var ref: DatabaseReference
    private lateinit var ref2: Query
    lateinit var auth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdapterPelanggan
    val userArrayList = arrayListOf<User>()
    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPelangganBinding.inflate(layoutInflater)

        (activity as AppCompatActivity).supportActionBar?.title = "Daftar Pelanggan"

        progressDialog = ProgressDialog(requireContext())
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Loading ...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        auth = FirebaseAuth.getInstance()
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        ref2 = FirebaseDatabase.getInstance().getReference("User")

        recyclerView = binding.recycleViewPelanggan
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        adapter = AdapterPelanggan(arrayListOf(), object : AdapterPelanggan.OnItemClicklistener {
            override fun onItemClick(pelanggan: User, v: View) {
                val id = pelanggan.id
                setFragmentResult(
                    "id",
                    bundleOf("id" to id)
                )
                findNavController().navigate(R.id.action_pelangganFragment_to_detailPelangganFragment)
            }
        })
        recyclerView.adapter = adapter

        binding.buttonSearch.setOnClickListener {
            val keyword = binding.etSearch.text.toString()
            searchData(keyword)
        }

        showPelanggan()

        return binding.root
    }

    private fun showPelanggan() {
//        val currentUserId = auth.currentUser!!.uid
        ref2 = FirebaseDatabase.getInstance().reference.child("User").orderByChild("admin")
            .equalTo(false)
        ref2.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                userArrayList.clear()
                if (snapshot.exists()) {
                    progressDialog.hide()
                    for (data in snapshot.children) {
                        val item = data.getValue(User::class.java)
                        userArrayList.add(item!!)
                    }
                    userArrayList.sortBy { it.nama }

                    binding.kosong.visibility = View.GONE
                    adapter.setData(userArrayList)
                } else {
                    progressDialog.hide()
                    binding.kosong.visibility = View.VISIBLE
                    adapter.setData(userArrayList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun searchData(keyword: String) {
        ref2 = FirebaseDatabase.getInstance().getReference("User").orderByChild("admin")
            .equalTo(false)
        ref2.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                progressDialog.hide()
                val searchResults = mutableListOf<User>()

                for (data in snapshot.children) {
                    val user = data.getValue(User::class.java)
                    user?.let {
                        val nama = it.nama
                        if (nama != null) {
                            if (nama.contains(keyword, ignoreCase = true)) {
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