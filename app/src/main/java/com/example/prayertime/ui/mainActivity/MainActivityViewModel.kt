package com.example.prayertime.ui.mainActivity

import android.util.Log
import androidx.lifecycle.ViewModel
import com.azan.astrologicalCalc.Location
import com.example.prayertime.database.TimesByYearDao
import com.example.prayertime.helper.TimeHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.*

class MainActivityViewModel(private val dataSource: TimesByYearDao) :
    ViewModel() {

    private val TAG = "MainActivityViewModel"
    private lateinit var timeHelper: TimeHelper
    private lateinit var astroLocation: Location
    private lateinit var gmt: TimeZone

    fun timeInitializer(location: android.location.Location) {
        Log.d(TAG, "timeInitializer: ")
        gmt = TimeZone.getDefault()
        astroLocation = Location(location.latitude, location.longitude, gmt.rawOffset/3600000.toDouble(), 0)
        timeHelper = TimeHelper(astroLocation, dataSource)
        runBlocking {
            withContext(Dispatchers.IO) {
                timeHelper.prayerTime()
            }
        }

    }


}