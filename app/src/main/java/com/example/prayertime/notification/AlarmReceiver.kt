package com.example.prayertime.notification

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.media.RingtoneManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.prayertime.R
import com.example.prayertime.constants.DEFAULT_LOCATION
import com.example.prayertime.constants.LATITUDE
import com.example.prayertime.constants.LONGITUDE
import com.example.prayertime.constants.MY_PREFS
import com.example.prayertime.helper.TimeHelper
import com.example.prayertime.ui.mainActivity.MainActivity


const val CHANNEL_ID = "alarm_channel"
const val CHANNEL_NAME = "prayer_time"

const val BUNDLE_EXTRA = "bundle_extra"
const val ALARM_KEY = "alarm_key"

const val NOTIFICATION_ID = 1000

class AlarmReceiver() : BroadcastReceiver() {

    private val TAG = AlarmReceiver::class.java.simpleName

    override fun onReceive(context: Context?, intent: Intent?) {

//        val alarm = intent?.getBundleExtra(BUNDLE_EXTRA)?.getString(ALARM_KEY)
//        if (alarm == null) {
//            Log.e(TAG, "onReceive: ", NullPointerException())
//            return
//        }

        val manager =
            context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel(context)

        val builder =
            NotificationCompat.Builder(context, CHANNEL_ID)
        builder.setSmallIcon(R.drawable.ic_launcher_foreground)
        builder.color = ContextCompat.getColor(context, R.color.white)
        builder.setContentTitle(context.getString(R.string.app_name))
        builder.setContentText("Sample text")
        builder.setTicker("Prayer time")
        builder.setVibrate(longArrayOf(1000, 500, 1000, 500, 1000, 500))
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        builder.setContentIntent(launchAlarmLandingPage(context))
        builder.setAutoCancel(true)
        builder.priority = Notification.PRIORITY_HIGH

        manager.notify(NOTIFICATION_ID, builder.build())

        //Reset Alarm manually
        setRemainderAlarm(context)

    }

    private fun createNotificationChannel(context: Context) {

        if (VERSION.SDK_INT < VERSION_CODES.O) return

        val mgr: NotificationManager = context.getSystemService(
            NotificationManager::class.java
        ) ?: return

        if (mgr.getNotificationChannel(CHANNEL_NAME) == null) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(1000, 500, 1000, 500, 1000, 500)
            channel.setBypassDnd(true)
            mgr.createNotificationChannel(channel)
        }

    }

    private fun launchAlarmLandingPage(ctx: Context): PendingIntent? {
        return PendingIntent.getActivity(
            ctx, NOTIFICATION_ID, launchIntent(ctx), PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun launchIntent(context: Context?): Intent {
        val i = Intent(
            context,
            MainActivity::class.java
        )
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        return i
    }

    companion object {

        fun setAlarm(context: Context) {
            cancelReminderAlarm(context)
            setRemainderAlarm(context)
        }

        private fun setRemainderAlarm(context: Context) {
            //do some code
            val time = TimeHelper(getLocFromPrefs(context)).getAlarmTime()
            val intent = Intent(context, AlarmReceiver::class.java)
            val bundle = Bundle()
            bundle.putLong(ALARM_KEY, time)
            intent.putExtra(BUNDLE_EXTRA, bundle)

            val pIntent = PendingIntent.getBroadcast(
                context,
                NOTIFICATION_ID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            ScheduleAlarm.with(context).schedule(time, pIntent)
        }

        private fun getLocFromPrefs(context: Context): Location {
            val prefs = context.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE)
            val lat = prefs.getString(LATITUDE, "")?.toDouble()
            val long = prefs.getString(LONGITUDE, "")?.toDouble()

            Log.d("AlarmReceiver", "getLocFromPrefs: lat: $lat long: $long")

            if (lat != null && long != null) {
                val location = Location("")
                location.latitude = lat
                location.longitude = long
                return location
            }

            return DEFAULT_LOCATION

        }

        private fun cancelReminderAlarm(context: Context) {
            val intent = Intent(context, AlarmReceiver::class.java)
            val pIntent = PendingIntent.getBroadcast(
                context,
                NOTIFICATION_ID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            manager.cancel(pIntent)
        }

    }

    private class ScheduleAlarm private constructor(
        private val am: AlarmManager
    ) {
        fun schedule(time: Long, pi: PendingIntent?) {
            am.setExact(AlarmManager.RTC_WAKEUP, time, pi)
        }

        companion object {
            fun with(context: Context): ScheduleAlarm {
                val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?
                    ?: throw IllegalStateException("alarm manager not found!")
                return ScheduleAlarm(am)
            }
        }
    }


}

