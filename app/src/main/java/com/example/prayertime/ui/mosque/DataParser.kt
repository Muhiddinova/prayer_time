package com.example.prayertime.ui.mosque

import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

class DataParser {
    private var TAG="DataParseMap"
    fun parse(jsonData: String?): List<HashMap<String, String>?> {
        var mJsonArray: JSONArray? = null
        val sJsonObject: JSONObject
        try {
            Log.d("Places", "parse")
            sJsonObject = JSONObject(jsonData)
            mJsonArray = sJsonObject.getJSONArray("results")
        } catch (e: JSONException) {
            Log.e(TAG, "parse: ", e)
            e.printStackTrace()
        }
        Log.d(TAG, "parse: $mJsonArray")
        return getPlaces(mJsonArray)

    }

    private fun getPlaces(jsonArray: JSONArray?): MutableList<HashMap<String, String>?> {
        val sPlacesCount = jsonArray!!.length()
        val sPlacesList: MutableList<HashMap<String, String>?> = ArrayList()
        var mPlaceMap: HashMap<String, String>? = null
        for (i in 0 until sPlacesCount) {
            try {
                mPlaceMap = getPlace(jsonArray[i] as JSONObject)
                sPlacesList.add(mPlaceMap)
            } catch (e: JSONException) {
                Log.e(TAG, "getPlaces: ", e)
                e.printStackTrace()
            }
        }
        return sPlacesList
    }

    private fun getPlace(googlePlaceJson: JSONObject): HashMap<String, String> {
        val googlePlaceMap = HashMap<String, String>()
        var placeName = "-NA-"
        var vicinity = "-NA-"
        var latitude = ""
        var longitude = ""
        var reference = ""
        Log.d("getPlace", "Entered")
        try {
            if (!googlePlaceJson.isNull("name")) {
                placeName = googlePlaceJson.getString("name")
            }
            if (!googlePlaceJson.isNull("vicinity")) {
                vicinity = googlePlaceJson.getString("vicinity")
            }
            latitude =
                googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat")
            longitude =
                googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng")
            reference = googlePlaceJson.getString("reference")
            googlePlaceMap["place_name"] = placeName
            googlePlaceMap["vicinity"] = vicinity
            googlePlaceMap["lat"] = latitude
            googlePlaceMap["lng"] = longitude
            googlePlaceMap["reference"] = reference
            Log.d("getPlace", "Putting Places")
        } catch (e: JSONException) {
            Log.e(TAG, "getPlace: ", e)
        }
        return googlePlaceMap
    }
}