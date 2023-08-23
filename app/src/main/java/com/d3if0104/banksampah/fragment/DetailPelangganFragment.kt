package com.d3if0104.banksampah.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import com.d3if0104.banksampah.databinding.FragmentDetailPelangganBinding
import com.d3if0104.banksampah.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import org.d3ifcool.appbangundatar.model.Pelanggan
import com.d3if0104.banksampah.model.TarikSaldo
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetailPelangganFragment : Fragment() {
    private lateinit var binding: FragmentDetailPelangganBinding
    private lateinit var ref: DatabaseReference
    private lateinit var ref2: Query
    private lateinit var ref3: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var idPelanggan: String

    @SuppressLint("SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailPelangganBinding.inflate(layoutInflater)

        auth = FirebaseAuth.getInstance()
        ref = FirebaseDatabase.getInstance().getReference("User")

        binding.tarikSaldoBtn.setOnClickListener {
            binding.tarikSaldoCV.visibility = View.VISIBLE
            binding.inputTarikSaldo.setText("0")
        }

        binding.batalBtn.setOnClickListener {
            binding.tarikSaldoCV.visibility = View.GONE
        }

        binding.tarikBtn.setOnClickListener {
            val nama = binding.tvNama.text.toString()
            val sal = binding.tvsaldo.text.toString()
            val tarik = binding.inputTarikSaldo.text.toString()
            val replaceTarik = tarik.replace(".", "")
            val replaceTarik2 = replaceTarik.replace(",", "")
            val tarikInt = replaceTarik2.toInt()
            val replaceHarga = sal.replace(".", "")
            val replaceharga2 = replaceHarga.replace("Rp ", "")
            val replaceharga3 = replaceharga2.replace(",", "")
            val saldoInt = replaceharga3.toInt()

            if (saldoInt < tarikInt || saldoInt <= 0) {
                Toast.makeText(context, "Saldo anda tidak cukup", Toast.LENGTH_SHORT).show()
            } else if (tarik == "0") {
                Toast.makeText(context, "Pastikan isi dengan benar", Toast.LENGTH_SHORT).show()
            } else {
                val saldoPelangganInt = saldoInt - tarikInt
                val saldoPelanggan = saldoPelangganInt.toString()
                tarik(saldoPelanggan)

                val simpleDateFormat = SimpleDateFormat("dd MMMM yyy")
                val tanggal: String = simpleDateFormat.format(Date())
                val formater = NumberFormat.getInstance().format(tarikInt)
                val sSaldo = formater.toString()
                val saldo = "Rp $sSaldo"
                val timestamp = System.currentTimeMillis()
                tarikSaldo(nama, saldo, tanggal, timestamp)
            }
        }

        binding.inputTarikSaldo.addTextChangedListener(object : TextWatcher {
            private var current = ""
            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int,
                count: Int
            ) {
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) {
            }

            override fun afterTextChanged(s: Editable) {
                if (s.toString() != current) {
                    binding.inputTarikSaldo.removeTextChangedListener(this)
                    val local = Locale("id", "id")
                    val replaceable = String.format(
                        "[Rp,.\\s]",
                        NumberFormat.getCurrencyInstance().currency
                            .getSymbol(local)
                    )
                    val cleanString = s.toString().replace(
                        replaceable.toRegex(),
                        ""
                    )
                    val parsed: Double
                    parsed = try {
                        cleanString.toDouble()
                    } catch (e: NumberFormatException) {
                        0.00
                    }
                    val formatter = NumberFormat
                        .getCurrencyInstance(local)
                    formatter.maximumFractionDigits = 0
                    formatter.isParseIntegerOnly = true
                    val formatted = formatter.format(parsed)
                    val replace = String.format(
                        "[Rp\\s]",
                        NumberFormat.getCurrencyInstance().currency
                            .getSymbol(local)
                    )
                    val clean = formatted.replace(replace.toRegex(), "")
                    current = formatted
                    binding.inputTarikSaldo.setText(clean)
                    binding.inputTarikSaldo.setSelection(clean.length)
                    binding.inputTarikSaldo.addTextChangedListener(this)
                }
            }
        })

        saldoUser()
        showPelanggan()
//        setId()

        return binding.root
    }


    private fun saldoUser() {
//        val currentUserId = auth.currentUser!!.uid
        setFragmentResultListener("id") { requestKey, bundle ->
            val id = bundle.getString("id").toString()
            ref2 =
                FirebaseDatabase.getInstance().reference.child("User").child(id).orderByChild("id")
                    .equalTo(id)
            ref2.addValueEventListener(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (data in snapshot.children) {
                            val item = data.getValue(Pelanggan::class.java)
                            val saldo = item!!.saldo
                            val saldoInt = saldo.toInt()
                            val formater = NumberFormat.getInstance().format(saldoInt)
                            val saldoS = formater.toString()
                            val saldoPelanggan = "Rp $saldoS"
                            binding.tvsaldo.text = saldoPelanggan
                        }
                    } else {
                        binding.tvsaldo.text = "0"
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

        }
    }

    private fun showPelanggan() {
        setFragmentResultListener("id") { _, bundle ->
            val id = bundle.getString("id")
            ref2 = FirebaseDatabase.getInstance().reference.child("User").orderByChild("id")
                .equalTo(id)
            ref2.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (snap in snapshot.children) {
                            val pelanggan = snap.getValue(User::class.java)
                            val nama = pelanggan!!.nama
                            val alamat = pelanggan.alamat
                            val nomor = pelanggan.phone
                            val saldo = pelanggan.saldo

                            idPelanggan = pelanggan.id.toString()
                            binding.tvNama.text = nama
                            binding.tvAlamat.text = alamat
                            if (nomor != "") {
                                binding.tvNomor.text = nomor
                            } else {
                                binding.tvNomor.text = "-"
                            }
                            binding.tvsaldo.text = saldo.toString()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
    }

    private fun tarikSaldo(nama: String, saldo: String, tanggal: String, timestamp: Long) {
        val idtarikSaldo = ref.push().key!!
        val input = TarikSaldo(idTarik = idtarikSaldo.toString(), idUser = idPelanggan, nama = nama, saldo = saldo, tanggal = tanggal, timestamp = timestamp)
        ref3 = FirebaseDatabase.getInstance().getReference("TarikSaldo").child(idPelanggan).child(idtarikSaldo)
        ref3.setValue(input).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("berhasil", "data tersimpan")
            } else {
                val message = it.exception!!.toString()
                Toast.makeText(context, "Error : $message", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun tarik(saldoPelanggan: String) {
        val saldoUpdate = HashMap<String, Any>()
        saldoUpdate["saldo"] = saldoPelanggan.toInt()
        ref.child(idPelanggan).updateChildren(saldoUpdate)
        Toast.makeText(context, "Saldo berhasil ditarik", Toast.LENGTH_SHORT).show()
        binding.tarikSaldoCV.visibility = View.GONE
    }

//    private fun setId() {
//        setFragmentResultListener("id") { _, bundle ->
//            val id = bundle.getString("id")
//            binding.idPelanggan.text = id
//        }
//    }
}
