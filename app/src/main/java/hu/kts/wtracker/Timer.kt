package hu.kts.wtracker

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Timer @Inject constructor(
    private val coroutineScope: CoroutineScope
) {

    private val _tickFlow = MutableSharedFlow<Unit>(replay = 0)
    val tickFlow: SharedFlow<Unit> = _tickFlow

    private var task: TimerTask? = null
    private val timer = Timer()

    fun start() {
        task = object : TimerTask() {
            override fun run() {
                coroutineScope.launch {
                    _tickFlow.emit(Unit)
                }
            }
        }
        timer.schedule(task, 0, 1000)
    }

    fun stop() {
        task?.cancel()
    }
}
