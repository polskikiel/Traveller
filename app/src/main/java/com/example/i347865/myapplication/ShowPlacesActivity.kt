package com.example.i347865.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.i347865.myapplication.models.PlaceDistance
import com.example.i347865.myapplication.models.Point
import com.example.i347865.myapplication.services.PlacesServices
import kotlinx.android.synthetic.main.activity_show_places.*

class ShowPlacesActivity : AppCompatActivity() {

    private var places = setOf<PlaceDistance>()

    companion object {
        private val placesServices = PlacesServices()
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_places)

        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        places = placesServices.getNearestPlaces(location)
        setAdaptersOnClick()
    }

    private fun setAdaptersOnClick() {
        val placesAdapter = PlacesAdapter(this, places.toList())
        places_lv.adapter = placesAdapter
        places_lv.onItemClickListener = AdapterView.OnItemClickListener{ adapterView, view, position, id ->
            val pointsAdapter = PointsAdapter(places.toList()[position].place.points.toList())
            places_lv.adapter = pointsAdapter
            places_lv.onItemClickListener = AdapterView.OnItemClickListener{ adapterView2, view2, position2, id2 ->
                places_lv.adapter = placesAdapter
                setAdaptersOnClick()
            }
        }
    }

    inner class PlacesAdapter(context: Context, private var placesList: List<PlaceDistance>) : BaseAdapter() {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
            val view: View?
            val vh: ViewHolder

            if (convertView == null) {
                view = layoutInflater.inflate(R.layout.place, parent, false)
                vh = ViewHolder(view)
                view.tag = vh
                Log.i("JSA", "set Tag for ViewHolder, position: " + position)
            } else {
                view = convertView
                vh = view.tag as ViewHolder
            }

            vh.tvTitle.text = placesList[position].place.place.title
            vh.tvContent.text = placesList[position].place.place.description
            vh.tvDistance.text = "${(placesList[position].distance.toString().split(".")[0].toInt()/1000)}km"

            return view
        }

        override fun getItem(position: Int): Any {
            return placesList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return placesList.size
        }
    }


    private class ViewHolder(view: View?) {
        val tvTitle: TextView = view?.findViewById(R.id.tvTitle) as TextView
        val tvContent: TextView = view?.findViewById(R.id.tvContent) as TextView
        val tvDistance: TextView = view?.findViewById(R.id.tvDistance) as TextView
    }

    private class PointViewHolder(view: View?) {
        val tvTitle: TextView = view?.findViewById(R.id.tvTitle) as TextView
        val tvContent: TextView = view?.findViewById(R.id.tvContent) as TextView
    }

    inner class PointsAdapter(private var pointsList: List<Point>) : BaseAdapter() {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
            val view: View?
            val vh: PointViewHolder

            if (convertView == null) {
                view = layoutInflater.inflate(R.layout.place, parent, false)
                vh = PointViewHolder(view)
                view.tag = vh
                Log.i("JSA", "set Tag for ViewHolder, position: " + position)
            } else {
                view = convertView
                vh = view.tag as PointViewHolder
            }

            vh.tvTitle.text = pointsList[position].title
            vh.tvContent.text = pointsList[position].description

            return view
        }

        override fun getItem(position: Int): Any {
            return pointsList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return pointsList.size
        }
    }


}
