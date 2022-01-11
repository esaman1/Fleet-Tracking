package com.daily.motivational.quotes2.dataClass

import android.content.Context

class SharedPreference {

    companion object {
        val MyPREFERENCES = "SPData"
        var LOGIN_TOKEN = "LOGIN_TOKEN"

        fun getLogin(c1: Context): String {
            val sharedpreferences = c1.getSharedPreferences(
                MyPREFERENCES,
                Context.MODE_PRIVATE
            )
            return sharedpreferences.getString(LOGIN_TOKEN, "null").toString()
        }

        fun setLogin(c1: Context, value: String) {
            val sharedpreferences =
                c1.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE)
            val editor = sharedpreferences.edit()
            editor.putString(LOGIN_TOKEN, value)
            editor.apply()
        }

    }
}