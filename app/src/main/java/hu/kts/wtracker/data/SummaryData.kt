package hu.kts.wtracker.data

import kotlin.math.roundToInt

data class SummaryData(
    val workSec: Int,
    val choreSec: Int,
    val restSec: Int,
    val workSegmentSec: Int,
    val choreSegmentSec: Int,
    val restSegmentSec: Int,
    val period: Period,
) {
    fun calcEfficiency(): Int {
        if (workSec + choreSec == 0) return 0 // avoid divide by zero
        return ((workSec.toFloat() + choreSec) / (workSec + choreSec + restSec) * 100).roundToInt()
    }

    companion object {
        val empty = SummaryData(0, 0, 0, 0, 0, 0, Period.STOPPED)
    }
}
