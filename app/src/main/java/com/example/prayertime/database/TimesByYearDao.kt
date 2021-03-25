package com.example.prayertime.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.azan.astrologicalCalc.SimpleDate
import com.example.prayertime.model.Times

@Dao
interface TimesByYearDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTimes(times: Times)

    @Query("DELETE FROM times")
    fun clearAllFromTable()

    @Query("Select * from times where times.date = :date ")
    fun getTime(date: String): Times

    @Query("Select * from times where timesId = 0")
    fun getFirstElement(): Times



}