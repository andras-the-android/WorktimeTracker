package hu.kts.wtracker.data

enum class Period {
    STOPPED, WORK, CHORE, REST;

    fun isRunning() = this != STOPPED
}
