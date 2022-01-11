package com.daily.fleet.tracking.activities

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.daily.fleet.tracking.R
import com.daily.fleet.tracking.databinding.MapBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

abstract class BaseMapActivity : FragmentActivity(), OnMapReadyCallback {
    protected var map: GoogleMap? = null
    var binding: MapBinding? = null
    private var mIsRestore = false
    protected val layoutId: Int
        protected get() = R.layout.map

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mIsRestore = savedInstanceState != null
        binding = DataBindingUtil.setContentView(this@BaseMapActivity, layoutId)
        setUpMap()
    }

    override fun onMapReady(map: GoogleMap) {
        if (this.map != null) {
            return
        }
        this.map = map
        startDemo(mIsRestore)
    }

    private fun setUpMap() {
        (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)!!.getMapAsync(
            this
        )
    }

    /**
     * Run the demo-specific code.
     */
    protected abstract fun startDemo(isRestore: Boolean)
}

