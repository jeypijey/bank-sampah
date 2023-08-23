package com.d3if0104.banksampah.model

data class User(
    val id: String? = null,
    val nama: String? = null,
    val saldo: Int? = 0,
    val email: String? = null,
    val alamat: String? = null,
//    val avatar: String?=null,
    val phone : String?= null,
    val admin : Boolean?= null,
)

