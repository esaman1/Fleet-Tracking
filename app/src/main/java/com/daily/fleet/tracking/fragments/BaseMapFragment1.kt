package com.daily.fleet.tracking.fragments

import androidx.fragment.app.Fragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback

abstract class BaseMapFragment1 : Fragment(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null
    private var mIsRestore = false

    override fun onMapReady(map: GoogleMap) {
        if (mMap != null) {
            return
        }
        mMap = map
        startDemo(mIsRestore)
    }

    protected abstract fun startDemo(isRestore: Boolean)

    protected open fun getMap(): GoogleMap? {
        return mMap
    }

}