package com.daily.fleet.tracking.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView.OnEditorActionListener
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.daily.fleet.tracking.R
import com.daily.fleet.tracking.activities.HomeActivity
import com.daily.fleet.tracking.activities.HomeActivity.Companion.token
import com.daily.fleet.tracking.dataClass.Utils
import com.daily.fleet.tracking.databinding.FragmentMapBinding
import com.daily.fleet.tracking.models.VehicleView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ui.IconGenerator
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.map.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class MapFragment : BaseMapFragment1(), View.OnClickListener,
    ClusterManager.OnClusterClickListener<VehicleView>,
    ClusterManager.OnClusterInfoWindowClickListener<VehicleView>,
    ClusterManager.OnClusterItemClickListener<VehicleView>,
    ClusterManager.OnClusterItemInfoWindowClickListener<VehicleView> {


    companion object {
        var clusterManager: ClusterManager<VehicleView>? = null
        var locate: Boolean = false
        var searching: Boolean = false
        var searchLocate: Boolean = false
        var serachedString: String? = null
        var mMap: GoogleMap? = null

        var binding: FragmentMapBinding? = null
        var mActivity: Activity? = null
        var selectedVehicleModel: VehicleView? = null
        var mainArrayList = ArrayList<VehicleView>()
        var mContext: Context? = null
        var from: String? = null
        var fromVehicle: VehicleView? = null

        fun initData(mVehicle: VehicleView) {
            binding?.mainCard?.isVisible = true
            HomeActivity.initData(mVehicle)

            val lbm = mActivity?.baseContext?.let { LocalBroadcastManager.getInstance(it) }
            val localIn = Intent("VEHICLE_OVERVIEW")
            localIn.putExtra("vehicleModel", mVehicle)
            lbm?.sendBroadcast(localIn)

            val lbm1 = mActivity?.baseContext?.let { LocalBroadcastManager.getInstance(it) }
            val localIn1 = Intent("VEHICLE_HISTORY")
            localIn1.putExtra("vehicleModel", mVehicle)
            lbm1?.sendBroadcast(localIn1)

            mVehicle.status?.let { Log.e("Status", it) }

            when (mVehicle.status) {
                mContext?.resources?.getString(R.string.moving) -> {
                    binding?.mVehicleImg?.backgroundTintList =
                        ColorStateList.valueOf(mContext?.resources?.getColor(R.color.green)!!)

                }
                mContext?.resources?.getString(R.string.idling) -> {
                    binding?.mVehicleImg?.backgroundTintList =
                        ColorStateList.valueOf(mContext?.resources?.getColor(R.color.blue)!!)

                }
                mContext?.resources?.getString(R.string.parked) -> {
                    binding?.mVehicleImg?.backgroundTintList =
                        ColorStateList.valueOf(mContext?.resources?.getColor(R.color.orange)!!)

                }
                mContext?.resources?.getString(R.string.offline) -> {
                    binding?.mVehicleImg?.backgroundTintList =
                        ColorStateList.valueOf(mContext?.resources?.getColor(R.color.grey)!!)

                }
                mContext?.resources?.getString(R.string.pending) -> {
                    binding?.mVehicleImg?.backgroundTintList =
                        ColorStateList.valueOf(mContext?.resources?.getColor(R.color.yellow)!!)
                }
            }

            binding?.mVehiclenumber?.text = mVehicle.vehiclenumber
            binding?.mLastseen?.text = mVehicle.lastseen

            binding?.mRoot?.text = mVehicle.latitude?.let {
                mVehicle.longitude?.let { it1 ->
                    Utils.getAddress(
                        it,
                        it1
                    )
                }
            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false)
        mActivity = requireActivity()

        setUpMap()
        binding?.mainCard?.setOnClickListener(this)
        binding?.mainCard?.isVisible = false
        mContext = requireContext()

        binding?.mMenu?.setOnClickListener {
            Log.e("map_menu", "clicked")
            if (HomeActivity.binding?.mDrawerLayout?.isDrawerOpen(Gravity.LEFT) == true) {
                HomeActivity.binding?.mDrawerLayout?.closeDrawer(Gravity.LEFT)
            } else {
                HomeActivity.binding?.mDrawerLayout?.openDrawer(Gravity.LEFT)
            }
        }

        binding?.mSearchET?.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                serachedString = binding?.mSearchET?.text.toString()
                Log.e("searched", serachedString!!)
                if (!serachedString.isNullOrEmpty()) {
//                    getSearchedVehicle(serachedString!!)
                    binding?.lottie?.isVisible = true
                    from = "M"
                    val parserTask =
                        getMap()?.let { GetSearchedVehiclesTask(it, serachedString!!, from!!) }
                    parserTask?.execute()
                }
                val imm: InputMethodManager =
                    v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                return@OnEditorActionListener true
            }
            false
        })

        binding?.mSearchET?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                Log.e("searched", s.toString())
                if (s.isNullOrEmpty()) {
//                    getAllVehicles()
                    binding?.lottie?.isVisible = true
                    val parserTask = getMap()?.let { GetAllVehiclesTask(it) }
                    parserTask?.execute()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searching = true
                searchLocate = true
                if (s.isNullOrEmpty()) {
                    searchLocate = false
                }
            }
        })
        return binding?.root
    }

    fun setUpMap() {
        mMap = getMap()
        locate = true
        (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync(
            this
        )
    }

    override fun onClusterClick(cluster: Cluster<VehicleView>): Boolean {
        val title: String? = cluster.items.iterator().next().title
//        Toast.makeText(
//            mActivity,
//            cluster.size.toString() + " (including " + title + ")",
//            Toast.LENGTH_SHORT
//        ).show()

        val builder = LatLngBounds.builder()
        for (item in cluster.items) {
            builder.include(item.position)
        }

        try {
            getMap()?.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return true
    }

    override fun onClusterInfoWindowClick(cluster: Cluster<VehicleView>?) {
    }

    override fun onClusterItemClick(item: VehicleView?): Boolean {
        if (item != null) {
//            Log.e("clicked_id", item.vehicleid.toString())
            selectedVehicleModel = item
            initData(selectedVehicleModel!!)
        }
        return false
    }

    override fun onClusterItemInfoWindowClick(item: VehicleView?) {
        if (item != null) {
//            Log.e("clicked_id_window", item.vehicleid.toString())
            selectedVehicleModel = item
            initData(selectedVehicleModel!!)
        }
    }

    private class ZoomBasedRenderer(
        context: Context?,
        activity: Activity?,
        map1: GoogleMap?,
        clusterManager: ClusterManager<VehicleView>
    ) :
        DefaultClusterRenderer<VehicleView>(context, map1, clusterManager),
        OnCameraIdleListener {
        private var zoom = 15f
        private var oldZoom: Float? = null
        var map: GoogleMap? = null
        var context: Context? = null
        var mActivity: Activity? = null
        var mImageView: ImageView? = null
        private val mClusterImageView: ImageView
        private val mIconGenerator = IconGenerator(context)
        private val mClusterIconGenerator = IconGenerator(context)
        private val mDimension: Int

        override fun onBeforeClusterItemRendered(
            vehicle: VehicleView,
            markerOptions: MarkerOptions
        ) {
            markerOptions
                .icon(getItemIcon(vehicle))
                .title(vehicle.vehiclenumber)
        }

        override fun onClusterItemUpdated(vehicle: VehicleView, marker: Marker) {
            marker.setIcon(getItemIcon(vehicle))
            marker.title = vehicle.vehiclenumber
        }

        override fun onBeforeClusterRendered(
            cluster: Cluster<VehicleView?>,
            markerOptions: MarkerOptions
        ) {
            mClusterIconGenerator.setBackground(null)
            markerOptions.icon(getClusterIcon(cluster))
        }

        override fun onClusterUpdated(cluster: Cluster<VehicleView?>, marker: Marker) {
            // Same implementation as onBeforeClusterRendered() (to update cached markers)
            mClusterIconGenerator.setBackground(null)
            marker.setIcon(getClusterIcon(cluster))
        }

        private fun getClusterIcon(cluster: Cluster<VehicleView?>): BitmapDescriptor? {

            mClusterImageView.setImageDrawable(context?.resources?.getDrawable(R.drawable.ic_cluster_bg))
            val icon = mClusterIconGenerator.makeIcon(cluster.size.toString())
            return BitmapDescriptorFactory.fromBitmap(icon)
        }

        private fun getItemIcon(vehicle: VehicleView): BitmapDescriptor {
            when (vehicle.status) {
                context?.resources?.getString(R.string.moving) -> {
                    mImageView?.setImageDrawable(context?.resources?.getDrawable(R.drawable.ic_moving_circle))
                }
                context?.resources?.getString(R.string.idling) -> {
                    mImageView?.setImageDrawable(context?.resources?.getDrawable(R.drawable.ic_idle_circle))
                }
                context?.resources?.getString(R.string.parked) -> {
                    mImageView?.setImageDrawable(context?.resources?.getDrawable(R.drawable.ic_parked_circle))
                }
                context?.resources?.getString(R.string.offline) -> {
                    mImageView?.setImageDrawable(context?.resources?.getDrawable(R.drawable.ic_offline_circle))
                }
                context?.resources?.getString(R.string.pending) -> {
                    mImageView?.setImageDrawable(context?.resources?.getDrawable(R.drawable.ic_pending_circle))
                }
            }
            mIconGenerator.setBackground(null)
            val icon = mIconGenerator.makeIcon()
            return BitmapDescriptorFactory.fromBitmap(icon)
        }

        init {
            this.map = map1
            this.context = context
            this.mActivity = activity
            val multiProfile: View =
                mActivity?.layoutInflater?.inflate(R.layout.multi_profile, null)!!

            mClusterIconGenerator.setContentView(multiProfile)
            mClusterImageView = multiProfile.findViewById(R.id.image)
            mImageView = ImageView(context)
            mDimension = (context?.resources?.getDimension(R.dimen.custom_profile_image))?.toInt()!!
            mImageView!!.layoutParams = ViewGroup.LayoutParams(mDimension, mDimension)
            val padding =
                (context.resources?.getDimension(R.dimen.custom_profile_padding))?.toInt()!!
            mImageView!!.setPadding(padding, padding, padding, padding)
            mIconGenerator.setContentView(mImageView)
        }

        override fun onCameraIdle() {
            oldZoom = zoom
            zoom = map?.cameraPosition?.zoom!!
        }

        override fun shouldRenderAsCluster(cluster: Cluster<VehicleView?>): Boolean {
            mClusterImageView.setImageDrawable(context?.resources?.getDrawable(R.drawable.ic_cluster_bg))
            val icon = mClusterIconGenerator.makeIcon(cluster.size.toString())
            return zoom < ZOOM_THRESHOLD
        }

        override fun shouldRender(
            oldClusters: Set<Cluster<VehicleView?>?>,
            newClusters: Set<Cluster<VehicleView?>?>
        ): Boolean {
            return if (crossedZoomThreshold(oldZoom, zoom)) {
                true
            } else {
                super.shouldRender(oldClusters, newClusters)
            }
        }

        private fun crossedZoomThreshold(oldZoom: Float?, newZoom: Float?): Boolean {
            return if (oldZoom == null || newZoom == null) {
                true
            } else oldZoom < ZOOM_THRESHOLD && newZoom > ZOOM_THRESHOLD ||
                    oldZoom > ZOOM_THRESHOLD && newZoom < ZOOM_THRESHOLD
        }

        companion object {
            private const val ZOOM_THRESHOLD = 9f
        }
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mainCard -> {
                binding?.mainCard?.isVisible = false
                HomeActivity.vehicleInfoBehaviour.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    override fun startDemo(isRestore: Boolean) {

        clusterManager = ClusterManager<VehicleView>(mActivity, getMap())

        getMap()?.setOnCameraIdleListener(clusterManager)

        val renderer =
            ZoomBasedRenderer(requireContext(), requireActivity(), getMap(), clusterManager!!)
        clusterManager?.renderer = renderer
        clusterManager?.setOnClusterClickListener(this)
        clusterManager?.setOnClusterInfoWindowClickListener(this)
        clusterManager?.setOnClusterItemClickListener(this)
        clusterManager?.setOnClusterItemInfoWindowClickListener(this)

        val h = Handler()
        h.postDelayed(object : Runnable {
            override fun run() {
                if (serachedString.isNullOrEmpty()) {
//                    Log.e("Update", "All")
                    val parserTask = getMap()?.let { GetAllVehiclesTask(it) }
                    parserTask?.execute()
                } else {
//                    Log.e("Update", "serached")
                    val parserTask =
                        getMap()?.let { GetSearchedVehiclesTask(it, serachedString!!, from!!) }
                    parserTask?.execute()
                }
                h.postDelayed(this, 5000)
            }
        }, 100)
    }


//    *******************

    class GetAllVehiclesTask(map: GoogleMap) :
        AsyncTask<String?, Int?, Boolean>() {

        private var mMap: GoogleMap? = null

        init {
            this.mMap = map
        }

        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)
            if (result == true)
                binding?.lottie?.isVisible = false
        }

        override fun doInBackground(vararg p0: String?): Boolean {
            mainArrayList.clear()
            clusterManager?.clearItems()
            AndroidNetworking.get("https://api.locanix.net/api/vehicles/by/view/")
                .addHeaders("token", token)
                .addQueryParameter("license", "any")
                .addQueryParameter("viewName", "all vehicles")
                .setPriority(Priority.IMMEDIATE)
                .build()
                .getAsJSONObject(object :
                    JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject?) {
                        mainArrayList.clear()
                        if (response != null) {
                            val result = JSONObject(response.toString())
                            if (result["Success"] == true) {
                                val jArray: JSONArray = result.getJSONArray("Data")
                                var movingVehicle: VehicleView? = null
                                for (i in 0 until jArray.length()) {
                                    try {
                                        val `object` = jArray.getJSONObject(i)
                                        val vehicleModel =
                                            Gson().fromJson(
                                                `object`.toString(),
                                                VehicleView::class.java
                                            )

                                        if (vehicleModel.status.equals("Moving")) {
                                            movingVehicle = vehicleModel
                                        }
                                        mainArrayList.add(vehicleModel)
                                        clusterManager?.addItem(vehicleModel)

                                    } catch (e: JSONException) {
                                        e.printStackTrace()
                                        Log.e("Json_error:- ", e.message.toString())
                                    }
                                }
                                if (locate) {
                                    locate = false
                                    if (fromVehicle != null) {
                                        Log.e("fromVehicle", fromVehicle!!.vehiclenumber.toString())
                                        movingVehicle = fromVehicle
                                        initData(fromVehicle!!)
                                    }
                                    movingVehicle?.latitude?.toDouble()?.let {
                                        movingVehicle.longitude?.toDouble()
                                            ?.let { it1 -> LatLng(it, it1) }
                                    }
                                        ?.let {
                                            CameraUpdateFactory.newLatLngZoom(
                                                it,
                                                12f
                                            )
                                        }?.let {
                                            mMap?.moveCamera(
                                                it
                                            )
                                        }
                                    if (!searching) {
                                        val lbm2 = mActivity?.baseContext?.let {
                                            LocalBroadcastManager.getInstance(it)
                                        }
                                        val localIn2 = Intent("VEHICLE_ALL")
                                        lbm2?.sendBroadcast(localIn2)
                                    }
                                }
                                clusterManager?.cluster()
                            }
                        }
//                        Log.e("Vehicle_size :- ", mainArrayList.size.toString())
                    }

                    override fun onError(anError: ANError?) {
                        Log.e("Vehicle_error:- ", anError?.message.toString())
                    }

                })
            return true
        }
    }

    private class GetSearchedVehiclesTask(map: GoogleMap, searchKey: String, from: String) :
        AsyncTask<String?, Int?, Boolean>() {

        private var mMap: GoogleMap? = null
        private var searchKey: String? = null
        private var from: String? = null

        init {
            this.mMap = map
            this.searchKey = searchKey
            this.from = from
        }

        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)
            if (result == true)
                binding?.lottie?.isVisible = false
        }

        override fun doInBackground(vararg p0: String?): Boolean {
            clusterManager?.clearItems()
            AndroidNetworking.get("https://api.locanix.net/api/vehicles/search/")
                .addHeaders("token", token)
                .addQueryParameter("searchKey", searchKey)
                .setPriority(Priority.IMMEDIATE)
                .build()
                .getAsJSONObject(object :
                    JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject?) {
                        mainArrayList.clear()
                        val result = JSONObject(response.toString())
                        val jArray: JSONArray = result.getJSONArray("Data")
//                        Log.e("Search_response :-", "$response")
                        var selectedModel: VehicleView? = null
                        if (response != null) {
                            for (i in 0 until jArray.length()) {
                                try {
                                    val `object` = jArray.getJSONObject(i)
                                    val vehicleModel =
                                        Gson().fromJson(
                                            `object`.toString(),
                                            VehicleView::class.java
                                        )
                                    if (i == 0) {
                                        selectedModel = vehicleModel
                                    }
                                    clusterManager?.addItem(vehicleModel)
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                    Log.e("Json_error:- ", e.message.toString())
                                }
                            }

                            if (searchLocate) {
                                searchLocate = false
                                if (jArray.length() > 0) {
                                    if (from.equals("M")) {
                                        mActivity?.runOnUiThread {
                                            mActivity?.let {
                                                Toasty.info(
                                                    it,
                                                    "${jArray.length()} results found."
                                                ).show()
                                            }
                                        }
                                    }
                                    if (selectedModel != null) {
                                        if (from.equals("V")) {
                                            initData(selectedModel)
                                        }
                                        selectedModel.latitude?.toDouble()?.let {
                                            selectedModel.longitude?.toDouble()
                                                ?.let { it1 -> LatLng(it, it1) }
                                        }
                                            ?.let {
                                                CameraUpdateFactory.newLatLngZoom(
                                                    it,
                                                    9.5f
                                                )
                                            }?.let {
                                                mMap?.moveCamera(
                                                    it
                                                )
                                            }
                                    }

                                } else {
                                    mActivity?.runOnUiThread {
                                        mActivity?.let {
                                            Toasty.info(
                                                it,
                                                "No results found."
                                            ).show()
                                        }
                                    }
                                }
                            }
                            clusterManager?.cluster()
//                            Log.e("Searched_size :- ", jArray.length().toString())
                        }
                    }

                    override fun onError(anError: ANError?) {
                        Log.e("Searched_error:- ", anError?.message.toString())
                    }

                })
            return true
        }
    }


}