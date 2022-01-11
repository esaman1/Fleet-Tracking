package com.daily.fleet.tracking.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.daily.fleet.tracking.R
import com.daily.fleet.tracking.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    var binding: ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)

        binding?.mConnect?.setOnClickListener {
            var intent = Intent(this@MainActivity, ConnectingActivity::class.java)
            startActivity(intent)
        }
    }
}