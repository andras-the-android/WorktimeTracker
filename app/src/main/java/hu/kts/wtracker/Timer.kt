package hu.kts.wtracker

import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject

class Timer @Inject constructor() {

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
