package hu.kts.wtracker.data

enum class Period {
    STOPPED, WORK, REST;

    fun isRunning() = this != STOPPED
}
