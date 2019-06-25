package com.example.i347865.myapplication.services

import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Handler
import android.os.IBinder
import com.example.i347865.myapplication.MapsAct
import com.example.i347865.myapplication.models.Place
import com.example.i347865.myapplication.models.PlaceDistance
import com.example.i347865.myapplication.models.Point
import com.example.i347865.myapplication.models.PointDistance
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.jetbrains.anko.doAsync
import java.net.URL
import java.util.concurrent.Executors


class PlacesServices : Service() {

    private lateinit var mHandler: Handler
    private lateinit var mRunnable: Runnable

    override fun onBind(intent: Intent): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        mHandler = Handler()
        mRunnable = Runnable { fetchPlaces() }
        mHandler.postDelayed(mRunnable, 2000)

        return Service.START_STICKY
    }

    companion object {
        var places = setOf<Place>()
    }

    fun getPlaces(): Set<Place> {
        return places
    }

    private fun fetchPlaces() {
        val host = "https://travllerr.herokuapp.com"
        val executor = Executors.newScheduledThreadPool(5)

        doAsync(executorService = executor) {
            val resp = URL("$host/places").readText()
            val collectionType =
                object : TypeToken<List<Place>>() {}.type
            val tmp = Gson().fromJson(resp, collectionType) as List<Place>
            places = tmp.toSet()
        }
    }

    fun isMainPlace(title: String): Boolean {
        places.forEach { place: Place ->
            if (place.place.title == title) {
                return true
            }
        }
        return false
    }

    fun getPointsForTitle(title: String): List<Point> {
        places.forEach { place: Place ->
            if (place.place.title == title) {
                return place.points.toList()
            }
        }
        return listOf()
    }

    fun getNearestPlaces(pos: Location): Set<PlaceDistance> {
        val result = mutableSetOf<PlaceDistance>()
        places.forEach { place ->
            val loc = Location("")
            loc.latitude = place.place.x
            loc.longitude = place.place.y
            result.add(PlaceDistance(place, pos.distanceTo(loc)))
        }
        return result.sortedBy { place -> place.distance }.toCollection(result)
    }

    fun getNearestPoints(pos: Location, place: Place): Set<PointDistance> {
        val result = mutableSetOf<PointDistance>()
        place.points.forEach { point ->
            val loc = Location("")
            loc.latitude = point.x
            loc.longitude = point.y
            result.add(PointDistance(point, pos.distanceTo(loc)))
        }
        result.sortedBy { point -> point.distance }.toCollection(result)
        return result
    }
}