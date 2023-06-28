package com.bangkit23.storygram.data.remote.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class AutoLoginModel(
    var email: String?,
    var password: String?,
) : Parcelable