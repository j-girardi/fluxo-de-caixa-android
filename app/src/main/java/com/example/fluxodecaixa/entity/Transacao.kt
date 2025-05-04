package com.example.fluxodecaixa.entity

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Transacao(
    var _id: Int,
    var tipo: String,
    var detalhe: String,
    var valor: Double,
    var data: String
) {
    fun getFormattedDateTime(): Date? {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return formatter.parse(data)
    }
}