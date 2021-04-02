package com.example.prayertime.service

import android.app.*
import android.app.AlarmManager.AlarmClockInfo
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.util.SparseBooleanArray
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.prayertime.R
import com.example.prayertime.model.Times
import com.github.ppartisan.simplealarms.R
import com.github.ppartisan.simplealarms.model.Alarm
import com.github.ppartisan.simplealarms.ui.AlarmLandingPageActivity.launchIntent
import com.github.ppartisan.simplealarms.util.AlarmUtils
import java.util.*


class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val alarm: Times = intent.getBundleExtra(BUNDLE_EXTRA)!!.getParcelable(ALARM_KEY)
        if (alarm == null) {
            Log.e(TAG, "Alarm is null", NullPointerException())
            return
        }
        val id: Int = alarm.notificationId()
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(context)
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        builder.setSmallIcon(R.drawable.ic_baseline_notifications_24)
        builder.setColor(ContextCompat.getColor(context, R.color.accent))
        builder.setContentTitle(context.getString(R.string.app_name))
        builder.setContentText(alarm.)
        builder.setTicker(alarm.getLabel())
        builder.setVibrate(longArrayOf(1000, 500, 1000, 500, 1000, 500))
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        builder.setContentIntent(launchAlarmLandingPage(context, alarm))
        builder.setAutoCancel(true)
        builder.setPriority(Notification.PRIORITY_HIGH)
        manager.notify(id, builder.build())

        //Reset Alarm manually
        setReminderAlarm(context, alarm)
    }

    private class ScheduleAlarm private constructor(
        @field:NonNull @param:NonNull private val am: AlarmManager,
        @field:NonNull @param:NonNull private val ctx: Context
    ) {
        fun schedule(alarm: Alarm, pi: PendingIntent?) {
            if (VERSION.SDK_INT > VERSION_CODES.LOLLIPOP) {
                am.setAlarmClock(
                    AlarmClockInfo(
                        alarm.getTime(), launchAlarmLandingPage(
                            ctx, alarm
                        )
                    ), pi
                )
            } else if (VERSION.SDK_INT > VERSION_CODES.KITKAT) {
                am.setExact(AlarmManager.RTC_WAKEUP, alarm.getTime(), pi)
            } else {
                am[AlarmManager.RTC_WAKEUP, alarm.getTime()] = pi
            }
        }

        companion object {
            fun with(context: Context): ScheduleAlarm {
                val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    ?: throw IllegalStateException("AlarmManager is null")
                return ScheduleAlarm(am, context)
            }
        }
    }

    companion object {
        private val TAG = AlarmReceiver::class.java.simpleName
        private const val CHANNEL_ID = "alarm_channel"
        private const val BUNDLE_EXTRA = "bundle_extra"
        private const val ALARM_KEY = "alarm_key"

        //Convenience method for setting a notification
        fun setReminderAlarm(context: Context, alarm: Alarm) {

            //Check whether the alarm is set to run on any days
            if (!AlarmUtils.isAlarmActive(alarm)) {
                //If alarm not set to run on any days, cancel any existing notifications for this alarm
                cancelReminderAlarm(context, alarm)
                return
            }
            val nextAlarmTime = getTimeForNextAlarm(alarm)
            alarm.setTime(nextAlarmTime.timeInMillis)
            val intent = Intent(context, AlarmReceiver::class.java)
            val bundle = Bundle()
            bundle.putParcelable(ALARM_KEY, alarm)
            intent.putExtra(BUNDLE_EXTRA, bundle)
            val pIntent = PendingIntent.getBroadcast(
                context,
                alarm.notificationId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            PendingIntent.getBroadcast(
                context,
                100,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            ScheduleAlarm.with(context).schedule(alarm, pIntent)
        }

        fun setReminderAlarms(context: Context, alarms: List<Alarm>) {
            for (alarm in alarms) {
                setReminderAlarm(context, alarm)
            }
        }

        /**
         * Calculates the actual time of the next alarm/notification based on the user-set time the
         * alarm should sound each day, the days the alarm is set to run, and the current time.
         * @param alarm Alarm containing the daily time the alarm is set to run and days the alarm
         * should run
         * @return A Calendar with the actual time of the next alarm.
         */
        private fun getTimeForNextAlarm(alarm: Alarm): Calendar {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = alarm.getTime()
            val currentTime = System.currentTimeMillis()
            val startIndex = getStartIndexFromTime(calendar)
            var count = 0
            var isAlarmSetForDay: Boolean
            val daysArray: SparseBooleanArray = alarm.getDays()
            do {
                val index = (startIndex + count) % 7
                isAlarmSetForDay = daysArray.valueAt(index) && calendar.timeInMillis > currentTime
                if (!isAlarmSetForDay) {
                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                    count++
                }
            } while (!isAlarmSetForDay && count < 7)
            return calendar
        }

        fun cancelReminderAlarm(context: Context, alarm: Alarm) {
            val intent = Intent(context, AlarmReceiver::class.java)
            val pIntent = PendingIntent.getBroadcast(
                context,
                alarm.notificationId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            manager.cancel(pIntent)
        }

        private fun getStartIndexFromTime(c: Calendar): Int {
            val dayOfWeek = c[Calendar.DAY_OF_WEEK]
            var startIndex = 0
            when (dayOfWeek) {
                Calendar.MONDAY -> startIndex = 0
                Calendar.TUESDAY -> startIndex = 1
                Calendar.WEDNESDAY -> startIndex = 2
                Calendar.THURSDAY -> startIndex = 3
                Calendar.FRIDAY -> startIndex = 4
                Calendar.SATURDAY -> startIndex = 5
                Calendar.SUNDAY -> startIndex = 6
            }
            return startIndex
        }

        private fun createNotificationChannel(ctx: Context) {
            if (VERSION.SDK_INT < VERSION_CODES.O) return
            val mgr = ctx.getSystemService(
                NotificationManager::class.java
            )
                ?: return
            val name = ctx.getString(R.string.channel_name)
            if (mgr.getNotificationChannel(name) == null) {
                val channel =
                    NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH)
                channel.enableVibration(true)
                channel.vibrationPattern = longArrayOf(1000, 500, 1000, 500, 1000, 500)
                channel.setBypassDnd(true)
                mgr.createNotificationChannel(channel)
            }
        }

        private fun launchAlarmLandingPage(ctx: Context, alarm: Alarm): PendingIntent {
            return PendingIntent.getActivity(
                ctx, alarm.notificationId(), launchIntent(ctx), PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }
}