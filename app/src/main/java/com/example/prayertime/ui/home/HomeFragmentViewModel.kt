package com.example.prayertime.ui.home

import androidx.lifecycle.ViewModel
import com.example.prayertime.database.TimesByYearDao
import com.example.prayertime.model.Times
import com.example.prayertime.repository.TimesRepository

class HomeFragmentViewModel(dataSource: TimesByYearDao): ViewModel() {

    private val timesRepository = TimesRepository(dataSource)

    fun getFirstElement(): Times {
        return timesRepository.getFirstElement()
    }

    fun getSecondElement(): Times {
        return  timesRepository.getSecondElement()
    }
}