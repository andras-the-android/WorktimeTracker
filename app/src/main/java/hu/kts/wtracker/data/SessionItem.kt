package hu.kts.wtracker.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.concurrent.TimeUnit

@Entity(tableName = "Session")
data class SessionItem(
    @PrimaryKey @ColumnInfo(name = "timestamp") val timestamp: Long,
    @ColumnInfo(name = "session_timestamp") val sessionTimestamp: Long,
    @ColumnInfo(name = "period") val period: Period
) {

    @Ignore
    var durationSeconds = 0
        private set

    fun calcDuration(nextPeriodStart: Long) {
        durationSeconds = TimeUnit.MILLISECONDS.toSeconds(nextPeriodStart - timestamp).toInt()
    }
}
