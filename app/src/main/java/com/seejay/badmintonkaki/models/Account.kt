package com.seejay.badmintonkaki.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Account (
    val email: String = "",
    var password: String = ""
): Parcelable

