package hu.kts.wtracker.ui.main

import android.content.Context
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import hu.kts.wtracker.*
import hu.kts.wtracker.Timer
import java.util.*
import java.util.concurrent.TimeUnit

class MainViewModel : ViewModel() {

    private val _state = MutableLiveData<ViewState>()
    val state: LiveData<ViewState>
       get() = _state

    private val timer = Timer()
    private val context = WTrackerApp.instance.applicationContext
    private val preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
    private lateinit var textToSpeech: TextToSpeech

    private var workSec = 0
    private var restSec = 0
    private var workSegmentSec = 0
    private var restSegmentSec = 0
    private var period = Period.STOPPED
    private var lastPeriodSwitch = 0L
    private var notificationFrequency = NotificationFrequency.MIN1

    init {
        restoreState()
        updateViewState()
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.ERROR) {
                Toast.makeText(context, R.string.text_to_speech_error, Toast.LENGTH_SHORT).show()
            } else {
                textToSpeech.language = Locale.US
            }
        }
    }

    fun onStartButtonClicked() {
        if (period.isRunning()) {
            timer.stop()
            period = Period.STOPPED
        } else {
            timer.start { onTimerTick() }
            period = Period.WORK
        }
        persistState()
        updateViewState()
    }

    fun onStartButtonLongClicked(): Boolean {
        return if (!period.isRunning()) {
            workSec = 0
            restSec = 0
            workSegmentSec = 0
            restSegmentSec = 0
            persistState()
            updateViewState()
            true
        } else {
            false
        }
    }

    fun onNotificationFrequencyButtonClicked() {
        notificationFrequency = notificationFrequency.next()
        persistState()
        updateViewState()
    }

    fun onScreenTouch() {
        if (period.isRunning()) {
            if (period == Period.WORK) {
                period = Period.REST
                restSegmentSec = 0
            } else {
                period = Period.WORK
                workSegmentSec = 0
            }

            lastPeriodSwitch = System.currentTimeMillis()
            persistState()
            updateViewState()
        }
    }

    private fun onTimerTick() {
        if (period == Period.WORK) {
            ++workSec
            ++workSegmentSec
        } else {
            ++restSec
            ++restSegmentSec
        }
        updateViewState()
        handleNotification()
    }

    private fun updateViewState() {
        _state.postValue(ViewState(
            workSec.toTimeString(),
            restSec.toTimeString(),
            workSegmentSec.toTimeString(),
            restSegmentSec.toTimeString(),
            context.getString(if (period.isRunning()) R.string.stop else R.string.start),
            period,
            notificationFrequency
        ))
    }

    private fun persistState() {
        preferences.edit().apply {
            putInt(KEY_WORK_TIME, workSec)
            putInt(KEY_REST_TIME, restSec)
            putInt(KEY_WORK_SEGMENT_TIME, workSegmentSec)
            putInt(KEY_REST_SEGMENT_TIME, restSegmentSec)
            putLong(KEY_LAST_PERIOD_SWITCH, lastPeriodSwitch)
            putString(KEY_PERIOD, period.toString())
            putString(KEY_NOTIFICATION_FREQUENCY, notificationFrequency.toString())
        }.apply()
    }

    private fun restoreState() {
        period = Period.safeValueOf(preferences.getString(KEY_PERIOD, ""))
        workSec = preferences.getInt(KEY_WORK_TIME, 0)
        restSec = preferences.getInt(KEY_REST_TIME, 0)
        workSegmentSec = preferences.getInt(KEY_WORK_SEGMENT_TIME, 0)
        restSegmentSec = preferences.getInt(KEY_REST_SEGMENT_TIME, 0)
        lastPeriodSwitch = preferences.getLong(KEY_LAST_PERIOD_SWITCH, 0)
        if (period.isRunning()) {
            val elapsedSecs = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - lastPeriodSwitch).toInt()
            if (period == Period.WORK) {
                workSec += elapsedSecs
                workSegmentSec = elapsedSecs
            } else {
                restSec += elapsedSecs
                restSegmentSec = elapsedSecs
            }
            timer.start { onTimerTick() }
        }
        notificationFrequency = NotificationFrequency.safeValueOf(preferences.getString(KEY_NOTIFICATION_FREQUENCY, ""))
    }

    private fun handleNotification() {
        if (period == Period.REST && notificationFrequency != NotificationFrequency.MUTED && restSegmentSec.isWholeMinute()) {
            val minutes = TimeUnit.SECONDS.toMinutes(restSegmentSec.toLong()).toInt()
            if (minutes % notificationFrequency.frequency == 0) {
                textToSpeech.speak("$minutes minutes", TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
    }

    private fun Int.isWholeMinute() = this % 60 == 0

    data class ViewState(val work: String,
                         val rest: String,
                         val workSegment: String,
                         val restSegment: String,
                         val buttonText: String,
                         val period: Period,
                         val notificationFrequency: NotificationFrequency)

    enum class Period {
        STOPPED, WORK, REST;

        companion object {
            fun safeValueOf(value: String?): Period {
                return try {
                    valueOf(value ?: "")
                } catch (e: IllegalArgumentException) {
                    STOPPED
                }
            }
        }

        fun isRunning() = this != STOPPED
    }

    enum class NotificationFrequency(val frequency: Int, @StringRes val label: Int) {
        MIN1(1, R.string.sound_1min), MIN5(5, R.string.sound_5min), MUTED(0, R.string.sound_muted);

        companion object {
            fun safeValueOf(value: String?): NotificationFrequency {
                return try {
                    valueOf(value ?: "")
                } catch (e: IllegalArgumentException) {
                    MIN1
                }
            }
        }
    }
}