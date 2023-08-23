package com.d3if0104.banksampah.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.d3if0104.banksampah.databinding.FragmentTambahPelangganBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.d3ifcool.appbangundatar.model.Pelanggan

class TambahPelangganFragment : Fragment() {
    private lateinit var binding: FragmentTambahPelangganBinding
    private lateinit var firebaseUser : FirebaseUser
    private lateinit var ref: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTambahPelangganBinding.inflate(layoutInflater)

        auth = FirebaseAuth.getInstance()
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        ref = FirebaseDatabase.getInstance().getReference("Pelanggan")

        binding.tambahBtn.setOnClickListener(){
            val nama = binding.inputNama.text.toString()
            val alamat = binding.inputAlamat.text.toString()
            val noHp = binding.inputNoHP.text.toString()
            val saldo = "0"

            if(nama.isEmpty() || alamat.isEmpty() || noHp.isEmpty()){
                Toast.makeText(context,"Pastikan data terisi dengan benar", Toast.LENGTH_SHORT).show()
            }else if(noHp.length < 10 || noHp.length > 12){
                Toast.makeText(context,"Pastikan nomor hp anda benar", Toast.LENGTH_SHORT).show()
            }else {
                tambahPelanggan(nama, alamat, noHp, saldo)
            }
        }

        return binding.root
    }

    private fun tambahPelanggan(nama: String, alamat: String, noHp: String, saldo: String) {
        val currentUserId = auth.currentUser!!.uid
        val id = ref.push().key!!
        val input = Pelanggan(id, nama, alamat, noHp, saldo)

        ref.child(currentUserId).child(id).setValue(input).addOnCompleteListener {
            if (it.isSuccessful){
                Log.d("tambah pelanggan","data tersimpan")
                Toast.makeText(context, "Input data berhasil", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }else{
                val message = it.exception!!.toString()
                Toast.makeText(context,"Error : $message", Toast.LENGTH_SHORT).show()
            }
        }

    }
}