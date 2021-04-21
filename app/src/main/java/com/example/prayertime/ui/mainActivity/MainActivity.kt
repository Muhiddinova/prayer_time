package com.example.prayertime.ui.mainActivity

import android.Manifest
import android.app.Dialog
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.prayertime.R
import com.example.prayertime.constants.LOCATION_REQ_CODE
//import com.example.prayertime.database.RoomDatabase
import com.example.prayertime.databinding.ActivityMainBinding
import com.example.prayertime.helper.*

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity Main"
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var locationManager: LocationManager
    private lateinit var locationHelper: LocationHelper
    private lateinit var progress: Dialog
    private lateinit var prefs: SharedPreferences
    private var location: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val navHost =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        locationHelper = LocationHelper(this)
        navController = navHost.navController
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        progress = Dialog(this)
//        val dataSource = RoomDatabase.getDatabase(this).timesByYearDao
//        val factory = MainActivityViewModelFactory(dataSource)
        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && (FINISH_FLAG == 1)
        ) {
            locationHelper.showDialogSecondTime()
        }else if(ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED){
//            locationHelper.showDialogGpsCheck()
            locationHelper.getLocationViaProviders()
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
                locationHelper.hasPermission = true
                locationHelper.getCurrentLocation()
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    val showRational = shouldShowRequestPermissionRationale(permissions[0])
                    if (showRational) {
                        locationHelper.showDialogSecondTime()
                    } else {
                        locationHelper.showDialogThirdTime()
                        //here has some problems
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        locationHelper.dialogDismiss()
    }
}
