package com.example.prayertime.ui.mainActivity

import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.prayertime.R
import com.example.prayertime.database.RoomDatabase
import com.example.prayertime.databinding.ActivityMainBinding
import com.example.prayertime.helper.LocationHelper
import com.example.prayertime.ui.prayerTime.*

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity Main"
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var locationManager: LocationManager
    private lateinit var locationHelper: LocationHelper
    private lateinit var progress: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val navHost =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        locationHelper = LocationHelper.getInstance(this)
        navController = navHost.navController
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        progress = Dialog(this)
        val dataSource = RoomDatabase.getDatabase(this).timesByYearDao
        val factory = MainActivityViewModelFactory(dataSource)
        viewModel = ViewModelProviders.of(this, factory).get(MainActivityViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        showProgress()
        locationHelper.checkLocationPermission()
        locationHelper.getLocationLiveData().observe(this) {
            Log.d(TAG, "onCreate: ${it.longitude}")
            if (it != null) {
                Log.d(TAG, "onCreate: ${it.longitude}")
                Log.d(TAG, "onCreate: ${it.latitude}")
                progress.dismiss()
                viewModel.timeInitializer(it)
            }
        }
    }

    private fun showProgress() {
        progress.setContentView(R.layout.progress_dialog)
        progress.setCancelable(false)
        progress.show()

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
                locationHelper.showDialogForPermission()
            } else if (grantResults[0] != PackageManager.PERMISSION_DENIED) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    val showRational = shouldShowRequestPermissionRationale(permissions[0])
                    if (showRational) {
                        locationHelper.showDialogForPermission()
                    } else {
                        locationHelper.showDialogForSettings()
                    }
                } else {
                    locationHelper.checkLocationPermission()
                }
            } else {
                locationHelper.showDialogForPermission()
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        progress.dismiss()
        locationHelper.dialogDismiss()
    }


}
