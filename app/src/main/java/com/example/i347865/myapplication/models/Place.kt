package com.example.i347865.myapplication.models

import java.io.Serializable


class Place : Serializable {
    var place: Point = Point()
    var points: MutableSet<Point> = mutableSetOf()

    constructor()
    constructor(place: Point, points: MutableSet<Point>) {
        this.place = place
        this.points = points
    }

}