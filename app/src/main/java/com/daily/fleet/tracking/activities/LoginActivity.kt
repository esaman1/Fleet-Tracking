package com.daily.fleet.tracking.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.daily.fleet.tracking.R
import com.daily.fleet.tracking.databinding.ActivityLoginBinding
import com.daily.fleet.tracking.models.Token
import com.daily.motivational.quotes2.dataClass.SharedPreference
import com.google.gson.Gson
import es.dmoral.toasty.Toasty
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    var binding: ActivityLoginBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@LoginActivity, R.layout.activity_login)
        AndroidNetworking.initialize(applicationContext)
        binding?.loginLL?.isVisible = false
        val token = SharedPreference.getLogin(this@LoginActivity)
//        Log.e("login:", token)
        if (!token.equals("null")) {
            var intent = Intent(this@LoginActivity, HomeActivity::class.java)
            startActivity(intent)
        } else {
            binding?.loginLL?.isVisible = true
        }
        binding?.mLogin?.setOnClickListener {
            val user = binding?.mUsername?.text
            val pswd = binding?.mPassword?.text
            if (user?.length == 0) {
                binding?.mUsername?.error = "Username is required"
            } else if (pswd?.length == 0) {
                binding?.mPassword?.error = "Password is required"
            } else {
                if (user?.isNotEmpty() == true || pswd?.isNotEmpty() == true) {
//                    Log.e("user: $user", "pswd: $pswd")
                    getToken(user.toString(), pswd.toString())
                } else {
                    Log.e("Missing Fields", "( username, password )")
                }
            }
        }
    }

    fun getToken(user: String, pswd: String) {
        AndroidNetworking.post("https://api.locanix.net/api/token")
            .addBodyParameter("username", user)
            .addBodyParameter("password", pswd)
            .setPriority(Priority.IMMEDIATE)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    if (response != null) {
//                        Log.e("Token_response ${response.length()}:-", "$response")
                        try {
                            val tokenModel =
                                Gson().fromJson(response.toString(), Token::class.java)

                            if (tokenModel?.token != null) {
                                SharedPreference.setLogin(
                                    this@LoginActivity,
                                    tokenModel.token.toString()
                                )
                                var intent =
                                    Intent(this@LoginActivity, HomeActivity::class.java)
                                startActivity(intent)
                            } else {
                                Toasty.info(
                                    this@LoginActivity,
                                    "Invalid User name or Password !!!"
                                ).show()
                            }

                        } catch (e: JSONException) {
                            e.printStackTrace()
                            Log.e("Json_error:- ", e.message.toString())
                        }
                    }
                }

                override fun onError(anError: ANError?) {
                    Log.e("Token_error:- ", anError?.message.toString())
                }

            })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}