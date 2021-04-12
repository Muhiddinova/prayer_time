package com.example.prayertime.ui.mainActivity

import android.location.Location
import androidx.lifecycle.ViewModel
import com.example.prayertime.database.TimesByYearDao
import com.example.prayertime.helper.TimeHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.*

class MainActivityViewModel(private val dataSource: TimesByYearDao) :
    ViewModel() {

    private lateinit var timeHelper: TimeHelper
    private lateinit var gmt: TimeZone

    fun timeInitializer(location: Location) {
        gmt = TimeZone.getDefault()
        timeHelper = TimeHelper(location)
        runBlocking {
            withContext(Dispatchers.IO) {
                timeHelper.prayerTime()
            }
        }

    }


}