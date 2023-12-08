package hu.kts.wtracker.ui.main

import android.speech.tts.TextToSpeech
import hu.kts.wtracker.data.Period
import hu.kts.wtracker.data.SummaryData
import hu.kts.wtracker.framework.SystemNotifications
import hu.kts.wtracker.persistency.Preferences
import java.time.Clock
import java.time.Duration
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class Notifications @Inject constructor(
    private val preferences: Preferences,
    private val textToSpeech: TextToSpeech,
    private val clock: Clock,
    private val systemNotifications: SystemNotifications
) {

    private var skipNotificationsUntil = preferences.skipNotificationsUntil

    fun skipFor(minutes: Int) {
        skipNotificationsUntil = Clock.offset(clock, Duration.ofMinutes(minutes.toLong())).millis()
        preferences.skipNotificationsUntil = skipNotificationsUntil
    }

    fun resetSkip() {
        skipNotificationsUntil = 0
        preferences.skipNotificationsUntil = skipNotificationsUntil
    }

    fun calcSkipDisplayText(): String? {
        if (skipNotificationsUntil <= clock.millis()) return null
        return (skipNotificationsUntil - clock.millis()).mmSsFormat()
    }

    fun trigger(summaryData: SummaryData) {
        summaryData.run {
            if (period == Period.REST && restSegmentSec.isWholeMinute()) {
                if (clock.millis() > skipNotificationsUntil) {
                    val minutes = TimeUnit.SECONDS.toMinutes(restSegmentSec.toLong()).toInt()
                    systemNotifications.showMinutelyNotification(minutes)
                    textToSpeech.speak("$minutes minutes", TextToSpeech.QUEUE_FLUSH, null, null)
                }
            }
        }
    }

    private fun Int.isWholeMinute() = this % 60 == 0

}
