package com.d3if0104.banksampah.model

data class DataSampah(
    val id: String = "",
    val nama: String = "",
    val namaSampah: String = "",
    val berat: String = "",
    val harga: String = "",
    val tanggal: String = "",
    val catatan: String = "",
    val total: Int = 0,
    val timestamp: Long? = null
)
