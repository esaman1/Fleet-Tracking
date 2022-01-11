package com.daily.fleet.tracking.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.daily.fleet.tracking.R
import com.daily.fleet.tracking.databinding.ActivityConnectingBinding

class ConnectingActivity : AppCompatActivity() {
    var binding: ActivityConnectingBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this@ConnectingActivity,
            R.layout.activity_connecting
        )
    }
}