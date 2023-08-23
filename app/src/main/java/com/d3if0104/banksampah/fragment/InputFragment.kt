package com.d3if0104.banksampah.fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
//import androidx.recyclerview.widget.RecyclerView
import com.d3if0104.banksampah.DateSelected
import com.d3if0104.banksampah.R
//import com.d3if0104.banksampah.adapter.AdapterCariPelanggan
import com.d3if0104.banksampah.databinding.FragmentInputBinding
import com.d3if0104.banksampah.model.DataSampah
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale

class InputFragment : Fragment(), AdapterView.OnItemSelectedListener, DateSelected {
    private lateinit var binding: FragmentInputBinding
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var ref: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var listDatabase: DatabaseReference
    private lateinit var listPelangganRef: DatabaseReference
//    private lateinit var ref2: Query
    private lateinit var ref3: DatabaseReference
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var adapter: AdapterCariPelanggan
    private lateinit var idResultPelanggan: String

//    private val calendar: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInputBinding.inflate(layoutInflater)

        (activity as AppCompatActivity).supportActionBar?.title = "Tambah Data Sampah"

        auth = FirebaseAuth.getInstance()
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        ref = FirebaseDatabase.getInstance().getReference("DataSampah")
        ref3 = FirebaseDatabase.getInstance().getReference("User")
        listDatabase =
            FirebaseDatabase.getInstance().getReference("JenisSampah").child(firebaseUser.uid)
        listPelangganRef =
            FirebaseDatabase.getInstance().getReference("Pelanggan").child(firebaseUser.uid)


        binding.etPelanggan.setOnClickListener {
            findNavController().navigate(R.id.action_inputFragment_to_pilihPelangganFragment)
        }

        binding.etJenis.setOnClickListener {
            findNavController().navigate(R.id.action_inputFragment_to_pilihJenisFragment)
        }

        binding.inputTanggal.setOnClickListener {
            showDatePicker()
        }

        binding.inputBerat.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val berat = binding.inputBerat.text.toString()
                val harga = binding.inputHarga.text.toString()
                if (berat.isEmpty()) {
                    binding.total.text = "Rp 0"
                } else {
                    val iBerat = berat.toDouble()
                    val replaceHarga = harga.replace(".", "")
                    val replaceharga2 = replaceHarga.replace("Rp ", "")
                    val replaceharga3 = replaceharga2.replace(",", "")
                    val iHarga = replaceharga3.toInt()
                    val sTotal = iBerat * iHarga
                    val formater = NumberFormat.getInstance().format(sTotal)
                    val total = formater.toString()
                    binding.total.text = "Rp $total"
                }

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(editable: Editable) {}
        })

        binding.inputBtn.setOnClickListener {
            val namaF = binding.etPelanggan.text.toString()
            val nama = namaF.toString()
            val berat = binding.inputBerat.text.toString()
            val harga = binding.inputHarga.text.toString()
            val namaS = binding.etJenis.text.toString()
            val tanggal = binding.inputTanggal.text.toString()
            val namaSampah = namaS.toString()


            if (nama.isEmpty() && berat.isEmpty() || nama.isNotEmpty() && berat.isEmpty() || nama.isEmpty() && berat.isNotEmpty()) {
                Toast.makeText(context, "Pastikan data terisi dengan benar", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val iBerat = berat.toDouble()
                val replaceHarga = harga.replace(".", "")
                val replaceharga2 = replaceHarga.replace("Rp ", "")
                val replaceharga3 = replaceharga2.replace(",", "")
                val iHarga = replaceharga3.toInt()
                val sTotal = iBerat * iHarga
                val formater = NumberFormat.getInstance().format(sTotal)
                val bTotal = formater.toString()
                val total = "Rp $bTotal"
                val timestamp = System.currentTimeMillis()

                val totalInput = sTotal.toInt()

                inputdata(nama, namaSampah, berat, harga, tanggal, total, totalInput, timestamp)
            }
        }

        tanggalHariIni()
        setNama()
        setJenis()

        return binding.root
    }

    @SuppressLint("SimpleDateFormat")
    private fun tanggalHariIni() {
        val simpleDateFormat = SimpleDateFormat("dd MMMM yyy")
        val hariIni: String = simpleDateFormat.format(Date())

        binding.inputTanggal.setText(hariIni)
    }

    private fun setNama() {
        setFragmentResultListener("nama") { _, bundle ->
            val resultId = bundle.getString("id")
            val result = bundle.getString("nama")
            binding.etPelanggan.setText(result)
            idResultPelanggan = resultId.toString()
        }
    }

    private fun setJenis() {
        setFragmentResultListener("namaSampah") { _, bundle ->
            val result = bundle.getString("namaSampah")
            binding.etJenis.setText(result)
        }
        setFragmentResultListener("harga") { _, bundle ->
            val result = bundle.getString("harga")
            binding.inputHarga.text = result
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val twoWeeksAgoCalendar = Calendar.getInstance()
        twoWeeksAgoCalendar.add(Calendar.WEEK_OF_YEAR, -2)

        val datePickerDialog = context?.let {
            DatePickerDialog(
                it,
                { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                    calendar.set(selectedYear, selectedMonth, selectedDayOfMonth)
                    val selectedDate = formatDate(calendar.time)
                    binding.inputTanggal.setText(selectedDate)
                },
                year,
                month,
                dayOfMonth
            )
        }

        datePickerDialog?.datePicker?.maxDate = System.currentTimeMillis()
        datePickerDialog?.datePicker?.minDate = twoWeeksAgoCalendar.timeInMillis

        datePickerDialog?.show()
    }

    private fun formatDate(date: Date): String {
        val format = SimpleDateFormat("dd MMMM yyy", Locale.getDefault())
        return format.format(date)
    }

    class DatePickerFragment(private val dateSelected: InputFragment) : DialogFragment(),
        DatePickerDialog.OnDateSetListener {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            return DatePickerDialog(requireContext(), this, year, month, day)
        }

        override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
            dateSelected.receiveDate(year, month, day)
        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun receiveDate(year: Int, month: Int, day: Int) {
        val calendar = GregorianCalendar()
        calendar.set(Calendar.DAY_OF_MONTH, day)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.YEAR, year)
        val viewFormatter = SimpleDateFormat("dd MMMM yyy")
        val viewFormattedDate: String = viewFormatter.format(calendar.time)

        binding.inputTanggal.setText(viewFormattedDate)
    }


    private fun inputdata(
        nama: String,
        namaSampah: String,
        berat: String,
        harga: String,
        tanggal: String,
        total: String,
        totalInput: Int,
        timestamp: Long
    ) {

//        val idData = ref.push().key!!
        val input = DataSampah(
            id = idResultPelanggan,
            nama = nama,
            namaSampah = namaSampah,
            berat = berat,
            harga = harga,
            tanggal = tanggal,
            total = totalInput,
            timestamp = timestamp
        )

        ref.child(firebaseUser.uid).push().setValue(input).addOnCompleteListener {
            if (it.isSuccessful) {
                getSaldo(totalInput)
                Toast.makeText(context, "Input data berhasil", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            } else {
                val message = it.exception!!.toString()
                Toast.makeText(context, "Error : $message", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getSaldo(totalInput: Int) {
        val database = FirebaseDatabase.getInstance().reference
            .child("User")
            .child(idResultPelanggan)
            .child("saldo")

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val saldoValue = snapshot.value
                val saldoInt = saldoValue.toString().toInt()
                val calculateSaldo = saldoInt + totalInput

                saldoUser(calculateSaldo)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
            }
        })
    }

    private fun saldoUser(updateSaldoValue: Int) {
        val saldoUpdate = HashMap<String, Any>()
        saldoUpdate["saldo"] = updateSaldoValue

        ref3.child(idResultPelanggan).updateChildren(saldoUpdate)
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        TODO("Not yet implemented")
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

}