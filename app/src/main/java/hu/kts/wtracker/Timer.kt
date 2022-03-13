package hu.kts.wtracker

import java.util.*
import java.util.Timer

class Timer {

    private var task: TimerTask? = null
    private val timer = Timer()

    fun start(callback: () -> Any) {
        task = object : TimerTask() {
            override fun run() {
                callback()
            }
        }
        timer.schedule(task, 0, 1000)
    }

    fun stop() {
        task?.cancel()
    }
}