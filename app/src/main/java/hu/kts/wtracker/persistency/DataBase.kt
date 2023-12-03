package hu.kts.wtracker.persistency

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import hu.kts.wtracker.KEY_PERIOD_HISTORY
import hu.kts.wtracker.data.Period
import hu.kts.wtracker.data.PeriodHistoryItem
import hu.kts.wtracker.data.SummaryData
import java.time.Clock
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class DataBase @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val clock: Clock,
) {
    private val gson = Gson()
    private val periodHistoryItemType = object : TypeToken<ArrayList<PeriodHistoryItem>>() {}.type

    private var periodHistory = ArrayList<PeriodHistoryItem>()

    fun clearHistory() {
        periodHistory.clear()
        persist()
    }

    fun addToHistory(period: Period) {
        val newItem = PeriodHistoryItem(clock.millis(), period)
        periodHistory.lastOrNull()?.calcDuration(newItem.timestamp)
        periodHistory.add(newItem)
        persist()
    }

    fun restore(): SummaryData {
        periodHistory = gson.fromJson(sharedPreferences.getString(KEY_PERIOD_HISTORY, "[]"), periodHistoryItemType)
        if (periodHistory.isEmpty()) return SummaryData.empty // nothing to restore

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

    private fun persist() {
        sharedPreferences.edit().apply {
            putString(KEY_PERIOD_HISTORY, gson.toJson(periodHistory))
        }.apply()
    }

    /**
     * Returns the duration of a period even if it's not finished yet
     */
    private fun PeriodHistoryItem.getOngoingDuration(): Int {
        return if (durationSeconds == 0) {
            TimeUnit.MILLISECONDS.toSeconds(clock.millis() - timestamp).toInt()
        } else {
            durationSeconds
        }
    }
}
