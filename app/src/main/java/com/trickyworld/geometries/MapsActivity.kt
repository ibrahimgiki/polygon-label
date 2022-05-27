package com.trickyworld.geometries

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.ui.IconGenerator
import com.trickyworld.geometries.databinding.ActivityMapsBinding
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnPolygonClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private lateinit var customIcon : BitmapDescriptor
    private lateinit var iconGenerator: IconGenerator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        iconGenerator = IconGenerator(this)
        iconGenerator!!.setStyle(IconGenerator.STYLE_ORANGE)
    }

    override fun onMapReady(googleMap: GoogleMap) {
       mMap = googleMap
       mMap.setOnPolygonClickListener(this)

        var polygon = mMap.addPolygon(
            PolygonOptions()
                .add(
                    LatLng(34.046324, 71.556191),
                    LatLng(34.046538, 71.574602),
                    LatLng(34.037043, 71.573658),
                    LatLng(34.036794, 71.549153))
                .strokeWidth(7f)
                .clickable(true)
                .fillColor(Color.CYAN)
                .strokeColor(Color.BLUE)
        )
        polygon.tag = "This is a polygon"

        addLabel(polygon)

        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(LatLng(34.046324, 71.556191), 14f)
        mMap.animateCamera(cameraUpdate)
    }

    override fun onPolygonClick(polygon: Polygon) {
        Toast.makeText(this, polygon.tag.toString(), Toast.LENGTH_LONG).show()
    }

    private fun addLabel(polygon: Polygon) {

        val bitmap = iconGenerator!!.makeIcon(polygon.tag.toString())
        customIcon = BitmapDescriptorFactory.fromBitmap(bitmap)

        mMap.addMarker(
            MarkerOptions()
              .visible(true)
              .position(getPolygonCentroid(polygon))
              .icon(customIcon)
        )
    }

    //This function will return center point of the polygon
    private fun getPolygonCentroid(polygon : Polygon) : LatLng {

        val latitudeList: ArrayList<Double>  = ArrayList()
        val longitudeList: ArrayList<Double>  = ArrayList()

        for (i in 0..polygon.points.size-1){
            latitudeList.add(polygon.points.get(i).latitude)
            longitudeList.add(polygon.points.get(i).longitude)
        }

        val minLatitude = Collections.min(latitudeList)
        val minLongitude = Collections.min(longitudeList)

        val maxLatitude = Collections.max(latitudeList)
        val maxLongitude = Collections.max(longitudeList)

        val bounds = LatLngBounds(
            LatLng(minLatitude, minLongitude),
            LatLng(maxLatitude, maxLongitude)
        )
        return bounds.center
    }
}