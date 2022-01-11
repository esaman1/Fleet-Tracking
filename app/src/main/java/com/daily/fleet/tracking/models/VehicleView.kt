package com.daily.fleet.tracking.models

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import com.google.maps.android.clustering.ClusterItem
import java.io.Serializable


open class VehicleView(

    @field:SerializedName("lastseen")
    val lastseen: String? = null,

    @field:SerializedName("vehiclenumber")
    val vehiclenumber: String? = null,

    @field:SerializedName("odometer")
    val odometer: Double? = null,

    @field:SerializedName("heading")
    val heading: Double? = null,

    @field:SerializedName("latitude")
    val latitude: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("vehicleid")
    val vehicleid: Int? = null,

    @field:SerializedName("ignition")
    val ignition: Boolean? = null,

    @field:SerializedName("speed")
    val speed: Double? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("longitude")
    val longitude: String? = null
) : ClusterItem, Serializable {
    override fun getPosition(): LatLng {
        return latitude?.let { longitude?.let { it1 -> LatLng(it.toDouble(), it1.toDouble()) } }!!
    }

    override fun getTitle(): String? {
        return vehiclenumber
    }

    override fun getSnippet(): String? {
        return "Last seen : " + lastseen
    }
}

