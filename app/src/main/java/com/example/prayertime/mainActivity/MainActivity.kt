package com.example.prayertime.mainActivity

import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.azan.astrologicalCalc.Location
import com.example.prayertime.R
import com.example.prayertime.database.RoomDatabase
import com.example.prayertime.database.TimesByYearDao
import com.example.prayertime.databinding.ActivityMainBinding
import com.example.prayertime.helper.LocationHelper
import com.example.prayertime.helper.TimeHelper
import com.example.prayertime.ui.prayerTime.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.*

class MainActivity: AppCompatActivity() {

    private val TAG = "MainActivity Main"
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var locationManager: LocationManager
    private lateinit var locationHelper: LocationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val navHost =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        locationHelper = LocationHelper.getInstance(this)
        navController = navHost.navController
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val dataSource = RoomDatabase.getDatabase(this).timesByYearDao
        val factory = MainActivityViewModelFactory(dataSource)
        viewModel = ViewModelProviders.of(this, factory).get(MainActivityViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        locationHelper.getLocationLiveData().observe(this) {
            Log.d(TAG, "onCreate: ${it.longitude}")
            Log.d(TAG, "onCreate: ${it.latitude}")
            viewModel.timeInitializer(it)
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_REQ_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationHelper.hasLocationPermission = true
                locationHelper.alertDialogGpsCheck()
            } else {
                locationHelper.showDialogForPermission()
            }
        }
    }
}
