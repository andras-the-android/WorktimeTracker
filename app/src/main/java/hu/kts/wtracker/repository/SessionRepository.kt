package hu.kts.wtracker.repository

import hu.kts.wtracker.data.Period
import hu.kts.wtracker.data.SessionItem
import hu.kts.wtracker.data.SummaryData
import hu.kts.wtracker.persistency.Preferences
import hu.kts.wtracker.persistency.SessionDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Clock
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepository @Inject constructor(
    private val dao: SessionDao,
    private val clock: Clock,
    private val preferences: Preferences,
    private val coroutineScope: CoroutineScope,
) {
    private var _sessionItems = MutableStateFlow(listOf<SessionItem>())
    val sessionItems = _sessionItems.asStateFlow()

    fun endSession() {
        _sessionItems.value = listOf()
        preferences.clearActiveSessionTimestamp()
    }

    fun addToHistory(period: Period) {
        coroutineScope.launch {
            val newItem = SessionItem(clock.millis(), preferences.getOrCreateActiveSessionTimestamp(), period, )
            _sessionItems.value.lastOrNull()?.calcDuration(newItem.timestamp)
            _sessionItems.update { it + newItem }
            dao.insert(newItem)
        }
    }

    suspend fun restore(): SummaryData {
        if (!preferences.hasActiveSession()) return SummaryData.empty // nothing to restore
        val sessionItems = dao.getAll(preferences.getOrCreateActiveSessionTimestamp()).toMutableList()

        val period = sessionItems.last().period

        var workSec = 0
        var choreSec = 0
        var restSec = 0
        var workSegmentSec = 0
        var choreSegmentSec = 0
        var restSegmentSec = 0

        sessionItems.forEachIndexed { index, item ->
            if (index < sessionItems.size - 1) {
                item.calcDuration(sessionItems[index + 1].timestamp)
            }
            when (item.period) {
                Period.WORK -> {
                    workSegmentSec = item.getOngoingDuration()
                    workSec += workSegmentSec
                }

                Period.CHORE -> {
                    choreSegmentSec = item.getOngoingDuration()
                    choreSec += restSegmentSec
                }

                Period.REST -> {
                    restSegmentSec = item.getOngoingDuration()
                    restSec += restSegmentSec
                }

                Period.STOPPED -> {}
            }
        }

        _sessionItems.value = sessionItems

        return SummaryData(workSec, choreSec, restSec, workSegmentSec, choreSegmentSec, restSegmentSec, period)
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
