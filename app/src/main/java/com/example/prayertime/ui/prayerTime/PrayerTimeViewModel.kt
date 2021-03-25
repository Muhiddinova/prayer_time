package com.example.prayertime.ui.prayerTime

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.prayertime.database.TimesByYearDao
import com.example.prayertime.model.Times
import com.example.prayertime.repository.TimesRepository

class PrayerTimeViewModel(dataSource: TimesByYearDao) : ViewModel() {

    private val timesRepository = TimesRepository(dataSource)

    fun getDate(date: String): Times {
        return timesRepository.getTime(date)
    }

    fun getFirstElement(): Times{
        return timesRepository.getFirstElement()
    }
}