package com.daily.fleet.tracking.models

import com.google.gson.annotations.SerializedName

data class Token(

    @field:SerializedName("Error")
    val error: Any? = null,

    @field:SerializedName("Token")
    val token: String? = null,

    @field:SerializedName("Success")
    val success: Boolean? = null
)
