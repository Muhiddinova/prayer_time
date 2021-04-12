package com.example.prayertime.model


import com.azan.astrologicalCalc.SimpleDate

data class Times(
    val date: SimpleDate,
    val fajr: String = "",
    val shuruq: String = "",
    val thuhr: String = "",
    val assr: String = "",
    val maghrib: String = "",
    val ishaa: String = ""
)

