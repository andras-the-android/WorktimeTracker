package hu.kts.wtracker.ui.main

import android.content.Context
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import hu.kts.wtracker.KEY_NOTIFICATION_FREQUENCY
import hu.kts.wtracker.KEY_PERIOD
import hu.kts.wtracker.KEY_PERIOD_HISTORY
import hu.kts.wtracker.KEY_REST_SEGMENT_TIME
import hu.kts.wtracker.KEY_REST_TIME
import hu.kts.wtracker.KEY_WORK_SEGMENT_TIME
import hu.kts.wtracker.KEY_WORK_TIME
import hu.kts.wtracker.R
import hu.kts.wtracker.Timer
import hu.kts.wtracker.WTrackerApp
import hu.kts.wtracker.next
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.LinkedList
import java.util.Locale
import java.util.concurrent.TimeUnit

class MainViewModel : ViewModel() {

    private val _state = MutableLiveData<ViewState>()
    val state: LiveData<ViewState>
       get() = _state

    private val _historyState = MutableLiveData(LinkedList<PeriodHistoryViewItem>())
    val historyState: LiveData<LinkedList<PeriodHistoryViewItem>>
        get() = _historyState

    private val timer = Timer()
    private val context = WTrackerApp.instance.applicationContext
    private val preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
    private lateinit var textToSpeech: TextToSpeech
    private val gson = Gson()
    private val periodHistoryItemType = object : TypeToken<ArrayList<PeriodHistoryItem>>() {}.type

    private var workSec = 0
    private var restSec = 0
    private var workSegmentSec = 0
    private var restSegmentSec = 0
    private var period = Period.STOPPED
    private var notificationFrequency = NotificationFrequency.MIN1
    private var periodHistory = ArrayList<PeriodHistoryItem>()

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

    fun onStopResetButtonClicked() {
        if (period.isRunning()) {
            timer.stop()
            period = Period.STOPPED
            addToHistory(period)
            persistState()
            updateViewState()
        } else {
            reset()
        }
    }

    fun reset(): Boolean {
        return if (!period.isRunning()) {
            workSec = 0
            restSec = 0
            workSegmentSec = 0
            restSegmentSec = 0
            periodHistory = arrayListOf()
            _historyState.value = LinkedList<PeriodHistoryViewItem>()
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

    fun onWorkSegmentClick() {
        onTimeSegmentClick(Period.WORK)
    }

    fun onRestSegmentClick() {
        onTimeSegmentClick(Period.REST)
    }

    private fun onTimeSegmentClick(initialPeriod: Period) {
        // if it's already running, switch to the other one regardless which timer was clicked
        if (period.isRunning()) {
            if (period == Period.WORK) {
                period = Period.REST
                restSegmentSec = 0
            } else {
                period = Period.WORK
                workSegmentSec = 0
            }
        // set the initial value if it's not running
        } else {
            timer.start { onTimerTick() }
            period = initialPeriod
        }

        addToHistory(period)
        persistState()
        updateViewState()
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
            context.getString(if (period.isRunning()) R.string.stop else R.string.reset),
            period,
            notificationFrequency
        ))
    }

    private fun addToHistory(period: Period) {
        val newItem = PeriodHistoryItem(System.currentTimeMillis(), period)
        //This may seems weird at first but we always show the finished items in the history view.
        //So the history view will always contain periodHistory.size - 1 elements
        if (periodHistory.isNotEmpty()) {
            _historyState.value = _historyState.value?.apply { add(periodHistory.last().toViewItem(newItem.timestamp)) }
        }
        periodHistory.add(newItem)
    }

    private fun persistState() {
        preferences.edit().apply {
            putInt(KEY_WORK_TIME, workSec)
            putInt(KEY_REST_TIME, restSec)
            putInt(KEY_WORK_SEGMENT_TIME, workSegmentSec)
            putInt(KEY_REST_SEGMENT_TIME, restSegmentSec)
            putString(KEY_PERIOD, period.toString())
            putString(KEY_NOTIFICATION_FREQUENCY, notificationFrequency.toString())
            putString(KEY_PERIOD_HISTORY, gson.toJson(periodHistory))
        }.apply()
    }

    private fun restoreState() {
        period = Period.safeValueOf(preferences.getString(KEY_PERIOD, ""))
        workSec = preferences.getInt(KEY_WORK_TIME, 0)
        restSec = preferences.getInt(KEY_REST_TIME, 0)
        workSegmentSec = preferences.getInt(KEY_WORK_SEGMENT_TIME, 0)
        restSegmentSec = preferences.getInt(KEY_REST_SEGMENT_TIME, 0)
        periodHistory = gson.fromJson(preferences.getString(KEY_PERIOD_HISTORY, "[]"), periodHistoryItemType)
        generateHistoryView()
        if (period.isRunning()) {
            //at this point there must be at least one item in the list
            val lastPeriodSwitch = periodHistory.last().timestamp
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

    private fun generateHistoryView() {
        _historyState.value = LinkedList<PeriodHistoryViewItem>().apply {
            for (i in 0 until periodHistory.size - 1) {
                add(periodHistory[i].toViewItem(periodHistory[i + 1].timestamp))
            }
        }
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
                         val stopResetText: String,
                         val period: Period,
                         val notificationFrequency: NotificationFrequency)

    data class PeriodHistoryItem(
        val timestamp: Long,
        val period: Period
    ) {

        fun toViewItem(nextPeriodStart: Long): PeriodHistoryViewItem {
            return PeriodHistoryViewItem(
                format.format(Date(timestamp)),
                period.color,
                TimeUnit.MILLISECONDS.toMinutes(nextPeriodStart - timestamp).toInt()
            )
        }

        companion object {
            private val format = SimpleDateFormat.getTimeInstance(DateFormat.SHORT)
        }
    }

    data class PeriodHistoryViewItem(
        val timestamp: String,
        @ColorRes val color: Int,
        val duration: Int
    )

    enum class Period(@ColorRes val color: Int) {
        STOPPED(R.color.bg_default), WORK(R.color.bg_work), REST(R.color.bg_rest);

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
