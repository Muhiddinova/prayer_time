package com.example.prayertime

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        cm.registerNetworkCallback(NetworkRequest.Builder().build(),object : ConnectivityManager.NetworkCallback(){

            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                Variables.isNetworkAvailable .postValue(true)



            }

            override fun onLosing(network: Network, maxMsToLive: Int) {
                super.onLosing(network, maxMsToLive)
                Variables.isNetworkAvailable.postValue(false)


            }

            override fun onLost(network: Network) {
                super.onLost(network)
                Variables.isNetworkAvailable.postValue(false)
            }

            override fun onUnavailable() {
                super.onUnavailable()
                Variables.isNetworkAvailable.postValue(false)
            }

        })
    }

}