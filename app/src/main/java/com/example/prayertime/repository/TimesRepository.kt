package com.example.prayertime.repository

import com.example.prayertime.model.Times
import com.example.prayertime.database.TimesByYearDao

class TimesRepository(private val dataSource: TimesByYearDao) {

    fun insertTimes(times: Times){
        dataSource.insertTimes(times)
    }

    fun clearAllFromTable(){
        dataSource.clearAllFromTable()
    }

    fun getTime(date: String): Times{
        return dataSource.getTime(date)
    }

    fun getFirstElement(): Times{
        return dataSource.getFirstElement()
    }

    fun getSecondElement(): Times{
        return  dataSource.getSecondElement()
    }
}