package com.d3if0104.banksampah

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.d3if0104.banksampah.databinding.ActivityResetPasswordBinding
import com.google.firebase.auth.FirebaseAuth
//import java.util.regex.Pattern

class ResetPasswordActivity : AppCompatActivity() {

    lateinit var binding: ActivityResetPasswordBinding
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.resetButton.setOnClickListener{
            val email = binding.editEmailReset.text.toString()
            val editEmail = binding.editEmailReset

            if (email.isEmpty()) {
                editEmail.error = "Email Tidak Boleh Kosong"
                editEmail.requestFocus()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                editEmail.error = "Email Tidak Valid"
                editEmail.requestFocus()
                return@setOnClickListener
            }

            FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener {

                if (it.isSuccessful) {
                    Toast.makeText(this, "Email Rese Password Telah Dikirim", Toast.LENGTH_SHORT)
                        .show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    Toast.makeText(this,"${it.exception?.message}", Toast.LENGTH_SHORT)
                        .show()
                }

            }
        }
    }
}