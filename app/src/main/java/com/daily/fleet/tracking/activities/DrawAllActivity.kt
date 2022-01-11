package com.daily.fleet.tracking.activities

import android.util.Log
import android.widget.Toast
import com.daily.fleet.tracking.R
import com.daily.fleet.tracking.dataClass.Utils
import com.daily.fleet.tracking.models.History
import com.daily.fleet.tracking.models.Position
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.set

class DrawAllActivity : BaseMapActivity() {
    var routes: ArrayList<ArrayList<HashMap<String, String>>> = ArrayList()
    var mMarkerPoints: ArrayList<LatLng>? = null
    var historyArray: ArrayList<History>? = ArrayList<History>()
    var trackPoints: ArrayList<Position> = ArrayList()
    private var mOrigin: LatLng? = null
    private var mDestination: LatLng? = null
    private var mPolyline: Polyline? = null

    override fun startDemo(isRestore: Boolean) {
        mMarkerPoints = java.util.ArrayList()
        val map: GoogleMap? = map

        historyArray = intent.getSerializableExtra("historyList") as ArrayList<History>
        for (i in 0 until historyArray!!.size) {
            var historyClass = historyArray!![i]
            Log.e("Trackpoints_size :-", "${historyClass.trackPoints?.size}")
            if (historyClass.trackPoints?.size!! > 0) {
                trackPoints.clear()
                val path: ArrayList<HashMap<String, String>> =
                    java.util.ArrayList<HashMap<String, String>>()
                for (i in 0 until historyClass.trackPoints?.size!!) {

                    val pointsModel = historyClass.trackPoints?.get(i)?.position
                    pointsModel?.let { trackPoints.add(it) }
                    val hm = HashMap<String, String>()
                    if (pointsModel != null) {
                        hm["lat"] =
                            pointsModel.latitude?.let {
                                java.lang.Double.toString(
                                    it
                                )
                            }.toString()
                        hm["lng"] =
                            pointsModel.longitude?.let {
                                java.lang.Double.toString(
                                    it
                                )
                            }.toString()
                        path.add(hm)
                    }
                    routes.add(path)
                }
                drawPoints()
            }
        }
    }

    fun drawPoints() {
        trackPoints[0].latitude?.let {
            trackPoints[0].longitude?.let { it1 ->
                LatLng(
                    it,
                    it1
                )
            }
        }
            ?.let { mMarkerPoints!!.add(it) }

        trackPoints[trackPoints.size - 1].latitude?.let {
            trackPoints[trackPoints.size - 1].longitude?.let { it1 ->
                LatLng(
                    it,
                    it1
                )
            }
        }
            ?.let { mMarkerPoints!!.add(it) }

        val circleDrawable = resources.getDrawable(R.drawable.ic_track_current)
        val markerIcon: BitmapDescriptor = Utils.getMarkerIconFromDrawable(circleDrawable)

        val source = Utils.getAddress(
            trackPoints[0].latitude.toString(),
            trackPoints[0].longitude.toString()
        )
        trackPoints[0].latitude?.let { trackPoints[0].longitude?.let { it1 -> LatLng(it, it1) } }
            ?.let {
                MarkerOptions()
                    .position(it)
                    .title(source)
                    .icon(markerIcon)
            }?.let {
                map?.addMarker(
                    it
                )
            }

        val circleDrawable1 = resources.getDrawable(R.drawable.ic_track_source)
        val markerIcon1: BitmapDescriptor = Utils.getMarkerIconFromDrawable(circleDrawable1)
        val dest = Utils.getAddress(
            trackPoints[trackPoints.size - 1].latitude.toString(),
            trackPoints[trackPoints.size - 1].longitude.toString()
        )
        trackPoints[trackPoints.size - 1].latitude?.let {
            trackPoints[trackPoints.size - 1].longitude?.let { it1 ->
                LatLng(
                    it,
                    it1
                )
            }
        }?.let {
            MarkerOptions()
                .position(it)
                .title(dest)
                .icon(markerIcon1)
        }?.let {
            map?.addMarker(
                it
            )
        }
        if (mMarkerPoints!!.size >= 2) {
            mOrigin = mMarkerPoints!![0]
            mDestination = mMarkerPoints!![1]
            drawRoute()
        }
    }


    fun drawRoute() {
        var points: java.util.ArrayList<LatLng?>? = null
        var lineOptions: PolylineOptions? = null

        for (i in routes.indices) {
            points = java.util.ArrayList()
            lineOptions = PolylineOptions()

            // Fetching i-th route
            val path: List<HashMap<String, String>> = routes.get(i)

            // Fetching all the points in i-th route
            for (j in path.indices) {
                val point = path[j]
                val lat = point["lat"]!!.toDouble()
                val lng = point["lng"]!!.toDouble()
                val position = LatLng(lat, lng)
                points.add(position)
            }

            // Adding all the points in the route to LineOptions
            lineOptions.addAll(points)
            lineOptions.width(15f)
            lineOptions.color(resources.getColor(R.color.track_color))
        }

        if (lineOptions != null) {
//            mPolyline?.remove()
            mPolyline = map?.addPolyline(lineOptions)

            trackPoints[0].latitude?.toDouble()?.let {
                trackPoints[0].longitude?.toDouble()
                    ?.let { it1 -> LatLng(it, it1) }
            }
                ?.let {
                    CameraUpdateFactory.newLatLngZoom(
                        it,
                        9f
                    )
                }?.let {
                    map?.moveCamera(
                        it
                    )
                }

        } else Toast.makeText(applicationContext, "No route found", Toast.LENGTH_LONG).show()
    }

}