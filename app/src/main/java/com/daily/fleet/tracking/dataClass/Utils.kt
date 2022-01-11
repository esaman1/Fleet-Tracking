package com.daily.fleet.tracking.dataClass

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.location.Address
import android.location.Geocoder
import com.daily.fleet.tracking.fragments.MapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*


class Utils {
    companion object {

        fun convertIntoKms(meter: Double): Double {
            val ff: Double = meter / 1000.0
            var bd: BigDecimal = BigDecimal.valueOf(ff)
            bd = bd.setScale(2, RoundingMode.HALF_UP)
            return bd.toDouble()
        }

        fun getAddress(latt1: String, long1: String): String {
            val addresses: List<Address>
            val geocoder: Geocoder = Geocoder(MapFragment.mActivity, Locale.getDefault())
            addresses = latt1.let {
                long1.let { it1 ->
                    geocoder.getFromLocation(
                        it.toDouble(),
                        it1.toDouble(),
                        1
                    )
                }
            } as List<Address>
            val address: String =
                addresses[0].getAddressLine(0)
            return address
        }

        fun getMarkerIconFromDrawable(drawable: Drawable): BitmapDescriptor {
            val canvas = Canvas()
            val bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            canvas.setBitmap(bitmap)
            drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
            drawable.draw(canvas)
            return BitmapDescriptorFactory.fromBitmap(bitmap)
        }

    }
}