package com.dicoding.habitapp.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.dicoding.habitapp.R
import com.dicoding.habitapp.ui.countdown.CountDownActivity
import com.dicoding.habitapp.ui.detail.DetailHabitActivity
import com.dicoding.habitapp.ui.list.HabitListActivity
import com.dicoding.habitapp.utils.HABIT_ID
import com.dicoding.habitapp.utils.HABIT_TITLE
import com.dicoding.habitapp.utils.NOTIFICATION_CHANNEL_ID
import com.dicoding.habitapp.utils.NOTIF_UNIQUE_WORK

class NotificationWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    private val habitId = inputData.getInt(HABIT_ID, 0)
    private val habitTitle = inputData.getString(HABIT_TITLE)

    private fun pendingIntent(id: Int): PendingIntent? {
        val intent = Intent(applicationContext, DetailHabitActivity::class.java).apply {
            putExtra(HABIT_ID, id)
        }
        return TaskStackBuilder.create(applicationContext).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    override fun doWork(): Result {
        val prefManager = androidx.preference.PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val shouldNotify = prefManager.getBoolean(applicationContext.getString(R.string.pref_key_notify), false)

        //TODO 12 : If notification preference on, show notification with pending intent
        if (shouldNotify) {
            val message = applicationContext.getString(R.string.notify_content)
            val title = habitTitle
            val intent = Intent(applicationContext, HabitListActivity::class.java)
            val pendingIntent : PendingIntent? = pendingIntent(habitId)
            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val taskStack : android.app.TaskStackBuilder = android.app.TaskStackBuilder.create(applicationContext)
            taskStack.addParentStack(CountDownActivity::class.java)
            taskStack.addNextIntent(intent)

            val notificationBuilder =  NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
            notificationBuilder.apply {
                setContentTitle(title)
                setContentText(message)
                setSmallIcon(R.drawable.ic_notifications)
                setContentIntent(pendingIntent)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                val channel = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    NOTIF_UNIQUE_WORK,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationBuilder.setChannelId(NOTIFICATION_CHANNEL_ID)
                notificationManager.createNotificationChannel(channel)
            }

            val notification = notificationBuilder.build()
            notification.apply {
                flags = Notification.FLAG_AUTO_CANCEL or Notification.FLAG_ONGOING_EVENT
            }
            notificationManager.notify(habitId, notification)
        }
        return Result.success()
    }
}
