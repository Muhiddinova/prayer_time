package com.example.prayertime.ui.compass

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.hardware.SensorEventListener
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.prayertime.R
import com.example.prayertime.databinding.CompassNewBinding
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class CompassFragment : Fragment(), SensorEventListener, LocationListener {

    private var mGravity: FloatArray = FloatArray(3)
    private var mGeomagnetic: FloatArray = FloatArray(3)
    private var azimuth = 180f
    private var currentAzimuth = 180f
    private lateinit var sensorManager: SensorManager
    private lateinit var binding: CompassNewBinding
    private lateinit var viewModel: CompassViewModel
    private var degree: Double = 0.0
    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2
    private var longtitude: Float = 0f
    private var lattitude: Float = 0f
    private var result: Double = 0.0
    private var intDegree: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?


    ): View? {
        Log.d("gps:", "Compass Fragment: degree: $degree")
        binding = DataBindingUtil.inflate(inflater, R.layout.compass_new, container, false)
        binding.imgCompass.startAnimation()
        sensorManager = (requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager?)!!

        getLocation()
        degree = getDegree()
        intDegree = degree.toInt()

        Log.d("gps", "getLocation  : $degree")
        binding.tvLocation.text = "$intDegree Degree "
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CompassViewModel::class.java)
        // TODO: Use the ViewModel


    }

    override fun onResume() {
        super.onResume()

        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
            SensorManager.SENSOR_DELAY_GAME
        )
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_GAME
        )
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        val alpha: Float = 0.97f
        synchronized(this){
            if (sensorEvent != null) {
                if(sensorEvent.sensor.type == Sensor.TYPE_ACCELEROMETER){
                    mGravity[0] = alpha * mGravity[0] + (1 - alpha) * sensorEvent.values[0]
                    mGravity[1] = alpha * mGravity[1] + (1 - alpha) * sensorEvent.values[1]
                    mGravity[2] = alpha * mGravity[2] + (1 - alpha) * sensorEvent.values[2]
                }
                if(sensorEvent.sensor.type == Sensor.TYPE_MAGNETIC_FIELD){
                    mGeomagnetic[0] = alpha * mGeomagnetic[0] + (1 - alpha) * sensorEvent.values[0]
                    mGeomagnetic[1] = alpha * mGeomagnetic[1] + (1 - alpha) * sensorEvent.values[1]
                    mGeomagnetic[2] = alpha * mGeomagnetic[2] + (1 - alpha) * sensorEvent.values[2]
                }

                val R: FloatArray = FloatArray(9)
                val I: FloatArray = FloatArray(9)
                val success: Boolean = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic)
                if (success){
                    val orientation: FloatArray = FloatArray(3)
                    SensorManager.getOrientation(R, orientation)
                    azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
                    azimuth = ((azimuth - degree + 360)%360).toFloat()

                    val anim: Animation = RotateAnimation(
                        -currentAzimuth, -azimuth,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f
                    )
                    currentAzimuth = azimuth

                    anim.duration = 100
                    anim.repeatCount = 0
                    anim.fillAfter = true

//                    binding.tvPosition.text = ("Your current position: $degree")
//                    binding.tvPosition.width
                    binding.imgCompass.startAnimation(anim)
                } else{}
            } else{
                Toast.makeText(requireContext(), "THERE IS NO TYPE_ACCELEROMETER OR TYPE_MAGNETIC_FIELD", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        var mAccelerometerAccuracy : Int = 0
        var mMagneticAccuracy: Int = 0
        when (sensor!!.type) {
            Sensor.TYPE_ACCELEROMETER -> mAccelerometerAccuracy = accuracy
            Sensor.TYPE_MAGNETIC_FIELD -> mMagneticAccuracy = accuracy
            else -> {
            }
        }
    }

    override fun onLocationChanged(location: Location) {
        lattitude = location.latitude.toFloat()
        longtitude = location.longitude.toFloat()
    }

    private fun getLocation() {
        locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(
                requireContext() as Activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                locationPermissionCode
            )
        }
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            5000,
            5f,
            LocationListener { })
    }

    private fun getDegree(): Double {
        val kaabaLng = 39.826206 // ka'bah Position https://www.latlong.net/place/kaaba-mecca-saudi-arabia-12639.html
        val kaabaLat = Math.toRadians(21.422487) // ka'bah Position https://www.latlong.net/place/kaaba-mecca-saudi-arabia-12639.html

        val myLatRad = Math.toRadians(lattitude.toDouble())
        val longDiff = Math.toRadians(kaabaLng - longtitude.toDouble())
        val y = sin(longDiff) * cos(kaabaLat)
        val x = cos(myLatRad) * sin(kaabaLat) - sin(myLatRad) * cos(kaabaLat) * cos(longDiff)
        result = (Math.toDegrees(atan2(y, x)) + 180) % 360
        return  result
    }

}