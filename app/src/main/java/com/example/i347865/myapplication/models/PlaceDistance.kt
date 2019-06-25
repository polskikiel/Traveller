package com.example.i347865.myapplication.models


class PlaceDistance {
    var distance: Float = 0F
    var place: Place

    constructor(place: Place, distance: Float) {
        this.distance = distance
        this.place = place
    }

    override fun toString(): String {
        return "PlaceDistance(distance=$distance, place=$place)"
    }


}