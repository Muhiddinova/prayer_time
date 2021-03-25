package com.example.prayertime.model

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.azan.astrologicalCalc.SimpleDate
import java.util.*

@Entity(tableName = "times")
data class Times(
    @PrimaryKey
    val timesId: Int? = null,
    @TypeConverters(DateConverter::class)
    val date: SimpleDate,
    val fajr: String = "",
    val shuruq: String = "",
    val thuhr: String = "",
    val assr: String = "",
    val maghrib: String = "",
    val ishaa: String = ""
)



class DateConverter {
    @RequiresApi(Build.VERSION_CODES.N)
    @TypeConverter
    fun fromDate(date: SimpleDate): String {
        return ("${date.day}-${date.month}-${date.year}")
    }

    @TypeConverter
    fun toDate(data: String): SimpleDate {
        val simpleDate = data.split("-".toRegex())
        val day = SimpleDate(GregorianCalendar())
        day.day= simpleDate[0].toInt()
        day.month = simpleDate[1].toInt()
        day.year = simpleDate[2].toInt()
        return day
    }
}

