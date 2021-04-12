package com.example.prayertime.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.prayertime.notification.AlarmReceiver
import java.util.concurrent.Executors

/**
 * Re-schedules all stored alarms. This is necessary as [AlarmManager] does not persist alarms
 * between reboots.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            Executors.newSingleThreadExecutor().execute {
                AlarmReceiver.setAlarm(context)
            }
        }
    }
}