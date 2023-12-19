package hu.kts.wtracker.data

data class SummaryViewState(
    val work: String,
    val chore: String,
    val rest: String,
    val workSegment: String,
    val choreSegment: String,
    val restSegment: String,
    val period: Period,
    val dialog: DialogType?,
    val skipNotificationTimeLeft: String?,
    val efficiency: Int,
) {
    enum class DialogType {
        Reset, SkipNotifications
    }

    companion object {
        val empty = SummaryViewState(
            work = "",
            chore = "",
            rest = "",
            workSegment = "",
            choreSegment = "",
            restSegment = "",
            period = Period.STOPPED,
            dialog = null,
            skipNotificationTimeLeft = null,
            efficiency = 0
        )
    }
}
