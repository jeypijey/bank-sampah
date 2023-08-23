package com.d3if0104.banksampah.fragment

import android.app.ProgressDialog
//import android.graphics.Bitmap
//import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.d3if0104.banksampah.databinding.FragmentEditProfileBinding
import com.d3if0104.banksampah.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EditProfileFragment : Fragment() {
    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var auth: FirebaseAuth
    private lateinit var ref: DatabaseReference
    private lateinit var progressDialog: ProgressDialog
//    private val REQUEST_IMAGE_CAPTURE = 1
//    private val REQUEST_IMAGE_PICK = 2
//
//    private var selectedImageUri: Uri? = null
//    private var capturedImageBitmap: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditProfileBinding.inflate(layoutInflater)

        (activity as AppCompatActivity).supportActionBar?.title = "Edit Profil"

        auth = FirebaseAuth.getInstance()
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        currentUser()

//        binding.profileImageView.setOnClickListener {
//            chooseImageOrTakePhoto()
//        }

        return binding.root
    }

    private fun currentUser() {
        progressDialog = ProgressDialog(requireContext())
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Loading ...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val firebaseUser = FirebaseAuth.getInstance().currentUser

        if (firebaseUser != null) {
            val currentUserId = firebaseUser.uid

            val database = FirebaseDatabase.getInstance().reference.child("User").child(currentUserId)
            database.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val user = snapshot.getValue(User::class.java)
                        val id = user?.id
                        val nama = user?.nama
//                        val email = user?.email
                        val phone = user?.phone
                        val alamat = user?.alamat

                        binding.nameInput.setText(nama)
//                        binding.emailInput.setText(email)
                        binding.phoneInput.setText(phone)
                        binding.alamatInput.setText(alamat)
                        binding.idUser.text = id


                        progressDialog.hide()

//                        // Check if the avatar URL is not null before loading the image
//                        if (!profileImageURL.isNullOrEmpty()) {
//                            Glide.with(requireContext())
//                                .load(profileImageURL)
//                                .placeholder(R.mipmap.avatar_image_round)
//                                .into(binding.profileImageView)
//                        } else {
//                            // Set a default placeholder image if the avatar URL is null or empty
//                            binding.profileImageView.setImageResource(R.mipmap.avatar_image_round)
//                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    progressDialog.hide()
                }
            })

            binding.simpanBtn.setOnClickListener {
                progressDialog = ProgressDialog(requireContext())
                progressDialog.setTitle("Please Wait")
                progressDialog.setMessage("Loading ...")
                progressDialog.setCancelable(false)
                progressDialog.show()

                val getName = binding.nameInput.text.toString().trim()
                val getPhone = binding.phoneInput.text.toString().trim()
                val getAlamat = binding.alamatInput.text.toString().trim()
                val getId = binding.idUser.text.toString().trim()


                updateProfile(currentUserId, getName, getPhone, getAlamat, getId)
            }
        } else {
            // Handle the case when the user is not signed in or FirebaseAuth.getInstance().currentUser is null
        }
    }




    private fun updateProfile(
        currentUserId: String,
        getName: String?,
        getPhone: String?,
        getAlamat: String?,
        getId: String?
    ) {
        ref = FirebaseDatabase.getInstance().reference.child("User").child(currentUserId)
        val userUpdates = HashMap<String, Any>()
        userUpdates["nama"] = getName.toString()
        userUpdates["phone"] = getPhone.toString()
        userUpdates["alamat"] = getAlamat.toString()

        ref.updateChildren(userUpdates).addOnCompleteListener {
            if (it.isSuccessful) {
                progressDialog.hide()
                Toast.makeText(context, "Update Profil Berhasil", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            } else {
                progressDialog.hide()
                Toast.makeText(context, "Gagal Update Profil", Toast.LENGTH_SHORT).show()
            }
        }
    }
}