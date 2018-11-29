package com.toshiba.book.other

import android.content.Context
import android.widget.Toast
import java.text.NumberFormat
import java.util.*

object Utility {
    fun getDate(time: Long): String? {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        var month: String? = null

        when {
            calendar.get(Calendar.MONTH) == 0 -> month = "Januari"
            calendar.get(Calendar.MONTH) == 1 -> month = "Februari"
            calendar.get(Calendar.MONTH) == 2 -> month = "Maret"
            calendar.get(Calendar.MONTH) == 3 -> month = "April"
            calendar.get(Calendar.MONTH) == 4 -> month = "Mei"
            calendar.get(Calendar.MONTH) == 5 -> month = "Juni"
            calendar.get(Calendar.MONTH) == 6 -> month = "Juli"
            calendar.get(Calendar.MONTH) == 7 -> month = "Agustus"
            calendar.get(Calendar.MONTH) == 8 -> month = "September"
            calendar.get(Calendar.MONTH) == 9 -> month = "Oktober"
            calendar.get(Calendar.MONTH) == 10 -> month = "November"
            calendar.get(Calendar.MONTH) == 11 -> month = "Desember"
        }

        return ""  + calendar.get(Calendar.DAY_OF_MONTH) + " " + month + " "+ + calendar.get(Calendar.YEAR)
    }

    fun getRupiah(rupiah: Long): String? {
        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)

        return formatRupiah.format(rupiah.toDouble())
    }

    fun toastCustom (context: Context, title: String){
        Toast.makeText(context, ""+title, Toast.LENGTH_SHORT).show()
    }
}