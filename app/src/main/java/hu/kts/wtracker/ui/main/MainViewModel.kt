package hu.kts.wtracker.ui.main

import android.annotation.SuppressLint
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
import hu.kts.wtracker.KEY_PERIOD_HISTORY
import hu.kts.wtracker.KEY_SKIP_NOTIFICATIONS_UNTIL
import hu.kts.wtracker.R
import hu.kts.wtracker.Timer
import hu.kts.wtracker.WTrackerApp
import hu.kts.wtracker.next
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Clock
import java.time.Duration
import java.util.Date
import java.util.LinkedList
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

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
    private val clock = Clock.systemDefaultZone()

    private var workSec = 0
    private var restSec = 0
    private var workSegmentSec = 0
    private var restSegmentSec = 0
    private var period = Period.STOPPED
    private var notificationFrequency = NotificationFrequency.MIN1
    private var periodHistory = ArrayList<PeriodHistoryItem>()
    private var dialog: DialogType? = null
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
            dialog = DialogType.Reset
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
        _historyState.value = LinkedList<PeriodHistoryViewItem>()
        resetSkipNotifications()
        persistState()
        updateViewState()
        cancelDialog()
    }

    fun onSkipNotificationsButtonClick() {
        dialog = DialogType.SkipNotifications
        updateViewState()
    }

    fun skipNotificationFor(minutes: Int) {
        skipNotificationsUntil = Clock.offset(clock, Duration.ofMinutes(minutes.toLong())).millis()
        preferences.edit().apply {
            putLong(KEY_SKIP_NOTIFICATIONS_UNTIL, skipNotificationsUntil)
        }.apply()
        cancelDialog()
    }

    private fun resetSkipNotifications() {
        skipNotificationsUntil = 0
        preferences.edit().apply {
            putLong(KEY_SKIP_NOTIFICATIONS_UNTIL, skipNotificationsUntil)
        }.apply()
    }

    fun cancelDialog() {
        dialog = null
        updateViewState()
    }

    fun onNotificationFrequencyButtonClicked() {
        notificationFrequency = notificationFrequency.next()
        preferences.edit().apply {
            putString(KEY_NOTIFICATION_FREQUENCY, notificationFrequency.toString())
        }.apply()
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
        _state.postValue(ViewState(
            workSec.toTimeString(),
            restSec.toTimeString(),
            workSegmentSec.toTimeString(),
            restSegmentSec.toTimeString(),
            context.getString(if (period.isRunning()) R.string.stop else R.string.reset),
            period,
            notificationFrequency,
            dialog,
            calcSkipNotificationsButtonText(),
            calcEfficiency(),
        ))
    }

    private fun calcSkipNotificationsButtonText(): String? {
        if (skipNotificationsUntil <= clock.millis()) return null
        return mmHhFormat.format(Date(skipNotificationsUntil - clock.millis()))
    }

    private fun calcEfficiency(): Int {
        if (workSec == 0) return 0 // avoid divide by zero
        return (workSec.toFloat() / (workSec + restSec) * 100).roundToInt()
    }

    private fun addToHistory(period: Period) {
        val newItem = PeriodHistoryItem(clock.millis(), period)
        //This may seems weird at first but we always show the finished items in the history view.
        //So the history view will always contain periodHistory.size - 1 elements
        if (periodHistory.isNotEmpty()) {
            _historyState.value = _historyState.value?.apply { add(periodHistory.last().toViewItem(newItem.timestamp)) }
        }
        periodHistory.lastOrNull()?.calcDuration(newItem.timestamp)
        periodHistory.add(newItem)
        persistState()
    }

    private fun persistState() {
        preferences.edit().apply {
            putString(KEY_PERIOD_HISTORY, gson.toJson(periodHistory))
        }.apply()
    }

    private fun restoreState() {
        periodHistory = gson.fromJson(preferences.getString(KEY_PERIOD_HISTORY, "[]"), periodHistoryItemType)
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

        generateHistoryView()
        notificationFrequency = NotificationFrequency.safeValueOf(preferences.getString(KEY_NOTIFICATION_FREQUENCY, ""))
        skipNotificationsUntil = preferences.getLong(KEY_SKIP_NOTIFICATIONS_UNTIL, 0L)
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
            if (minutes % notificationFrequency.frequency == 0 && clock.millis() > skipNotificationsUntil) {
                textToSpeech.speak("$minutes minutes", TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
    }

    private fun Int.isWholeMinute() = this % 60 == 0

    companion object {
        @SuppressLint("SimpleDateFormat")
        private val mmHhFormat = SimpleDateFormat("mm:ss")
    }

    data class ViewState(
        val work: String,
        val rest: String,
        val workSegment: String,
        val restSegment: String,
        val stopResetText: String,
        val period: Period,
        val notificationFrequency: NotificationFrequency,
        val dialog: DialogType? = null,
        val skipNotificationTimeLeft: String?,
        val efficiency: Int,
    )

    data class PeriodHistoryItem(
        val timestamp: Long,
        val period: Period
    ) {

        var durationSeconds = 0
        private set

        fun toViewItem(nextPeriodStart: Long): PeriodHistoryViewItem {
            return PeriodHistoryViewItem(
                format.format(Date(timestamp)),
                period.color,
                TimeUnit.MILLISECONDS.toMinutes(nextPeriodStart - timestamp).toInt()
            )
        }

        fun calcDuration(nextPeriodStart: Long) {
            durationSeconds = TimeUnit.MILLISECONDS.toSeconds(nextPeriodStart - timestamp).toInt()
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

    enum class DialogType {
        Reset, SkipNotifications
    }
}
