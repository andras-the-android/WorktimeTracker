package hu.kts.wtracker.ui.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import hu.kts.wtracker.*
import java.util.concurrent.TimeUnit

class MainViewModel : ViewModel() {

    private val _state = MutableLiveData<ViewState>()
    val state: LiveData<ViewState>
       get() = _state

    private val timer = Timer()
    private val context = WTrackerApp.instance.applicationContext
    private val preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    private var workSec = 0
    private var restSec = 0
    private var period = Period.WORK
    private var isRunning = false

    init {
        restoreState()
        updateViewState()
    }

    fun onButtonClicked() {
        if (isRunning) {
            timer.stop()
        } else {
            timer.start { onTimerTick() }
        }
        isRunning = !isRunning
        persistState()
        updateViewState()
    }

    fun onButtonLongClicked(): Boolean {
        return if (!isRunning) {
            workSec = 0
            restSec = 0
            period = Period.WORK
            persistState()
            updateViewState()
            true
        } else {
            false
        }
    }

    fun onScreenTouch() {
        period = if (period == Period.WORK) Period.REST else Period.WORK
        persistState()
        updateViewState()
    }

    private fun onTimerTick() {
        if (period == Period.WORK) ++workSec else ++restSec
        updateViewState()
    }

    private fun updateViewState() {
        _state.postValue(ViewState(
            workSec.toTimeString(),
            restSec.toTimeString(),
            context.getString(if (isRunning) R.string.stop else R.string.start),
            period,
            isRunning
        ))
    }

    private fun persistState() {
        preferences.edit().apply {
            putInt(KEY_WORK_TIME, workSec)
            putInt(KEY_REST_TIME, restSec)
            putLong(KEY_LAST_PERIOD_SWITCH, System.currentTimeMillis())
            putBoolean(KEY_IS_RUNNING, isRunning)
            putString(KEY_PERIOD, period.toString())
        }.apply()
    }

    private fun restoreState() {
        isRunning = preferences.getBoolean(KEY_IS_RUNNING, false)
        period = Period.safeValueOf(preferences.getString(KEY_PERIOD, ""))
        workSec = preferences.getInt(KEY_WORK_TIME, 0)
        restSec = preferences.getInt(KEY_REST_TIME, 0)
        if (isRunning) {
            val lastPeriodSwitch = preferences.getLong(KEY_LAST_PERIOD_SWITCH, 0)
            val elapsedSecs = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - lastPeriodSwitch).toInt()
            if (period == Period.WORK) {
                workSec += elapsedSecs
            } else {
                restSec += elapsedSecs
            }
            timer.start { onTimerTick() }
        }

    }

    data class ViewState(val work: String, val rest: String, val buttonText: String, val period: Period, val isRunning: Boolean)

    enum class Period {
        WORK, REST;

        companion object {
            fun safeValueOf(value: String?): Period {
                return try {
                    valueOf(value ?: "")
                } catch (e: IllegalArgumentException) {
                    WORK
                }
            }
        }
    }
}