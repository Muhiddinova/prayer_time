package com.example.prayertime.helper

import android.location.Location
import com.azan.Azan
import com.azan.Madhhab
import com.azan.Method
import com.azan.Time
import com.azan.astrologicalCalc.SimpleDate
import com.example.prayertime.model.Times
import java.util.*

class TimeHelper(private val location: Location) {

    private val TAG = "GettingTime"
    private var times = arrayOf<Time>()
    private val mCalendar = Calendar.getInstance()
    private lateinit var simpleDate: SimpleDate

    init {
        mCalendar.time = Date()
        prayerTime()
        checkIsTimeAvailable()
    }


    fun prayerTime() {

        getSimpleDate()

        val method = Method.NORTH_AMERICA
        method.madhhab = Madhhab.HANAFI
        val gmt = TimeZone.getDefault().rawOffset / 3600000.toDouble()
        val azanLocation =
            com.azan.astrologicalCalc.Location(location.latitude, location.longitude, gmt, 0)
        val azan = Azan(azanLocation, method)

        val prayerTimes = azan.getPrayerTimes(simpleDate)
        times = prayerTimes.times
    }

    private fun checkIsTimeAvailable() {
        val year = mCalendar.get(Calendar.YEAR)
        val month = mCalendar.get(Calendar.MONTH)
        val day = mCalendar.get(Calendar.DATE)
        val ishaa = mCalendar
        ishaa.set(year, month, day, times[5].hour, times[5].minute)
        if (Date().time > ishaa.time.time) {
            mCalendar.add(Calendar.DAY_OF_YEAR, 1)
        }
    }

    private fun getSimpleDate() {
        val day = mCalendar.get(Calendar.DAY_OF_MONTH)
        val month = mCalendar.get(Calendar.MONTH)
        val year = mCalendar.get(Calendar.YEAR)
        simpleDate = SimpleDate(day, month, year)
    }

    fun getAlarmTime(): Long {
        val year = mCalendar.get(Calendar.YEAR)
        val month = mCalendar.get(Calendar.MONTH)
        val day = mCalendar.get(Calendar.DATE)
        val cal = mCalendar
        val now = Date().time
        (times.indices).forEach loop@{
            if (it == 1) return@loop
            cal.set(year, month, day, times[it].hour, times[it].minute)
            if (cal.timeInMillis > now) return cal.timeInMillis
        }
        cal
            .set(year, month, day, times[5].hour, times[5].minute)
        return cal.timeInMillis
    }

    fun getAllTimes(): Times {
        return Times(
            simpleDate,
            times[0].toString(),
            times[1].toString(),
            times[2].toString(),
            times[3].toString(),
            times[4].toString(),
            times[5].toString()
        )
    }

//    fun getTime(date: String): Times{
//        return timesRepository.getTime(date)
//    }
}
