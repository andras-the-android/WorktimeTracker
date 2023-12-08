package hu.kts.wtracker.framework

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import hu.kts.wtracker.R
import javax.inject.Inject

class SystemNotifications @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val notificationManager = NotificationManagerCompat.from(context)

    init {
        val name = context.getString(R.string.minutely_notification_channel)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }

    fun showMinutelyNotification(minutes: Int) {
        if (!checkPermission()) return
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_adaptive_fore)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(context.getString(R.string.minutely_notification_title, minutes))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(VISIBILITY_PUBLIC)

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    private fun checkPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        val result = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
        return result == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        const val CHANNEL_ID = "minutelyNotifications"
        const val NOTIFICATION_ID = 0


    }
}