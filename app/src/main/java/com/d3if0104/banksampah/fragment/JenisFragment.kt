package com.d3if0104.banksampah.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.d3if0104.banksampah.R
import com.d3if0104.banksampah.adapter.AdapterJenisSampah
import com.d3if0104.banksampah.databinding.FragmentJenisBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.d3if0104.banksampah.model.JenisSampah
import java.text.NumberFormat
import java.util.Locale

class JenisFragment : Fragment() {
    private lateinit var binding: FragmentJenisBinding
    private lateinit var firebaseUser: FirebaseUser
    lateinit var ref: DatabaseReference
    private lateinit var ref2: Query
    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    lateinit var adapter: AdapterJenisSampah
    private val sampahArrayList = arrayListOf<JenisSampah>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentJenisBinding.inflate(layoutInflater)

        (activity as AppCompatActivity).supportActionBar?.title = "Tambah Jenis Sampah"

        auth = FirebaseAuth.getInstance()
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        ref = FirebaseDatabase.getInstance().getReference("JenisSampah").child(firebaseUser.uid)

        recyclerView = binding.recycleView
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        adapter = AdapterJenisSampah(arrayListOf(), object : AdapterJenisSampah.OnAdapterListener {
            override fun deleteSampah(jenis: JenisSampah, v: View) {
                AlertDialog.Builder(requireContext()).apply {
                    setMessage(R.string.pesan_hapus_sampah)
                    setPositiveButton("HAPUS") { _, _ ->
                        val idSampah = jenis.id
                        ref2 = FirebaseDatabase.getInstance().reference.child("JenisSampah")
                            .child(idSampah)
                        ref2.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    snapshot.ref.removeValue()
                                    Toast.makeText(
                                        context,
                                        "Berhasil Menghapus sampah",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    showListSampah()

                                } else {
                                    showListSampah()
                                }

                            }

                            override fun onCancelled(error: DatabaseError) {

                            }

                        })

                    }
                    setNegativeButton("Batal") { dialog, _ ->
                        dialog.cancel()
                    }
                    show()
                }
            }

        }, ref)
        recyclerView.adapter = adapter

        binding.harga.addTextChangedListener(object : TextWatcher {
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
                    binding.harga.removeTextChangedListener(this)
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
                    binding.harga.setText(clean)
                    binding.harga.setSelection(clean.length)
                    binding.harga.addTextChangedListener(this)
                }
            }
        })

        binding.simpanBtn.setOnClickListener() {
            val jenisSampah = binding.jenisSampah.text.toString()
            val har = binding.harga.text.toString()
            val replaceHarga = har.replace(".", "")
            val harga = replaceHarga.replace(",", "")
            val satuan = binding.spSatuan.selectedItem.toString()

            if (jenisSampah.isEmpty() && harga.isEmpty()) {
                Toast.makeText(context, "Pastikan data terisi dengan benar", Toast.LENGTH_SHORT)
                    .show()
            } else {
                simpan(jenisSampah, harga, satuan)
                showListSampah()
            }
        }

        binding.buttonSearch.setOnClickListener {
            val keyword = binding.etSearch.text.toString()
            searchData(keyword)
        }

        showListSampah()

        return binding.root
    }

    fun simpan(jenisSampah: String, harga: String, satuan: String) {
        val idSampah = ref.push().key!!
        val input = JenisSampah(idSampah, jenisSampah, harga, satuan)

        ref = FirebaseDatabase.getInstance().getReference("JenisSampah").child(idSampah)
        ref.setValue(input).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Berhasil menambahkan jenis sampah", Toast.LENGTH_SHORT)
                    .show()
                binding.jenisSampah.setText("")
                binding.harga.setText("")

            } else {
                val message = it.exception!!.toString()
                Toast.makeText(context, "Error : $message", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showListSampah() {
        ref = FirebaseDatabase.getInstance().reference.child("JenisSampah")
        ref.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                sampahArrayList.clear()
                if (snapshot.exists()) {
                    for (data in snapshot.children) {
                        val item = data.getValue(JenisSampah::class.java)
                        sampahArrayList.add(item!!)
                    }
                    adapter.setData(sampahArrayList)
                } else {
                    adapter.setData(sampahArrayList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun searchData(keyword: String) {
        ref2 = FirebaseDatabase.getInstance().getReference("JenisSampah")
        ref2.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val searchResults = mutableListOf<JenisSampah>()

                for (data in snapshot.children) {
                    val jenis = data.getValue(JenisSampah::class.java)
                    jenis?.let {
                        val jenisSampah = it.jenisSampah
                        if (jenisSampah != null) {
                            if (jenisSampah.contains(keyword, ignoreCase = true)) {
                                searchResults.add(it)
                            }
                        }
                    }
                }

                adapter.setData(searchResults)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onResume() {
        super.onResume()
        val stringArray = resources.getStringArray(R.array.satuan)
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, stringArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spSatuan.adapter = adapter
    }
}