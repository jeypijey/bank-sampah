package com.d3if0104.banksampah.fragment

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.d3if0104.banksampah.LoginActivity
import com.d3if0104.banksampah.R
import com.d3if0104.banksampah.databinding.FragmentUserProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.d3if0104.banksampah.model.User
import java.text.NumberFormat
import java.util.Locale

class UserProfileFragment : Fragment() {
    private lateinit var binding: FragmentUserProfileBinding
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var auth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserProfileBinding.inflate(layoutInflater)

        (activity as AppCompatActivity).supportActionBar?.title = "Profil Anda"

        auth = FirebaseAuth.getInstance()
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        currentUser()

        binding.editProfileBtn.setOnClickListener {
            findNavController().navigate(R.id.action_userProfileFragment_to_editProfileFragment)
        }

        binding.logoutButton.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        return binding.root
    }

    private fun currentUser() {
        progressDialog = ProgressDialog(requireContext())
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Loading ...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val database =
            FirebaseDatabase.getInstance().reference.child("User").child(firebaseUser.uid)
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(User::class.java)
                    val nama = user!!.nama
                    val email = user.email
                    val phone = user.phone
                    val alamat = user.alamat
                    val saldo = user.saldo ?: 0

//                    Glide.with(requireContext())
//                        .load(avatar)
//                        .placeholder(R.mipmap.avatar_image_round)
//                        .into(binding.avatarImageView)

                    binding.nameText.text = nama
                    val formattedSaldo = formatCurrency(saldo)
                    binding.saldoText.text = formattedSaldo
                    binding.emailText.text = email
                    if (phone == null || phone == "") {
                        binding.phoneText.text = "-"
                    } else {
                        binding.phoneText.text = phone
                    }
                    if (alamat == null || alamat == "") {
                        binding.alamatText.text = "-"
                    } else {
                        binding.alamatText.text = alamat
                    }

                    getTransaksi()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun getTransaksi() {
        val database =
            FirebaseDatabase.getInstance().reference.child("DataSampah").child("6I9rENqt63hm22uIyzrNiVOmqVt2").orderByChild("id").equalTo(firebaseUser.uid)
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val countData = snapshot.childrenCount
                    binding.transaksiText.text = countData.toString()

                    progressDialog.hide()
                } else {
                    binding.transaksiText.text = "0"
                    progressDialog.hide()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                progressDialog.hide()
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
        val i = Intent(activity, LoginActivity::class.java)
        activity?.startActivity(i)
        activity?.finish()
    }

    private fun formatCurrency(value: Int): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        return formatter.format(value).replace(",00", "")
    }
}