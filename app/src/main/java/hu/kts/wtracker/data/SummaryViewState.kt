package hu.kts.wtracker.data

data class SummaryViewState(
    val work: String,
    val rest: String,
    val workSegment: String,
    val restSegment: String,
    val stopResetText: String,
    val period: Period,
    val dialog: DialogType? = null,
    val skipNotificationTimeLeft: String?,
    val efficiency: Int,
) {
    enum class DialogType {
        Reset, SkipNotifications
    }
}
