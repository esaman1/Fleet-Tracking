package com.daily.fleet.tracking.fragments

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.daily.fleet.tracking.R
import com.daily.fleet.tracking.RerouteInterface
import com.daily.fleet.tracking.activities.HomeActivity
import com.daily.fleet.tracking.adapters.VehicleAdapter
import com.daily.fleet.tracking.adapters.VehicleAdapter.Companion.arrayFilterList
import com.daily.fleet.tracking.databinding.FragmentVehicleBinding
import com.daily.fleet.tracking.fragments.MapFragment.Companion.fromVehicle
import com.daily.fleet.tracking.fragments.MapFragment.Companion.locate
import com.daily.fleet.tracking.models.VehicleView

class VehicleFragment : Fragment(), RerouteInterface {

    private var refreshReceiver: RefreshReceiver? = null

    companion object {
        var vehicleAdapter: VehicleAdapter? = null
        var binding: FragmentVehicleBinding? = null
        var mActivity: Activity? = null

        fun setData() {
            vehicleAdapter?.newArray()
            if (MapFragment.mainArrayList.size > 0) {
                binding?.noData?.isVisible = false
                binding?.mRecyclerView?.isVisible = true
                for (i in 0 until MapFragment.mainArrayList.size) {
                    vehicleAdapter!!.add(MapFragment.mainArrayList[i])
                }
                binding?.lottie?.isVisible = false
                vehicleAdapter?.filter?.filter("")
                if (MapFragment.mainArrayList.size > 1)
                    binding?.mTotal?.text = "${MapFragment.mainArrayList.size} Results"
                else
                    binding?.mTotal?.text = "${MapFragment.mainArrayList.size} Result"

            } else {
                binding?.noData?.isVisible = true
                binding?.mRecyclerView?.isVisible = false
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_vehicle, container, false)
        mActivity = requireActivity()

        refreshReceiver = RefreshReceiver()
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            refreshReceiver!!,
            IntentFilter("VEHICLE_ALL")
        )

        binding?.mMenu?.setOnClickListener {
            Log.e("map_menu", "clicked")
            if (HomeActivity.binding?.mDrawerLayout?.isDrawerOpen(Gravity.LEFT) == true) {
                HomeActivity.binding?.mDrawerLayout?.closeDrawer(Gravity.LEFT)
            } else {
                HomeActivity.binding?.mDrawerLayout?.openDrawer(Gravity.LEFT)
            }
        }

        vehicleAdapter = VehicleAdapter(requireContext(), this)
        binding?.mRecyclerView?.layoutManager = LinearLayoutManager(
            requireActivity(),
            LinearLayoutManager.VERTICAL, false
        )
        binding?.mRecyclerView?.adapter = vehicleAdapter

        binding?.mSearchET?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                vehicleAdapter?.filter?.filter(s)

            }

            override fun afterTextChanged(s: Editable) {
                if (arrayFilterList.size > 1)
                    binding?.mTotal?.text = "${arrayFilterList.size} Results"
                else
                    binding?.mTotal?.text = "${arrayFilterList.size} Result"
            }
        })

        binding?.lottie?.isVisible = true
        return binding?.root
    }

    private class RefreshReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            mActivity?.runOnUiThread { setData() }
        }
    }

    override fun onClick(vehicleClass: VehicleView) {
        HomeActivity.binding?.viewPager?.currentItem = 0
//        vehicleClass.vehiclenumber?.let { Log.e("searchVehicle:", it) }
        fromVehicle = vehicleClass
        locate = true
        MapFragment.binding?.lottie?.isVisible = true
    }

}