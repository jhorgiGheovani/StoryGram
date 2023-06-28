package com.bangkit23.storygram.data.remote.response

import com.google.gson.annotations.SerializedName

data class ListStoryWIthLocation(

    @field:SerializedName("lon")
    val lon: Double?=null,

    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("lat")
    val lat: Double?=null,

    @field:SerializedName("name")
    val name: String?=null,
)