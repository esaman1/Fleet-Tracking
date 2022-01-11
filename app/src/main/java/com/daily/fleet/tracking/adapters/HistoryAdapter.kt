package com.daily.fleet.tracking.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.daily.fleet.tracking.R
import com.daily.fleet.tracking.activities.DrawingActivity
import com.daily.fleet.tracking.models.History
import es.dmoral.toasty.Toasty
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class HistoryAdapter(context1: Context?) :
    RecyclerView.Adapter<HistoryAdapter.MyClassView2>() {
    var context: Context? = null
    var mainArrayList = ArrayList<History>()


    init {
        this.context = context1
        mainArrayList.clear()
    }

    private val inflater: LayoutInflater = LayoutInflater.from(context1)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyClassView2 {
        val itemView = inflater.inflate(R.layout.history_item, parent, false)

        return MyClassView2(itemView)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: MyClassView2, position: Int) {
        var historyClass = mainArrayList.get(position)

        holder.tvTime.text = ""
        holder.tvKm.text = "${historyClass.totalDistance}  km"
        var startPoint = historyClass.startTrackPoint
        var endPoint = historyClass.endTrackPoint

        holder.tvSource.text = startPoint?.address
        holder.tvDestination.text = endPoint?.address

        val startIST = startPoint?.ist
        val endIST = endPoint?.ist

        val startTime = setDateFormat(startIST)
        val endTime = setDateFormat(endIST)

        holder.tvTime.text = "$startTime - $endTime"

        holder.historyRL.setOnClickListener {
            if (historyClass.trackPoints?.size!! > 0) {
                var intent = Intent(context, DrawingActivity::class.java)
                intent.putExtra("historyClass", historyClass)
                context?.startActivity(intent)
            } else {
                context?.let { it1 ->
                    Toasty.info(it1, "No routes to draw !!!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(ParseException::class)
    fun setDateFormat(unformattedDate: String?): String? {
        @SuppressLint("SimpleDateFormat") val dateformat =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(unformattedDate)
        return SimpleDateFormat("hh:mm a").format(dateformat)
    }

    override fun getItemCount(): Int {
        return mainArrayList.size
    }

    class MyClassView2(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTime: TextView
        var tvKm: TextView
        var tvSource: TextView
        var tvDestination: TextView
        var historyRL: RelativeLayout

        init {
            tvTime = itemView.findViewById(R.id.tvTime)
            tvKm = itemView.findViewById(R.id.tvKm)
            tvSource = itemView.findViewById(R.id.tvSource)
            tvDestination = itemView.findViewById(R.id.tvDestination)
            historyRL = itemView.findViewById(R.id.historyRL)
        }
    }


    fun add(model: History?) {
        if (model != null) {
            mainArrayList.add(model)
        }
//        Log.e("History_size :- ", mainArrayList.size.toString())
        notifyDataSetChanged()
    }

    fun newArray() {
        mainArrayList = ArrayList<History>()
        mainArrayList.clear()

        notifyDataSetChanged()
    }

}