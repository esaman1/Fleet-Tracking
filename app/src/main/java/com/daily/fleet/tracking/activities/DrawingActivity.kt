package com.daily.fleet.tracking.activities


import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import com.daily.fleet.tracking.R
import com.daily.fleet.tracking.dataClass.Utils
import com.daily.fleet.tracking.models.History
import com.daily.fleet.tracking.models.Position
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class DrawingActivity : BaseMapActivity() {
    var routes: ArrayList<ArrayList<HashMap<String, String>>> = ArrayList()
    var mMarkerPoints: ArrayList<LatLng>? = null
    var historyClass: History? = null
    var trackPoints: ArrayList<Position> = ArrayList()
    var timeArray: ArrayList<String> = ArrayList()
    private var mOrigin: LatLng? = null
    private var mDestination: LatLng? = null
    private var lastposition: Int? = null
    private var mPolyline: Polyline? = null
    var firstDraw: Boolean? = false

    override fun startDemo(isRestore: Boolean) {
        routes.clear()
        mMarkerPoints = java.util.ArrayList()
        firstDraw = true
        binding?.mSeekbar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(s: SeekBar?) {
                if (mMarkerPoints!!.size > 3) {
                    if (lastposition != null) {
                        Log.e("lastposition", lastposition.toString())
                        trackPoints[lastposition!!].latitude?.let {
                            trackPoints[lastposition!!].longitude?.let { it1 ->
                                LatLng(
                                    it,
                                    it1
                                )
                            }
                        }
                            ?.let { mMarkerPoints!!.remove(it) }

                    }
                }
                lastposition = s?.progress
                drawPoints()
            }

        })

        historyClass = intent.getSerializableExtra("historyClass") as History
        Log.e("Trackpoints_size :-", "${historyClass!!.trackPoints?.size}")
        if (historyClass!!.trackPoints?.size!! > 0) {
            trackPoints.clear()
            val path: ArrayList<HashMap<String, String>> =
                java.util.ArrayList<HashMap<String, String>>()

            binding?.mSeekbar?.max = (historyClass!!.trackPoints?.size!!).minus(1)

            for (i in 0 until historyClass!!.trackPoints?.size!!) {
                val pointsModel = historyClass!!.trackPoints?.get(i)?.position
                if (pointsModel != null) {
                    trackPoints.add(pointsModel)
                    timeArray.add(historyClass!!.trackPoints?.get(i)?.utc.toString())
                }

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
                }
                path.add(hm)
            }
            routes.add(path)
        }
        lastposition = 0
        drawPoints()
    }

    fun drawPoints() {
        if (mMarkerPoints!!.size > 1) {
            mMarkerPoints!!.clear()
            map?.clear()
        }

        drawSource(0, "S")
        drawSource(trackPoints.size - 1, "D")
        lastposition?.let { drawSource(it, "C") }

        drawRoute()

//        binding?.tvTime?.text = Utils.fetchTime(timeArray[lastposition!!])
        binding?.tvTime?.text = setDateFormat(timeArray[lastposition!!])
        binding?.tvLocation?.text = Utils.getAddress(
            trackPoints[lastposition!!].latitude.toString(),
            trackPoints[lastposition!!].longitude.toString()
        )
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(ParseException::class)
    fun setDateFormat(unformattedDate: String?): String? {
        @SuppressLint("SimpleDateFormat") val dateformat =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(unformattedDate)
        return SimpleDateFormat("hh:mm a").format(dateformat)
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
            mPolyline?.remove()
            mPolyline = map?.addPolyline(lineOptions)

            if (firstDraw == true) {
                firstDraw = false
                trackPoints[lastposition!!].latitude?.toDouble()?.let {
                    trackPoints[lastposition!!].longitude?.toDouble()
                        ?.let { it1 -> LatLng(it, it1) }
                }
                    ?.let {
                        CameraUpdateFactory.newLatLngZoom(
                            it,
                            7f
                        )
                    }?.let {
                        map?.moveCamera(
                            it
                        )
                    }
            }
        } else Toast.makeText(applicationContext, "No route found", Toast.LENGTH_LONG).show()
    }

    fun drawSource(pos: Int, from: String) {
        trackPoints[pos].latitude?.let {
            trackPoints[pos].longitude?.let { it1 ->
                LatLng(
                    it,
                    it1
                )
            }
        }
            ?.let { mMarkerPoints!!.add(it) }

        var circleDrawable: Drawable? = null

        when (from) {
            "S" -> {
                circleDrawable = resources.getDrawable(R.drawable.ic_track_source)
            }
            "D" -> {
                circleDrawable = resources.getDrawable(R.drawable.ic_track_desination)
            }
            "C" -> {
                circleDrawable = resources.getDrawable(R.drawable.ic_track_current)
            }
        }
        val markerIcon: BitmapDescriptor? =
            circleDrawable?.let { Utils.getMarkerIconFromDrawable(it) }

        val source = Utils.getAddress(
            trackPoints[pos].latitude.toString(),
            trackPoints[pos].longitude.toString()
        )
        trackPoints[pos].latitude?.let {
            trackPoints[pos].longitude?.let { it1 ->
                LatLng(
                    it,
                    it1
                )
            }
        }
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
    }


}