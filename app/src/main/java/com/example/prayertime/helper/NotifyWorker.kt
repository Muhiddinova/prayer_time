package com.example.prayertime.helper

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.prayertime.R

class NotifyWorker(private val context: Context, params: WorkerParameters) : Worker(context, params){


    @SuppressLint("InlinedApi")
    override fun doWork(): Result {

        return Result.success()
    }

   }