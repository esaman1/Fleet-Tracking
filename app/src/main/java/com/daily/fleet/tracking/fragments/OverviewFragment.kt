package com.daily.fleet.tracking.fragments

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.daily.fleet.tracking.R
import com.daily.fleet.tracking.databinding.FragmentOverviewBinding

class OverviewFragment : Fragment() {

    companion object {
        var binding: FragmentOverviewBinding? = null
        var mActivity: Activity? = null
    }

    private var refreshReceiver: RefreshReceiver? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_overview, container, false)
        mActivity = requireActivity()
        refreshReceiver = RefreshReceiver()
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            refreshReceiver!!,
            IntentFilter("VEHICLE_OVERVIEW")
        )
        return binding?.root
    }

    private class RefreshReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            val model = MapFragment.selectedVehicleModel
            binding?.tv11?.text = " "
            binding?.tv22?.text = " "
            binding?.tv33?.text = model?.status
            binding?.tv44?.text = " "
            binding?.tv55?.text = " "
            binding?.tv66?.text = " "
            binding?.tv77?.text = model?.heading.toString()
            if (model?.ignition == true) {
                binding?.tv88?.text = mActivity?.resources?.getString(R.string.on)
            } else {
                binding?.tv88?.text = mActivity?.resources?.getString(R.string.off)
            }
            binding?.tv99?.text = model?.odometer.toString()
            binding?.tv00?.text = model?.speed.toString()
        }
    }
}