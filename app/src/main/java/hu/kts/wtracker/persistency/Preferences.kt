package hu.kts.wtracker.persistency

import android.content.SharedPreferences
import javax.inject.Inject

class Preferences @Inject constructor(
    private val preferences: SharedPreferences
) {

    var skipNotificationsUntil: Long
        get() = preferences.getLong(KEY_SKIP_NOTIFICATIONS_UNTIL, 0L)
        set(value) {
            preferences.edit().apply {
                putLong(KEY_SKIP_NOTIFICATIONS_UNTIL, value)
            }.apply()
        }

    companion object {
        const val KEY_SKIP_NOTIFICATIONS_UNTIL = "skipNotificationsUntil"
    }
}
