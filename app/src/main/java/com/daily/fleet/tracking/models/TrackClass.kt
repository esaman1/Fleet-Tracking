package com.daily.fleet.tracking.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TrackClass(

    @field:SerializedName("valid")
    val valid: Boolean? = null,

    @field:SerializedName("variables")
    val variables: Variables? = null,

    @field:SerializedName("utc")
    val utc: String? = null,

    @field:SerializedName("trackInfoId")
    val trackInfoId: Int? = null,

    @field:SerializedName("serverUtc")
    val serverUtc: String? = null,

    @field:SerializedName("position")
    val position: Position? = null,

    @field:SerializedName("velocity")
    val velocity: Velocity? = null
) : Serializable

data class Position(

    @field:SerializedName("altitude")
    val altitude: Double? = null,

    @field:SerializedName("latitude")
    val latitude: Double? = null,

    @field:SerializedName("longitude")
    val longitude: Double? = null
) : Serializable

data class Velocity(

    @field:SerializedName("heading")
    val heading: Double? = null,

    @field:SerializedName("groundSpeed")
    val groundSpeed: Double? = null
) : Serializable

data class Variables(

    @field:SerializedName("idling")
    val idling: Boolean? = null,

    @field:SerializedName("satelliteCount")
    val satelliteCount: Double? = null,

    @field:SerializedName("mnc")
    val mnc: Double? = null,

    @field:SerializedName("battery Voltage")
    val batteryVoltage: Double? = null,

    @field:SerializedName("gsmSignalLevel")
    val gsmSignalLevel: Double? = null,

    @field:SerializedName("mcc")
    val mcc: Double? = null,

    @field:SerializedName("cellID")
    val cellID: Double? = null,

    @field:SerializedName("ignition")
    val ignition: Boolean? = null,

    @field:SerializedName("speed")
    val speed: Double? = null,

    @field:SerializedName("lac")
    val lac: Double? = null
) : Serializable
