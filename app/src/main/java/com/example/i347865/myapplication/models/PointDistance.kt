package com.example.i347865.myapplication.models

class PointDistance {
    var distance: Float = 0F
    var point: Point = Point()

    constructor(point: Point, distance: Float) {
        this.point = point
        this.distance = distance
    }
}