package hu.kts.wtracker.ui.main

import android.content.Context
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import hu.kts.wtracker.KEY_PERIOD_HISTORY
import hu.kts.wtracker.R
import hu.kts.wtracker.Timer
import hu.kts.wtracker.WTrackerApp
import hu.kts.wtracker.data.Period
import hu.kts.wtracker.data.PeriodHistoryItem
import hu.kts.wtracker.data.SummaryViewState
import hu.kts.wtracker.persistency.Preferences
import java.time.Clock
import java.time.Duration
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class MainViewModel : ViewModel() {

    private val _state = MutableLiveData<SummaryViewState>()
    val state: LiveData<SummaryViewState>
       get() = _state

    private val timer = Timer()
    private val context = WTrackerApp.instance.applicationContext
    private val sharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
    private val preferences = Preferences(context)
    private lateinit var textToSpeech: TextToSpeech
    private val gson = Gson()
    private val periodHistoryItemType = object : TypeToken<ArrayList<PeriodHistoryItem>>() {}.type
    private val clock = Clock.systemDefaultZone()

    private var workSec = 0
    private var restSec = 0
    private var workSegmentSec = 0
    private var restSegmentSec = 0
    private var period = Period.STOPPED
    private var periodHistory = ArrayList<PeriodHistoryItem>()
    private var dialog: SummaryViewState.DialogType? = null
    private var skipNotificationsUntil = 0L

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
            resetSkipNotifications()
        } else {
            dialog = SummaryViewState.DialogType.Reset
        }
        updateViewState()
    }

    fun confirmReset() {
        assert(!period.isRunning())
        workSec = 0
        restSec = 0
        workSegmentSec = 0
        restSegmentSec = 0
        periodHistory = arrayListOf()
        resetSkipNotifications()
        persistState()
        updateViewState()
        cancelDialog()
    }

    fun onSkipNotificationsButtonClick() {
        dialog = SummaryViewState.DialogType.SkipNotifications
        updateViewState()
    }

    fun skipNotificationFor(minutes: Int) {
        skipNotificationsUntil = Clock.offset(clock, Duration.ofMinutes(minutes.toLong())).millis()
        preferences.skipNotificationsUntil = skipNotificationsUntil
        cancelDialog()
    }

    private fun resetSkipNotifications() {
        skipNotificationsUntil = 0
        preferences.skipNotificationsUntil = skipNotificationsUntil
    }

    fun cancelDialog() {
        dialog = null
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
                resetSkipNotifications()
            }
        // set the initial value if it's not running
        } else {
            timer.start { onTimerTick() }
            period = initialPeriod
        }

        addToHistory(period)
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
        _state.postValue(
            SummaryViewState(
                workSec.hMmSsFormat(),
                restSec.hMmSsFormat(),
                workSegmentSec.hMmSsFormat(),
                restSegmentSec.hMmSsFormat(),
                context.getString(if (period.isRunning()) R.string.stop else R.string.reset),
                period,
                dialog,
                calcSkipNotificationsButtonText(),
                calcEfficiency(),
            )
        )
    }

    private fun calcSkipNotificationsButtonText(): String? {
        if (skipNotificationsUntil <= clock.millis()) return null
        return (skipNotificationsUntil - clock.millis()).mmSsFormat()
    }

    private fun calcEfficiency(): Int {
        if (workSec == 0) return 0 // avoid divide by zero
        return (workSec.toFloat() / (workSec + restSec) * 100).roundToInt()
    }

    private fun addToHistory(period: Period) {
        // TODO implement history
    }

    private fun persistState() {
        sharedPreferences.edit().apply {
            putString(KEY_PERIOD_HISTORY, gson.toJson(periodHistory))
        }.apply()
    }

    private fun restoreState() {
        periodHistory = gson.fromJson(sharedPreferences.getString(KEY_PERIOD_HISTORY, "[]"), periodHistoryItemType)
        if (periodHistory.isEmpty()) return // nothing to restore

        period = periodHistory.last().period

        periodHistory.forEachIndexed { index, item ->
            if (index < periodHistory.size - 1) {
                item.calcDuration(periodHistory[index + 1].timestamp)
            }
            when (item.period) {
                Period.WORK -> {
                    workSegmentSec = item.getOngoingDuration()
                    workSec += workSegmentSec
                }
                Period.REST -> {
                    restSegmentSec = item.getOngoingDuration()
                    restSec += restSegmentSec
                }
                else -> {}
            }
        }

        if (period.isRunning()) {
            timer.start { onTimerTick() }
        }

        skipNotificationsUntil = preferences.skipNotificationsUntil
    }

    /**
     * Returns the duration of a period even if it's not finished yet
     */
    private fun PeriodHistoryItem.getOngoingDuration(): Int {
        return if (durationSeconds == 0) {
            TimeUnit.MILLISECONDS.toSeconds(clock.millis() - timestamp).toInt()
        } else {
            durationSeconds
        }
    }

    private fun handleNotification() {
        if (period == Period.REST && restSegmentSec.isWholeMinute()) {
            if (clock.millis() > skipNotificationsUntil) {
                val minutes = TimeUnit.SECONDS.toMinutes(restSegmentSec.toLong()).toInt()
                textToSpeech.speak("$minutes minutes", TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
    }

    private fun Int.isWholeMinute() = this % 60 == 0

}
