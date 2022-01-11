package com.daily.fleet.tracking.fragments

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.daily.fleet.tracking.R
import com.daily.fleet.tracking.activities.DrawAllActivity
import com.daily.fleet.tracking.activities.HomeActivity.Companion.token
import com.daily.fleet.tracking.adapters.HistoryAdapter
import com.daily.fleet.tracking.dataClass.Utils
import com.daily.fleet.tracking.databinding.FragmentHistoryBinding
import com.daily.fleet.tracking.models.History
import com.daily.fleet.tracking.models.VehicleView
import com.google.gson.Gson
import es.dmoral.toasty.Toasty
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class HistoryFragment : Fragment() {

    companion object {
        var binding: FragmentHistoryBinding? = null
        var mActivity: Activity? = null
        var mainArrayList = ArrayList<History>()
        var historyAdapter: HistoryAdapter? = null
        var totalKm: Double = 0.0

        fun getHistory(vehicleView: VehicleView) {
            mainArrayList.clear()
            historyAdapter?.newArray()
            val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
            val currentDate = sdf.format(Date())
//            Log.e(" Current_date ", currentDate)

            val date = sdf.parse(currentDate)
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.add(Calendar.HOUR, -24)
//            Log.e(" Before_24_Hours: ", sdf.format(calendar.time))
            val beforeDate24 = sdf.format(calendar.time)

            AndroidNetworking.get("https://api.locanix.net/api/trips")
                .addHeaders("token", token)
                .addQueryParameter("vehicleid", vehicleView.vehicleid.toString())
                .addQueryParameter("from", currentDate)
                .addQueryParameter("to", beforeDate24)
                .setPriority(Priority.IMMEDIATE)
                .build()
                .getAsJSONObject(object :
                    JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject?) {
                        mainArrayList.clear()
                        val result = JSONObject(response.toString())
                        Log.e("History_response :-", response.toString())
                        if (result["Success"] == true) {
                            if (response != null) {
                                if (result["Data"].equals("[]")) {
                                    Log.e("History_success :-", "false")
                                } else {
                                    val jArray: JSONArray = result.getJSONArray("Data")
                                    binding?.lottie?.isVisible = false
                                    binding?.noData?.isVisible = false
                                    binding?.mRecyclerView?.isVisible = true
                                    for (i in 0 until jArray.length()) {
                                        try {
                                            val `object` = jArray.getJSONObject(i)
                                            val historyModel =
                                                Gson().fromJson(
                                                    `object`.toString(),
                                                    History::class.java
                                                )
                                            totalKm =
                                                historyModel.totalDistance?.let { totalKm.plus(it) }!!
                                            historyModel.totalDistance =
                                                historyModel.totalDistance?.let {
                                                    Utils.convertIntoKms(
                                                        it
                                                    )
                                                }
                                            Log.e(
                                                "History_TrackPoints :-",
                                                "${historyModel.trackPoints?.size}"
                                            )
                                            historyAdapter?.add(historyModel)
                                            mainArrayList.add(historyModel)

                                        } catch (e: JSONException) {
                                            e.printStackTrace()
                                            Log.e("Json_error:- ", e.message.toString())
                                        }
                                    }
                                    binding?.tvTotal?.isVisible = true
                                    binding?.tvTotal?.text =
                                        "${Utils.convertIntoKms(totalKm)} km . ${jArray.length()} trips"

                                }
                            } else {
                                binding?.noData?.isVisible = true
                                binding?.mRecyclerView?.isVisible = false
                                binding?.lottie?.isVisible = false
                            }
                        }
                    }

                    override fun onError(anError: ANError?) {
                        Log.e("History_error:- ", anError?.message.toString())
                        binding?.noData?.isVisible = true
                        binding?.mRecyclerView?.isVisible = false
                        binding?.lottie?.isVisible = false
                    }
                })
        }
    }

    private var refreshReceiver: RefreshReceiver? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_history, container, false)
        mActivity = requireActivity()
        refreshReceiver = RefreshReceiver()
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            refreshReceiver!!,
            IntentFilter("VEHICLE_HISTORY")
        )

        historyAdapter = HistoryAdapter(requireContext())
        binding?.mRecyclerView?.layoutManager =
            LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
        binding?.mRecyclerView?.adapter = historyAdapter
        historyAdapter?.newArray()

        binding?.mDrawAll?.setOnClickListener {
            if (mainArrayList.size > 0) {
                var intent = Intent(context, DrawAllActivity::class.java)
                intent.putExtra("historyList", mainArrayList)
                context?.startActivity(intent)
            } else {
                Toasty.info(requireContext(), "No records for draw !!!").show()
            }
        }

        return binding?.root
    }

    private class RefreshReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            binding?.lottie?.isVisible = true
            val model = intent?.getSerializableExtra("vehicleModel") as VehicleView
            model.vehiclenumber?.let { Log.e("history $it", "id: ${model.vehicleid}") }
            mActivity?.runOnUiThread { getHistory(model) }
        }
    }

}
