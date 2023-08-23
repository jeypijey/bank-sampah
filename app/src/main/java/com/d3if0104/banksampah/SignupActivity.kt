package com.d3if0104.banksampah

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.d3if0104.banksampah.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.d3if0104.banksampah.model.User

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var ref: DatabaseReference
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (this as? AppCompatActivity)?.supportActionBar?.apply {
            setDisplayShowTitleEnabled(false);
            setDisplayHomeAsUpEnabled(false);
            hide();
        }

        auth = FirebaseAuth.getInstance()
        ref = FirebaseDatabase.getInstance().getReference("User")

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.signupButton.setOnClickListener {
            progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Please Wait")
            progressDialog.setMessage("Loading ...")
            progressDialog.setCancelable(false)
            progressDialog.show()

            val id = ref.push().key!!
            val nama = binding.name.text.toString().trim()
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()

            val saldo = 0
            val phone = ""
            val alamat = ""

            // Jika false maka akun adalah User, jika true maka akun adalah admin
            val admin = false

            if (nama.isEmpty() && email.isEmpty() && password.isEmpty()) {
                progressDialog.hide()
                Toast.makeText(this, "Gagal, pastikan data terisi dengan benar", Toast.LENGTH_SHORT)
                    .show()
            } else {
                daftarUser(
                    id = id,
                    nama = nama,
                    saldo = saldo,
                    email = email,
                    password = password,
                    admin = admin,
                    phone = phone,
                    alamat = alamat
                )
            }
        }

        binding.loginText.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun daftarUser(
        id: String,
        nama: String,
        saldo: Int,
        email: String,
        password: String,
        phone: String,
        alamat: String,
        admin: Boolean
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    progressDialog.hide()
                    saveUser(
                        id = id,
                        nama = nama,
                        saldo = saldo,
                        email = email,
                        alamat = alamat,
                        phone = phone,
                        admin = admin
                    )

                } else {
                    progressDialog.hide()
                    Toast.makeText(this, "Register gagal", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveUser(
        id: String,
        nama: String,
        saldo: Int,
        email: String,
        alamat: String?,
        phone: String?,
        admin: Boolean?
    ) {
        val currentUser = auth.currentUser!!.uid
        ref = FirebaseDatabase.getInstance().reference.child("User")
        val idUser = auth.uid

        val user = User(
            id = idUser,
            nama = nama,
            saldo = saldo,
            email = email,
            alamat = alamat,
            phone = phone,
            admin = admin
        )
        ref.child(currentUser).setValue(user).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                progressDialog.hide()
                Toast.makeText(this, "Register berhasil", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, UserMainActivity::class.java))
                finish()
            } else {
                progressDialog.hide()
                val message = it.exception!!.toString()
                Toast.makeText(this, "Error : $message", Toast.LENGTH_SHORT).show()
            }
        }
    }
}