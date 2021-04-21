package com.example.prayertime.ui.mainActivity

//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import com.example.prayertime.database.TimesByYearDao
//
//class MainActivityViewModelFactory(
//    private val dataSource: TimesByYearDao
//) : ViewModelProvider.Factory {
//    @Suppress("UNCHECKED_CAST")
//    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
//            return MainActivityViewModel(dataSource) as T
//        } else throw  IllegalArgumentException("MainActivity class not found")
//    }
//}