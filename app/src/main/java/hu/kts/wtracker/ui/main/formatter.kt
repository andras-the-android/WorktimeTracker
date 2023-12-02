package hu.kts.wtracker.ui.main

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import java.util.concurrent.TimeUnit

@SuppressLint("SimpleDateFormat")
private val mmSsFormat = SimpleDateFormat("mm:ss")
@SuppressLint("SimpleDateFormat")
private val hMmSsFormat = SimpleDateFormat("H:mm:ss").apply {
    timeZone = TimeZone.getTimeZone("GMT")
}

fun Int.hMmSsFormat(): String {
    val millis = TimeUnit.SECONDS.toMillis(this.toLong())
    return hMmSsFormat.format(Date(millis))
}

fun Long.mmSsFormat(): String {
    return mmSsFormat.format(Date(this))
}
