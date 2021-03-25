package com.example.prayertime.helper

import android.annotation.SuppressLint
import android.util.Log
import com.azan.Azan
import com.azan.AzanTimes
import com.azan.Madhhab
import com.azan.Method
import com.azan.astrologicalCalc.Location
import com.azan.astrologicalCalc.SimpleDate
import com.example.prayertime.model.Times
import com.example.prayertime.database.TimesByYearDao
import com.example.prayertime.repository.TimesRepository
import java.util.*

class TimeHelper(private val location: Location, private val dataSource: TimesByYearDao) {

    private val TAG = "GettingTime"
    private val timesRepository =  TimesRepository(dataSource)

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    fun prayerTime() {

        Log.d(
            "Location",
            "GettingTime Longtitude: ${location!!.degreeLong} Lattitude: ${location!!.degreeLat}"
        )
        location.let {

            Log.d(
                "Location",
                "GettingTime Longtitude: ${location!!.degreeLong} Lattitude: ${location!!.degreeLat}"
            )

            val method = Method.NORTH_AMERICA
            method.madhhab = Madhhab.HANAFI
            val azan = Azan(location, method)
            var simpleDate : SimpleDate
            var prayerTimes: AzanTimes
            val calendar = Calendar.getInstance()
            val mDay = calendar[Calendar.DAY_OF_YEAR] // returning int day of year

            var year : Int
            var month: Int
            var dayOfMonth: Int

            var fajr  = ""
            var shuruq = ""
            var thuhr =""
            var assr = ""
            var maghrib = ""
            var ishaa = ""

            calendar.set(Calendar.DAY_OF_YEAR, mDay)
            Log.d(TAG, "calendar.time: SET ${calendar.time}")
            calendar.add(Calendar.DAY_OF_YEAR, -1)

            timesRepository.clearAllFromTable()

            val gCalendar = GregorianCalendar()

            for(x in 0..365){

                var day1 = gCalendar.get(GregorianCalendar.DATE)
                var month1 = gCalendar.get(GregorianCalendar.MONTH) + 1
                var year1 = gCalendar.get(GregorianCalendar.YEAR)
                val total = GregorianCalendar(year1, month1, day1)
                calendar.add(Calendar.DAY_OF_YEAR, 1)
//                Log.d(TAG, "calendar.time: ADD ${calendar.time}")
                year = calendar.get(Calendar.YEAR)
                month = calendar.get(Calendar.MONTH)+1
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
                simpleDate = SimpleDate(dayOfMonth, month, year)

                prayerTimes = azan.getPrayerTimes(simpleDate)
                fajr = prayerTimes.fajr().toString()
                shuruq = prayerTimes.shuruq().toString()
                thuhr = prayerTimes.thuhr().toString()
                assr = prayerTimes.assr().toString()
                maghrib = prayerTimes.maghrib().toString()
                ishaa = prayerTimes.ishaa().toString()
//                Log.d(TAG, "fajr: $fajr, shuruq: $shuruq, thuhr: $thuhr, assr: $assr, maghrib: $maghrib, ishaa: $ishaa")

                val times = Times(x, simpleDate, fajr, shuruq, thuhr, assr, maghrib, ishaa)
//                Log.d(TAG, "times: $times")

                //need to insert into room
                timesRepository.insertTimes(times)

            }
        }
    }

//    fun getTime(date: String): Times{
//        return timesRepository.getTime(date)
//    }
}
