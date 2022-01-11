package com.daily.fleet.tracking.adapters

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.daily.fleet.tracking.R
import com.daily.fleet.tracking.RerouteInterface
import com.daily.fleet.tracking.dataClass.Utils
import com.daily.fleet.tracking.models.VehicleView
import java.util.*


class VehicleAdapter(context1: Context?, anInterface: RerouteInterface?) :
    RecyclerView.Adapter<VehicleAdapter.MyClassView2>(), Filterable {
    var context: Context? = null
    var mainArrayList = ArrayList<VehicleView>()
    var anInterface: RerouteInterface? = null

    companion object {
        var arrayFilterList = ArrayList<VehicleView>()
    }

    init {
        this.context = context1
        this.anInterface = anInterface
        mainArrayList.clear()
        arrayFilterList.clear()
    }

    private val inflater: LayoutInflater = LayoutInflater.from(context1)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyClassView2 {
        val itemView = inflater.inflate(R.layout.vehicle_item, parent, false)
        return MyClassView2(itemView)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: MyClassView2, position: Int) {
        var vehicleClass = arrayFilterList.get(position)

        when (vehicleClass.status) {
            context?.resources?.getString(R.string.moving) ->
                holder.mImage.setImageDrawable(context?.resources?.getDrawable(R.drawable.ic_moving))
            context?.resources?.getString(R.string.idling) ->
                holder.mImage.setImageDrawable(context?.resources?.getDrawable(R.drawable.ic_idle))
            context?.resources?.getString(R.string.parked) ->
                holder.mImage.setImageDrawable(context?.resources?.getDrawable(R.drawable.ic_parked))
            context?.resources?.getString(R.string.offline) ->
                holder.mImage.setImageDrawable(context?.resources?.getDrawable(R.drawable.ic_offline))
            context?.resources?.getString(R.string.pending) ->
                holder.mImage.setImageDrawable(context?.resources?.getDrawable(R.drawable.ic_pending))
        }

        holder.tvVehicleNumber.text = vehicleClass.vehiclenumber
        holder.tvDestination.text = vehicleClass.latitude?.let {
            vehicleClass.longitude?.let { it1 ->
                Utils.getAddress(
                    it,
                    it1
                )
            }
        }
        holder.tvLastseen.text =
            "${context?.resources?.getString(R.string.last_seen_lbl)}  ${vehicleClass.lastseen}"

        holder.mainRL.setOnClickListener {
            vehicleClass.vehicleid?.let { Log.e("Clicked:", it.toString()) }
            anInterface?.onClick(vehicleClass)
        }
    }

    override fun getItemCount(): Int {
        return arrayFilterList.size
    }

    class MyClassView2(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mImage: ImageView
        var tvVehicleNumber: TextView
        var tvLastseen: TextView
        var tvDestination: TextView
        var mainRL: RelativeLayout

        init {
            mImage = itemView.findViewById(R.id.mImage)
            tvVehicleNumber = itemView.findViewById(R.id.tvVehicleNumber)
            tvLastseen = itemView.findViewById(R.id.tvLastseen)
            tvDestination = itemView.findViewById(R.id.tvDestination)
            mainRL = itemView.findViewById(R.id.mainRL)
        }
    }


    fun add(model: VehicleView?) {
        if (model != null) {
            mainArrayList.add(model)
        }
        notifyItemRangeInserted(mainArrayList.size - 1, 1)
    }

    fun newArray() {
        mainArrayList = ArrayList<VehicleView>()
        arrayFilterList = ArrayList<VehicleView>()
        mainArrayList.clear()
        arrayFilterList.clear()
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter? {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.equals("")) {
                    arrayFilterList = mainArrayList
                } else {
                    val filteredList1: ArrayList<VehicleView> = ArrayList<VehicleView>()
                    for (row in mainArrayList) {

                        if (row.vehiclenumber?.lowercase()
                                ?.contains(charString.lowercase()) == true
                        ) {
                            filteredList1.add(row)
                        }
                    }
                    arrayFilterList = filteredList1
                }
                val filterResults = FilterResults()
                filterResults.values = arrayFilterList
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                arrayFilterList = filterResults.values as ArrayList<VehicleView>
                notifyDataSetChanged()
            }
        }
    }


}