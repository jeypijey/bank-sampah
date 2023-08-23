package com.d3if0104.banksampah

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.d3if0104.banksampah.databinding.ActivityLoginBinding
import com.d3if0104.banksampah.model.User
//import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
//    private val contract = FirebaseAuthUIActivityResultContract()
    private lateinit var auth: FirebaseAuth
    private lateinit var ref: Query
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        auth = Firebase.auth

        binding.loginButton.setOnClickListener {
            progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Please Wait")
            progressDialog.setMessage("Loading ...")
            progressDialog.setCancelable(false)
            progressDialog.show()

            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                login(email, password)
            } else {
                Toast.makeText(applicationContext, "Silahkan isi semua data", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        binding.forgotText.setOnClickListener {
            val intent = Intent(this, ResetPasswordActivity::class.java)
            startActivity(intent)
        }

        val linkTextView: TextView = binding.signupText
        linkTextView.movementMethod = LinkMovementMethod.getInstance()
        linkTextView.setLinkTextColor(Color.GREEN)

        linkTextView.setOnClickListener {
            val intent = Intent(this@LoginActivity, SignupActivity::class.java)
            startActivity(intent)
        }
    }

    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    checkUser(email)
                } else {
                    progressDialog.hide()
                    Toast.makeText(this, "Akun tidak terdaftar", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveUserRole(role: String) {
        val sharedPreferences = getSharedPreferences("UserRolePrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("userRole", role)
        editor.apply()
    }

    private fun checkUser(email: String) {
        ref = FirebaseDatabase.getInstance().reference.child("User").orderByChild("email")
            .equalTo(email)
        ref.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (tranSnap in snapshot.children) {
                        val data = tranSnap.getValue(User::class.java)
                        val roleAdmin = data?.admin
                        println(data)
                        if (roleAdmin == false) {
                            progressDialog.hide()
                            saveUserRole("user")
                            Toast.makeText(
                                this@LoginActivity,
                                "Berhasil login sebagai user",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(this@LoginActivity, UserMainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            progressDialog.hide()
                            saveUserRole("admin")
                            Toast.makeText(
                                this@LoginActivity,
                                "Berhasil login sebagai admin",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled if needed
            }
        })
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val sharedPreferences = getSharedPreferences("UserRolePrefs", MODE_PRIVATE)
            val userRole = sharedPreferences.getString("userRole", "")
            if (userRole == "admin") {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                startActivity(Intent(this, UserMainActivity::class.java))
            }
            finish()
        }
    }
}