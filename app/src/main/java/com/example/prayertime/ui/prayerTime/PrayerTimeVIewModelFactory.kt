package com.example.prayertime.ui.prayerTime

//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import com.example.prayertime.database.TimesByYearDao
//
//class PrayerTimeVIewModelFactory(private val dataSource: TimesByYearDao): ViewModelProvider.Factory {
//    @Suppress("UNCHECKED_CAST")
//    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(PrayerTimeViewModel::class.java)){
//            return PrayerTimeViewModel(dataSource) as T
//        } else throw  IllegalArgumentException("PrayerTimeFragment class not found")
//    }
//}