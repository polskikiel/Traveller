package com.example.i347865.myapplication

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.example.i347865.myapplication.models.PlaceDistance
import com.example.i347865.myapplication.models.Point
import com.example.i347865.myapplication.models.PointDistance
import com.example.i347865.myapplication.services.PlacesServices
import com.example.i347865.myapplication.services.UserServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*


class MapsAct : FragmentActivity(), OnMapReadyCallback {
    companion object {
        private const val LOCATION_REQUEST_CODE = 101
        private val placesServices = PlacesServices()
        private val userServices = UserServices()
        private var lastPoints = mutableSetOf<Point>()
        private var placesList = setOf<PlaceDistance>()
        private var mGoogleMap: GoogleMap? = null

        private lateinit var pointIcon: BitmapDescriptor
        private lateinit var pointIconDone: BitmapDescriptor

        private fun putMarkersForPlaces() {
            placesServices.getPlaces().forEach { place ->
                val base = place.place
                mGoogleMap!!.addMarker(MarkerOptions().position(LatLng(base.x, base.y)).title(base.title))
            }
        }

        private fun putMarkersForLastPlaces() {
            lastPoints.forEach { place ->
                userServices.getVisits().forEach { visit ->
                    println("VISITS: " + visit.points)
                    visit.points.forEach { pt ->
                        if (pt.x == place.x && pt.y == place.y) {
                            mGoogleMap!!.addMarker(
                                MarkerOptions().position(
                                    LatLng(
                                        place.x,
                                        place.y
                                    )
                                ).title(place.title).icon(pointIconDone)
                            )
                        }
                    }
                }
                mGoogleMap!!.addMarker(
                    MarkerOptions().position(
                        LatLng(
                            place.x,
                            place.y
                        )
                    ).title(place.title).icon(pointIcon)
                )
            }

        }

        private fun refreshPlaces() {
            mGoogleMap!!.clear()
            putMarkersForPlaces()
            putMarkersForLastPlaces()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) =
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            val longitude = location.longitude
            val latitude = location.latitude

            val myLocationListener = MyLocationListener()
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0F, myLocationListener)

            mGoogleMap = googleMap
            googleMap.isMyLocationEnabled = true
            googleMap.uiSettings.isMyLocationButtonEnabled = true
            googleMap.uiSettings.isScrollGesturesEnabled = true
            googleMap.uiSettings.isZoomControlsEnabled = true
            googleMap.uiSettings.isCompassEnabled = true
            googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            pointIcon = BitmapDescriptorFactory.fromResource(R.drawable.ic_map_point)
            pointIconDone = BitmapDescriptorFactory.fromResource(R.drawable.ic_map_point_done)

            googleMap.setOnMarkerClickListener { marker ->
                var result = false
                if (placesServices.isMainPlace(marker.title)) {
                    val pointsForTitle = placesServices.getPointsForTitle(marker.title)

                    val markers = mutableSetOf<Marker>()
                    for (point in pointsForTitle) {
                        val addMarker = googleMap.addMarker(
                            MarkerOptions().position(
                                LatLng(
                                    point.x,
                                    point.y
                                )
                            ).title(point.title).icon(pointIcon)
                        )
                        markers.add(addMarker)
                    }
                    val builder = LatLngBounds.Builder()
                    for (m in markers) {
                        builder.include(m.position)
                    }

                    val bounds = builder.build()
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 250))
                    lastPoints = pointsForTitle.toMutableSet()
                    refreshPlaces()
                    result = true
                }
                result
            }
            googleMap.setOnInfoWindowClickListener {m ->
                if (!placesServices.isMainPlace(m.title)) {
                    val intent = Intent(this, PlacesActivity::class.java)
                    intent.putExtra("place", lastPoints.filter { point -> point.title == m.title }.get(0))
                    startActivity(intent)
                }

            }

            //add user and store data for achieved points
            putMarkersForPlaces()
            val nearestPlaces = placesServices.getNearestPlaces(location)
            if (nearestPlaces.size > 2) {

                val builder = LatLngBounds.Builder()
                for (p in nearestPlaces.toList().subList(0, 2)) {
                    builder.include(LatLng(p.place.place.x, p.place.place.y))
                }
                val bounds = builder.build()
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 250))
            }
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), 6f))

        } else {
            requestPermission(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_REQUEST_CODE
            )
        }

    public override fun onResume() {
        super.onResume()

        if (mGoogleMap != null) { //prevent crashing if the map doesn't exist yet (eg. on starting activity)
        }
    }

    private fun requestPermission(
        permissionType: Array<String>,
        requestCode: Int
    ) {
        ActivityCompat.requestPermissions(
            this,
            permissionType, requestCode
        )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] !=
                    PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(
                        this,
                        "Unable to show location - permission required",
                        Toast.LENGTH_LONG
                    ).show()
                } else {

                    val mapFragment = supportFragmentManager
                        .findFragmentById(R.id.map) as SupportMapFragment
                    mapFragment.getMapAsync(this)
                }
            }
        }
    }

    internal class MyLocationListener : LocationListener {
        override fun onLocationChanged(location: Location) {
            var a = false
            val nearestPlaces = placesServices.getNearestPlaces(location)

            nearestPlaces.forEach { placeDistance: PlaceDistance ->
                if (placeDistance.distance < 1000) {
                    placesServices.getNearestPoints(location, placeDistance.place)
                        .forEach { pointDistance: PointDistance ->
                            if (pointDistance.distance < 50) {
                                a = userServices.addVisit(placeDistance.place, pointDistance.point)
                            }
                        }
                }
            }
            println("DUPA ${nearestPlaces.size}:${placesList.size}")
            if (a || placesList.size != nearestPlaces.size) {
                refreshPlaces()
            }
            placesList = nearestPlaces
        }

        override fun onProviderDisabled(provider: String) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
    }

}