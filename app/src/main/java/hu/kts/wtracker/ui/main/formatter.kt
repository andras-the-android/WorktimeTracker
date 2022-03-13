package hu.kts.wtracker.ui.main

private const val SECS_IN_HOUR = 3600
private const val SECS_IN_MINUTE = 60

fun Int.toTimeString(): String {
    val hours = this / SECS_IN_HOUR
    val timeWithoutHours = this % SECS_IN_HOUR
    val minutes = timeWithoutHours / SECS_IN_MINUTE
    val seconds = timeWithoutHours % SECS_IN_MINUTE
    return "$hours:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
}