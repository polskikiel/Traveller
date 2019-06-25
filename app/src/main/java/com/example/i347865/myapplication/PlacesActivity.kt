package com.example.i347865.myapplication

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.i347865.myapplication.models.Point
import com.example.i347865.myapplication.services.PlacesServices
import kotlinx.android.synthetic.main.activity_places.*
import kotlinx.android.synthetic.main.activity_places.view.*

class PlacesActivity : AppCompatActivity() {

    companion object {
        private val placesServices = PlacesServices()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_places)

        val place = intent.extras.getSerializable("place") as? Point
        val backAct = intent.extras.getSerializable("act") as? String

        placeName.text = place!!.title
        placeDesc.text = place.description

        val imageView = ImageView(this)
        Glide.with(this).load(place.img).into(imageView)
        linearLayout.layy.addView(imageView)
    }
}
