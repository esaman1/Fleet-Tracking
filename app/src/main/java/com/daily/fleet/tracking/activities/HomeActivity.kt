package com.daily.fleet.tracking.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import com.daily.fleet.tracking.R
import com.daily.fleet.tracking.adapters.LibraryPagerAdapter
import com.daily.fleet.tracking.adapters.ViewPagerAdapter
import com.daily.fleet.tracking.databinding.ActivityHomeBinding
import com.daily.fleet.tracking.databinding.VehicleInfoBinding
import com.daily.fleet.tracking.fragments.HistoryFragment
import com.daily.fleet.tracking.fragments.MapFragment
import com.daily.fleet.tracking.fragments.OverviewFragment
import com.daily.fleet.tracking.fragments.VehicleFragment
import com.daily.fleet.tracking.models.VehicleView
import com.daily.motivational.quotes2.dataClass.SharedPreference
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.vehicle_info.*
import java.util.*


class HomeActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener,
    NavigationView.OnNavigationItemSelectedListener {

    var mViewPagerAdapter: LibraryPagerAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@HomeActivity, R.layout.activity_home)

        mActivity = this@HomeActivity
        context = baseContext
        token = mActivity?.baseContext?.let { SharedPreference.getLogin(it) }
        binding?.bottomNavigationView?.setOnNavigationItemSelectedListener(this)
        mViewPagerAdapter = LibraryPagerAdapter(supportFragmentManager)
        binding?.viewPager?.adapter = mViewPagerAdapter
        binding?.viewPager?.offscreenPageLimit = 2

        binding?.viewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(i: Int, v: Float, i1: Int) {}
            override fun onPageSelected(i: Int) {
                binding?.bottomNavigationView?.menu?.getItem(i)?.isChecked = true
            }

            override fun onPageScrollStateChanged(i: Int) {}
        })
        binding?.viewPager?.let { setupMainViewPager(it) }

        vehicleInfoBehaviour =
            BottomSheetBehavior.from(llBottom)
        vehicleInfoBehaviour.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    binding?.hideBack?.isVisible = false
                    MapFragment.binding?.mainCard?.isVisible = true
                }
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    binding?.hideBack?.isVisible = true
                    MapFragment.binding?.mainCard?.isVisible = false
                }

            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })

        setupViewPager(tab_viewpager)
        tab_tablayout.setupWithViewPager(tab_viewpager)

        binding?.navigationView?.menu!!.findItem(R.id.m2)
            .setOnMenuItemClickListener { menuItem: MenuItem? ->
                binding?.viewPager?.currentItem = 1
                binding?.mDrawerLayout?.closeDrawer(Gravity.LEFT)
                true
            }
        binding?.navigationView?.menu!!.findItem(R.id.m3)
            .setOnMenuItemClickListener { menuItem: MenuItem? ->
                binding?.viewPager?.currentItem = 0
                binding?.mDrawerLayout?.closeDrawer(Gravity.LEFT)
                true
            }
        binding?.navigationView?.menu!!.findItem(R.id.m4)
            .setOnMenuItemClickListener { menuItem: MenuItem? ->
                SharedPreference.setLogin(
                    this@HomeActivity,
                    "null"
                )
                binding?.mDrawerLayout?.closeDrawer(Gravity.LEFT)
                var intent = Intent(this@HomeActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
                true
            }
    }

    private fun setupMainViewPager(viewPager: ViewPager) {
        val adapter = LibraryPagerAdapter(supportFragmentManager)
        val mapFragment = MapFragment()
        val vehicleFragment = VehicleFragment()
        adapter.addFragment(mapFragment)
        adapter.addFragment(vehicleFragment)
        viewPager.adapter = adapter
    }

    private fun setupViewPager(viewpager: ViewPager) {
        var adapter = ViewPagerAdapter(supportFragmentManager)

        adapter.addFragment(OverviewFragment(), "Overview")
        adapter.addFragment(HistoryFragment(), "History")

        viewpager.adapter = adapter
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.map -> {
                binding?.viewPager?.currentItem = 0
            }
            R.id.vehicle -> {
                binding?.viewPager?.currentItem = 1
            }
        }
        return false
    }

    override fun onBackPressed() {

        if (vehicleInfoBehaviour.state == BottomSheetBehavior.STATE_EXPANDED) {
            vehicleInfoBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
            binding?.hideBack?.isVisible = false
            MapFragment.binding?.mainCard?.isVisible = true
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        var token: String? = null
        lateinit var vehicleInfoBehaviour: BottomSheetBehavior<LinearLayout>
        var mActivity: Activity? = null
        var context: Context? = null
        var binding: ActivityHomeBinding? = null

        fun initData(mVehicle: VehicleView) {
            val includedView: VehicleInfoBinding? = binding?.llMain
            mVehicle.vehiclenumber?.let { Log.e("home", it) }

            when (mVehicle.status) {
                mActivity?.resources?.getString(R.string.moving) -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        includedView?.mVehicleImg1?.backgroundTintList =
                            context?.let { ColorStateList.valueOf(it.getColor(R.color.green)) }
                    }

                }
                context?.resources?.getString(R.string.idling) -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        includedView?.mVehicleImg1?.backgroundTintList =
                            context?.getColor(R.color.blue)?.let { ColorStateList.valueOf(it) }
                    }

                }
                context?.resources?.getString(R.string.parked) -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        includedView?.mVehicleImg1?.backgroundTintList =
                            context?.getColor(R.color.orange)?.let { ColorStateList.valueOf(it) }
                    }

                }
                context?.resources?.getString(R.string.offline) -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        includedView?.mVehicleImg1?.backgroundTintList =
                            context?.getColor(R.color.grey)?.let { ColorStateList.valueOf(it) }
                    }

                }
                context?.resources?.getString(R.string.pending) -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        includedView?.mVehicleImg1?.backgroundTintList =
                            context?.getColor(R.color.yellow)?.let { ColorStateList.valueOf(it) }
                    }
                }
            }


            includedView?.mVehiclenumber1?.text = mVehicle.vehiclenumber
            includedView?.mLastseen1?.text = mVehicle.lastseen

            val addresses: List<Address>
            val geocoder = Geocoder(mActivity, Locale.getDefault())
            addresses = mVehicle.latitude?.let {
                mVehicle.longitude?.let { it1 ->
                    geocoder.getFromLocation(
                        it.toDouble(),
                        it1.toDouble(),
                        1
                    )
                }
            } as List<Address>
            val address: String =
                addresses[0].getAddressLine(0)
            includedView?.mRoot1?.text = address
        }
    }

}