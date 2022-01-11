package com.daily.fleet.tracking

import com.daily.fleet.tracking.models.VehicleView

interface RerouteInterface {
    fun onClick(vehicleClass: VehicleView)
}