package com.example.prayertime.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.prayertime.database.TimesByYearDao
import com.example.prayertime.ui.prayerTime.PrayerTimeViewModel

class HomeFragmentViewModelFactory(private val dataSource: TimesByYearDao): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeFragmentViewModel::class.java)){
            return HomeFragmentViewModel(dataSource) as T
        } else throw  IllegalArgumentException("HomeFragmentViewModel class not found")
    }
}