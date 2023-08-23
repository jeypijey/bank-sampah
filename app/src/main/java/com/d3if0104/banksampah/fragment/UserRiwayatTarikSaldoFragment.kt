package com.d3if0104.banksampah.fragment

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.d3if0104.banksampah.adapter.AdapterRiwayatTarikUser
import com.d3if0104.banksampah.databinding.FragmentUserRiwayatTarikSaldoBinding
import com.d3if0104.banksampah.model.TarikSaldo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class UserRiwayatTarikSaldoFragment : Fragment(){
    private lateinit var binding: FragmentUserRiwayatTarikSaldoBinding
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var ref: DatabaseReference
    private lateinit var refUser: DatabaseReference
//    private lateinit var ref2: Query
    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdapterRiwayatTarikUser
    private val riwayatArrayList = arrayListOf<TarikSaldo>()

    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserRiwayatTarikSaldoBinding.inflate(layoutInflater)

        (activity as AppCompatActivity).supportActionBar?.title = "Riwayat Tarik Saldo"

        auth = FirebaseAuth.getInstance()
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        ref = FirebaseDatabase.getInstance().getReference("TarikSaldo")
        refUser = FirebaseDatabase.getInstance().getReference("User")

        progressDialog = ProgressDialog(requireContext())
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Loading ...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        recyclerView = binding.recycleViewRiwayatTarikSaldo
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        adapter = AdapterRiwayatTarikUser(arrayListOf())
        recyclerView.adapter = adapter

        showListRiwayatTarik()

        return binding.root
    }

    private fun showListRiwayatTarik() {
        val currentUserId = auth.currentUser!!.uid

        ref.child(currentUserId).addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                riwayatArrayList.clear()
                if (snapshot.exists()) {
                    progressDialog.hide()
                    for (data in snapshot.children) {
                        val item = data.getValue(TarikSaldo::class.java)
                        riwayatArrayList.add(item!!)
                    }
                    binding.kosong.visibility = View.GONE
                    adapter.setData(riwayatArrayList)
                } else {
                    progressDialog.hide()
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