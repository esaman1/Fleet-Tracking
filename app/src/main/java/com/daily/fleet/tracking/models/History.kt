package com.daily.fleet.tracking.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

open class History(

    @field:SerializedName("totalDuration")
    val totalDuration: Double? = null,

    @field:SerializedName("trackInfoId")
    val trackInfoId: Int? = null,

    @field:SerializedName("startTrackPoint")
    val startTrackPoint: StartTrackPoint? = null,

    @field:SerializedName("isIdle")
    val isIdle: Boolean? = null,

    @field:SerializedName("endTrackPoint")
    val endTrackPoint: EndTrackPoint? = null,

    @field:SerializedName("totalDistance")
    var totalDistance: Double? = null,

    @field:SerializedName("trackPoints")
    val trackPoints: List<TrackClass>? = null,

    @field:SerializedName("userId")
    val userId: Int? = null
) : Serializable

open class StartTrackPoint(

    @field:SerializedName("valid")
    val valid: Boolean? = null,

    @field:SerializedName("address")
    val address: String? = null,

    @field:SerializedName("utc")
    val utc: String? = null,

    @field:SerializedName("ist")
    val ist: String? = null,

    @field:SerializedName("position")
    val position: Position? = null,

    @field:SerializedName("velocity")
    val velocity: Velocity? = null
) : Serializable

open class EndTrackPoint(

    @field:SerializedName("valid")
    val valid: Boolean? = null,

    @field:SerializedName("address")
    val address: String? = null,

    @field:SerializedName("utc")
    val utc: String? = null,

    @field:SerializedName("ist")
    val ist: String? = null,

    @field:SerializedName("position")
    val position: Position? = null,

    @field:SerializedName("velocity")
    val velocity: Velocity? = null
) : Serializable
