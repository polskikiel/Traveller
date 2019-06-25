package com.example.i347865.myapplication.services

import com.example.i347865.myapplication.models.Place
import com.example.i347865.myapplication.models.Point
import com.example.i347865.myapplication.models.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.jetbrains.anko.doAsync
import java.net.URL
import java.util.concurrent.Executors

class UserServices {

    private var host = "https://travllerr.herokuapp.com"

    companion object {
        var user = User()
        var visitedPlaces = mutableSetOf<Place>()
    }

    fun register(login: String, password: String) {
        val executor = Executors.newScheduledThreadPool(5)

        doAsync(executorService = executor) {
            val resp = URL("$host/register?login=$login&password=$password")
            resp.openConnection().doOutput = false
            val collectionType =
                object : TypeToken<String>() {}.type
            user.token = Gson().fromJson(resp.readText(), collectionType) as String
            user.username = login
        }
    }

    fun addVisit(place: Place, point: Point): Boolean {
        if (!visitedPlaces.contains(place)) {
            return visitedPlaces.add(Place(place.place, mutableSetOf(point)))
        }

        var pCache = Place()
        visitedPlaces.forEach { visitedPlace ->
            if (visitedPlace.place.x == place.place.x && visitedPlace.place.y == place.place.y) {
                pCache = visitedPlace
            }
        }
        pCache.points.add(point)
        return visitedPlaces.add(pCache)
    }

    fun getVisits(): MutableSet<Place>{
        return visitedPlaces
    }

    fun isLogged(): Boolean {
        return !user.token.isEmpty()
    }
}