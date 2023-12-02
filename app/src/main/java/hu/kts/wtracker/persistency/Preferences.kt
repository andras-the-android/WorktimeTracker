package hu.kts.wtracker.persistency

import android.content.Context

class Preferences(
    context: Context
) {

    private val preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

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
