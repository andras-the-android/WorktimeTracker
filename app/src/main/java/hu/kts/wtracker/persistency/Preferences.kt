package hu.kts.wtracker.persistency

import android.content.SharedPreferences
import java.time.Clock
import javax.inject.Inject

class Preferences @Inject constructor(
    private val preferences: SharedPreferences,
    private val clock: Clock
) {

    var skipNotificationsUntil: Long
        get() = preferences.getLong(KEY_SKIP_NOTIFICATIONS_UNTIL, 0L)
        set(value) {
            preferences.edit().apply {
                putLong(KEY_SKIP_NOTIFICATIONS_UNTIL, value)
            }.apply()
        }

    fun getOrCreateActiveSessionTimestamp(): Long {
        var value = preferences.getLong(KEY_ACTIVE_SESSION_TIMESTAMP, 0L)
        if (value == 0L) {
            value = clock.millis()
            preferences.edit().apply {
                putLong(KEY_ACTIVE_SESSION_TIMESTAMP, value)
            }.apply()
        }
        return value
    }

    fun clearActiveSessionTimestamp() {
        preferences.edit().apply {
            putLong(KEY_ACTIVE_SESSION_TIMESTAMP, 0L)
        }.apply()
    }

    companion object {
        const val KEY_SKIP_NOTIFICATIONS_UNTIL = "skipNotificationsUntil"
        const val KEY_ACTIVE_SESSION_TIMESTAMP = "activeSessionTimestamp"
    }
}
