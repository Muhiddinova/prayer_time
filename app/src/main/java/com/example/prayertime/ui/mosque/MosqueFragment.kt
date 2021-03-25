package com.example.prayertime.ui.mosque

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.directions.route.Route
import com.directions.route.RouteException
import com.directions.route.Routing
import com.directions.route.RoutingListener
import com.example.prayertime.R
import com.example.prayertime.databinding.FragmentMosqueBinding
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList


@Suppress("CAST_NEVER_SUCCEEDS")
class MosqueFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMapClickListener,
    GoogleMap.InfoWindowAdapter, GoogleMap.OnMyLocationButtonClickListener, RoutingListener {

    private lateinit var binding: FragmentMosqueBinding
    private var TAG = "MosqueFragment"
    private lateinit var mMap: GoogleMap
    private var sIsPermissionGranted = false
    private var mLocation: Location? = null
    private lateinit var mPlacesClient: PlacesClient
    private var mLastLocation: Location? = null
    private var mCurrLocationMarker: Marker? = null
    private var mLocationRequest: LocationRequest? = null
    private val PROXIMITY_RADIUS = 5000
    private lateinit var observable: Observable<Place>
    private val url: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mosque, container, false)
        Places.initialize(requireContext(), resources.getString(R.string.google_maps_key))

        mPlacesClient = Places.createClient(requireActivity())
        sIsPermissionGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        binding.map.onCreate(savedInstanceState)
        binding.map.onResume()
        try {
            MapsInitializer.initialize(requireContext().applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        binding.map.getMapAsync(this)

        binding.btnMosque.setOnClickListener(object : View.OnClickListener {
            var Mosque = "mosque"
            override fun onClick(v: View) {
                Log.d("onClick", "Button is Clicked")
                mMap.clear()
                val url = getUrl(41.311081, 69.240562, Mosque)
                val DataTransfer = arrayOfNulls<Any>(2)
                DataTransfer[0] = mMap
                DataTransfer[1] = url
                Log.d("onClick", url)

                getNearbyPlaceData(DataTransfer)
                Log.d(TAG, "onCreateView: ")
                Toast.makeText(requireContext(), "Nearby Restaurants", Toast.LENGTH_LONG).show()
            }
        })
        return binding.root
    }


    override fun onMapReady(googleMap: GoogleMap) {
        val markerOptions = MarkerOptions()
        Log.d(TAG, "onMapReady: ")
        mMap = googleMap
//        val uzb = LatLng(-34.0, 151.0)
        val uzb = LatLng(41.311081, 69.240562)
        mMap.addMarker(
            MarkerOptions()
                .position(uzb)
                .title("O'zbekiston")
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLng(uzb))
        mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.isTrafficEnabled = true
        mMap.setOnMapClickListener(this)
        mMap.setInfoWindowAdapter(this)
        mMap.setOnMyLocationButtonClickListener(this)
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11F))
        Log.d(TAG, "onMapReady: $this")
        enableLocation()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_SHORT)
                            .show()
                        sIsPermissionGranted = true
                        enableLocation()
                    }
                } else {
                    Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }


    override fun onMapClick(latLng: LatLng?) {
        mMap.clear()
        mMap.addMarker(latLng?.let {
            MarkerOptions().position(it)
                .title("marker")
        })


        mMap.animateCamera(
            CameraUpdateFactory.newLatLng(latLng),
            1000,
            object : GoogleMap.CancelableCallback {
                override fun onFinish() {
                    Toast.makeText(
                        requireContext(),
                        "move camera finish",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }

                override fun onCancel() {
                    Toast.makeText(
                        requireContext(),
                        "move camera cancelled",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        drawRoute(latLng)
    }

    override fun getInfoWindow(marker: Marker?): View? {
        return null
    }

    override fun getInfoContents(p0: Marker?): View? {
        Log.d(TAG, "getInfoContents: $p0")
        return null
    }

    override fun onMyLocationButtonClick(): Boolean {
        getPlacesInfo()
        return false
    }

    override fun onRoutingFailure(exception: RouteException?) {
        Log.e(TAG, "onRoutingFailure: $exception")
    }

    override fun onRoutingStart() {
        Log.d(TAG, "onRoutingStart: start")
    }

    override fun onRoutingSuccess(list: ArrayList<Route>?, p1: Int) {
        Log.d(TAG, "onRoutingSuccess: $p1")
        val options = PolylineOptions()

        list?.let {
            repeat(list.size) { i ->
                options.color(Color.BLUE)
                options.addAll(list[i].points)
                options.width((10 + 3 * i).toFloat())
                val polyline = mMap.addPolyline(options)
            }
        }
    }

    override fun onRoutingCancelled() {
        Log.d(TAG, "onRoutingCancelled: routing cancelled")
    }

    private fun onLocationChanged(location: Location) {
        mLocation = location
        mCurrLocationMarker?.remove()
        val latLng = LatLng(location.latitude, location.longitude)
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.title("Current position")
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))


    }


    private fun enableLocation() {
        if (sIsPermissionGranted) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            mMap.isMyLocationEnabled = true
            mMap.uiSettings.isMyLocationButtonEnabled = true
            mMap.setOnMyLocationClickListener {
                mLocation = it
            }
        } else {
            requestPermission()
        }

    }

    private fun requestPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            this.requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)
        }
    }


    private fun drawRoute(latLng: LatLng?) {
        if (mLocation != null && latLng != null) {

            val routing = Routing.Builder()
                .alternativeRoutes(true)
                .language("en")
                .withListener(this)
                .waypoints(LatLng(mLocation!!.latitude, mLocation!!.longitude), latLng)
                .key(resources.getString(R.string.google_maps_key))
                .build()
            routing.execute()
        }
    }


    @SuppressLint("MissingPermission")
    private fun getPlacesInfo() {
        val list = arrayListOf<String>()

        val placeFiles =
            arrayListOf(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.USER_RATINGS_TOTAL)

        val request = FindCurrentPlaceRequest.newInstance(placeFiles)

        val placeResult = mPlacesClient.findCurrentPlace(request)

        placeResult.addOnCompleteListener { response ->
            Log.d(TAG, "getPlacesInfo: ${response.isSuccessful}")
            if (response.isSuccessful) {
                val result = response.result
                if (result != null) {
                    Log.d(TAG, "getPlacesInfo: ${result.placeLikelihoods.size}")

                    repeat(result.placeLikelihoods.size) {

                        result.placeLikelihoods[it]
                            .place
                            .address?.let { it1 -> list.add(it1) }
                    }
                }
                Log.d("MapsActivity", "getPlacesInfo: ${list.toString()}")
            } else {
                Log.e("MapsActivity", "getPlacesInfo: ${response.exception}")
            }
        }

    }

    private fun getUrl(latitude: Double, longitude: Double, nearbyPlace: String): String {
        val googlePlacesUrl =
            java.lang.StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?")
        googlePlacesUrl.append("location=$latitude,$longitude")
        googlePlacesUrl.append("&radius=$PROXIMITY_RADIUS")
        googlePlacesUrl.append("&type=$nearbyPlace")
        googlePlacesUrl.append("&sensor=true")
        googlePlacesUrl.append("&key=" + "AIzaSyBV6Bor0xB5wpfrkOISDWz8QaNHmJcakKg")
        Log.d("getUrl", googlePlacesUrl.toString())
        return googlePlacesUrl.toString()
    }

    private fun getNearbyPlaceData(params: Array<Any?>) {
        var url: String? = null
        val observer = object : Observer<String> {
            override fun onSubscribe(d: Disposable) {
                Log.d(TAG, "onSubscribe: called")
            }


            override fun onError(e: Throwable) {
                Log.e(TAG, "onError: ", e)
            }

            override fun onComplete() {
                Log.d(TAG, "onComplete: called")
            }

            override fun onNext(jsonString: String) {
                var nearbyPlacesList: List<HashMap<String, String>?>? = null
                val dataParser = DataParser()
                nearbyPlacesList = dataParser.parse(jsonString)
                showNearbyPlaces(nearbyPlacesList)
            }

        }


        Observable
            .create<String> {
                try {
                    mMap = params[0] as GoogleMap

                    url = params[1] as String

                    val downloadUrl = DownloadUrl()

                    val googlePlacesData = downloadUrl.readUrl(url)

                    if (!it.isDisposed) {
                        it.onNext(googlePlacesData)
                    }

                } catch (e: java.lang.Exception) {
                    Log.e(TAG, "getNearbyPlaceData: ",e )

                }

            }
//        fromCallable(
//            Callable {
//            try {
//            Log.d("GetNearbyPlacesData", "doInBackground entered")
//            mMap = params[0] as GoogleMap
//            url = params[1] as String
//            val downloadUrl = DownloadUrl()
//            googlePlacesData = downloadUrl.readUrl(url)
//            Log.d("GooglePlacesReadTask", "doInBackground Exit")
//        } catch (e: java.lang.Exception) {
//            Log.d("GooglePlacesReadTask", e.toString())
//        }
//        })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer)


    }

    private fun showNearbyPlaces(nearbyPlacesList: List<HashMap<String, String>?>) {

        for (i in nearbyPlacesList.indices) {
            Log.d("onPostExecute", "Entered into showing locations")
            val markerOptions = MarkerOptions()

            val googlePlace = nearbyPlacesList[i]
            val lat = googlePlace?.get("lat")!!.toDouble()
            val lng = googlePlace["lng"]!!.toDouble()
            val placeName = googlePlace["place_name"]
            val vicinity = googlePlace["vicinity"]
            val latLng = LatLng(lat, lng)
            markerOptions.position(latLng)
            markerOptions.title("$placeName : $vicinity")
            mMap.addMarker(markerOptions)
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            //move map camera
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            mMap.animateCamera(CameraUpdateFactory.zoomTo(11f))
        }
    }


}


