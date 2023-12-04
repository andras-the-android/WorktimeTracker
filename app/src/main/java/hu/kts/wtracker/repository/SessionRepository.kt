package hu.kts.wtracker.repository

import hu.kts.wtracker.data.Period
import hu.kts.wtracker.data.SessionItem
import hu.kts.wtracker.data.SummaryData
import hu.kts.wtracker.persistency.Preferences
import hu.kts.wtracker.persistency.SessionDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.Clock
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SessionRepository @Inject constructor(
    private val dao: SessionDao,
    private val clock: Clock,
    private val preferences: Preferences,
    private val coroutineScope: CoroutineScope,
) {
    private var periodHistory = mutableListOf<SessionItem>()

    fun endSession() {
        periodHistory.clear()
        preferences.clearActiveSessionTimestamp()
    }

    fun addToHistory(period: Period) {
        coroutineScope.launch {
            val newItem = SessionItem(clock.millis(), preferences.getOrCreateActiveSessionTimestamp(), period, )
            periodHistory.lastOrNull()?.calcDuration(newItem.timestamp)
            dao.insert(newItem)
        }
    }

    suspend fun restore(): SummaryData {
        if (preferences.hasActiveSession()) return SummaryData.empty // nothing to restore
        periodHistory = dao.getAll(preferences.getOrCreateActiveSessionTimestamp()).toMutableList()

        val period = periodHistory.last().period

        var workSec = 0
        var restSec = 0
        var workSegmentSec = 0
        var restSegmentSec = 0

        periodHistory.forEachIndexed { index, item ->
            if (index < periodHistory.size - 1) {
                item.calcDuration(periodHistory[index + 1].timestamp)
            }
            when (item.period) {
                Period.WORK -> {
                    workSegmentSec = item.getOngoingDuration()
                    workSec += workSegmentSec
                }
                Period.REST -> {
                    restSegmentSec = item.getOngoingDuration()
                    restSec += restSegmentSec
                }
                else -> {}
            }
        }

        return SummaryData(workSec, restSec, workSegmentSec, restSegmentSec, period)
    }

    /**
     * Returns the duration of a period even if it's not finished yet
     */
    private fun SessionItem.getOngoingDuration(): Int {
        return if (durationSeconds == 0) {
            TimeUnit.MILLISECONDS.toSeconds(clock.millis() - timestamp).toInt()
        } else {
            durationSeconds
        }
    }
}
