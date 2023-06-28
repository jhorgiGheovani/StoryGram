package com.bangkit23.storygram.ui.dataModel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MapsDetailsModel (
    val id: String,
    val name: String,
    val lon: Float,
    val lat: Float
): Parcelable