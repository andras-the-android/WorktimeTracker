package hu.kts.wtracker.data

import java.util.concurrent.TimeUnit

data class SessionItem(
    val timestamp: Long,
    val sessionTimestamp: Long,
    val period: Period
) {

    var durationSeconds = 0
        private set

    fun calcDuration(nextPeriodStart: Long) {
        durationSeconds = TimeUnit.MILLISECONDS.toSeconds(nextPeriodStart - timestamp).toInt()
    }
}
