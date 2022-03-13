package hu.kts.wtracker.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import hu.kts.wtracker.R
import hu.kts.wtracker.Timer
import hu.kts.wtracker.WTrackerApp

class MainViewModel : ViewModel() {

    private val _state = MutableLiveData<ViewState>()
    val state: LiveData<ViewState>
       get() = _state

    private val timer = Timer()
    private val context = WTrackerApp.instance.applicationContext

    private var workSec = 0
    private var restSec = 0
    private var period = Period.WORK
    private var isRunning = false

    init {
        updateViewState()
    }

    fun onButtonClicked() {
        if (isRunning) {
            timer.stop()
        } else {
            timer.start { onTimerTick() }
        }
        isRunning = !isRunning
        updateViewState()
    }

    fun onButtonLongClicked(): Boolean {
        return if (!isRunning) {
            workSec = 0
            restSec = 0
            updateViewState()
            true
        } else {
            false
        }
    }

    fun onScreenTouch() {
        period = if (period == Period.WORK) Period.REST else Period.WORK
        updateViewState()
    }

    private fun onTimerTick() {
        if (period == Period.WORK) ++workSec else ++restSec
        updateViewState()
    }

    private fun updateViewState() {
        _state.postValue(ViewState(
            workSec.toString(),
            restSec.toString(),
            context.getString(if (isRunning) R.string.stop else R.string.start),
            period,
            isRunning
        ))
    }

    data class ViewState(val work: String, val rest: String, val buttonText: String, val period: Period, val isRunning: Boolean)

    enum class Period {
        WORK, REST
    }
}