package com.example.prayertime.ui.home

import android.location.Location
import androidx.lifecycle.ViewModel
//import com.example.prayertime.database.TimesByYearDao
import com.example.prayertime.helper.TimeHelper
import com.example.prayertime.model.Times
//import com.example.prayertime.repository.TimesRepository

class HomeFragmentViewModel(): ViewModel() {

//    private val timesRepository = TimesRepository(dataSource)

    fun getFirstElement(location: Location): Times {
        return TimeHelper(location).getAllTimes()
    }

//    fun getSecondElement(): Times {
//        return  timesRepository.getSecondElement()
//    }
}