package com.d3if0104.banksampah.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.d3if0104.banksampah.LoginActivity
import com.d3if0104.banksampah.databinding.FragmentProfilBinding
import com.d3if0104.banksampah.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class ProfilFragment : Fragment() {
    private lateinit var binding: FragmentProfilBinding
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfilBinding.inflate(layoutInflater)

        (activity as AppCompatActivity).supportActionBar?.title = "Profil Anda"

        auth = FirebaseAuth.getInstance()
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        currentUser()

        binding.logoutButton.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        return binding.root
    }

    private fun currentUser() {
        val database =
            FirebaseDatabase.getInstance().reference.child("User").child(firebaseUser.uid)
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(User::class.java)
                    val nama = user!!.nama
                    val email = user.email
                    binding.nameText.text = nama
                    binding.emailText.text = email

                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Apakah kamu yakin ingin keluar?")
        builder.setPositiveButton("Keluar") { _, _ ->
            logoutUser()
        }
        builder.setNegativeButton("Batal") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun logoutUser() {
        Firebase.auth.signOut()
        clearUserRole()
        val i = Intent(activity, LoginActivity::class.java)
        activity?.startActivity(i)
        activity?.finish()
    }

    private fun clearUserRole() {
        val sharedPreferences =
            requireContext().getSharedPreferences("UserRolePrefs", AppCompatActivity.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}