package com.d3if0104.banksampah.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.d3if0104.banksampah.R
import com.d3if0104.banksampah.databinding.FragmentHomeBinding
import com.d3if0104.banksampah.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)

        (activity as? AppCompatActivity)?.supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(false)
            hide()
        }

        auth = FirebaseAuth.getInstance()
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        currentUser(firebaseUser.uid)

        binding.imageButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_profilFragment)
        }

        binding.pelanggan.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_pelangganFragment)
        }

        binding.inputSampahCard.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_inputFragment)
        }

        binding.jenisSampahCard.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_jenisFragment)
        }

        binding.riwayatCard.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_adminRiwayatSampahFragment)
        }
        binding.riwayatTarikCard.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_riwayatTarikFragment)
        }

        return binding.root
    }

    private fun currentUser(uid: String) {
        val dbAdmin =
            FirebaseDatabase.getInstance().reference.child("User").child(uid)
        dbAdmin.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(User::class.java)
                    val nama = user!!.nama
                    binding.name.text = nama
                    println("contoh")
                    println(user)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? AppCompatActivity)?.supportActionBar?.apply {
            setDisplayShowTitleEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            show()
        }
    }
}