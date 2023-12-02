package hu.kts.wtracker.data

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

data class PeriodHistoryItem(
    val timestamp: Long,
    val period: Period
) {

    var durationSeconds = 0
        private set

    fun calcDuration(nextPeriodStart: Long) {
        durationSeconds = TimeUnit.MILLISECONDS.toSeconds(nextPeriodStart - timestamp).toInt()
    }

    companion object {
        private val format = SimpleDateFormat.getTimeInstance(DateFormat.SHORT)
    }
}
